package com.ui.utilities;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Selects a WebDriver element in Chrome's docked Elements panel via CDP. */
public final class ChromeDevToolsInspector {
    private ChromeDevToolsInspector() { }

    public static boolean selectInElementsPanel(WebDriver driver, WebElement element) {
        if (!(driver instanceof RemoteWebDriver)) return false;
        RemoteWebDriver remote = (RemoteWebDriver) driver;
        try {
            Object options = remote.getCapabilities().getCapability("goog:chromeOptions");
            if (!(options instanceof Map<?, ?>) || ((Map<?, ?>) options).get("debuggerAddress") == null) return false;
            Map<?, ?> map = (Map<?, ?>) options;
            String marker = "mandot-" + Long.toUnsignedString(System.nanoTime(), 36);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].setAttribute('data-mandot-devtools-target', arguments[1]);", element, marker);
            String address = String.valueOf(map.get("debuggerAddress"));
            JsonArray targets = JsonParser.parseString(get("http://" + address + "/json/list")).getAsJsonArray();
            String currentUrl = driver.getCurrentUrl();
            String websocket = null;
            for (var target : targets) {
                JsonObject candidate = target.getAsJsonObject();
                if (candidate.has("type") && candidate.has("url") && candidate.has("webSocketDebuggerUrl")
                        && "page".equals(candidate.get("type").getAsString())
                        && currentUrl.equals(candidate.get("url").getAsString())) {
                    websocket = candidate.get("webSocketDebuggerUrl").getAsString();
                    break;
                }
            }
            if (websocket == null || websocket.isBlank()) return false;
            CompletableFuture<WebSocket> connection = HttpClient.newHttpClient().newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(5)).buildAsync(URI.create(websocket), new WebSocket.Listener() { });
            WebSocket socket = connection.get(6, TimeUnit.SECONDS);
            String selector = "[data-mandot-devtools-target=\\\"" + marker + "\\\"]";
            String expression = "inspect(document.querySelector('" + selector + "'))";
            JsonObject command = new JsonObject();
            command.addProperty("id", 1);
            command.addProperty("method", "Runtime.evaluate");
            JsonObject parameters = new JsonObject();
            parameters.addProperty("expression", expression);
            parameters.addProperty("includeCommandLineAPI", true);
            command.add("params", parameters);
            socket.sendText(command.toString(), true).get(5, TimeUnit.SECONDS);
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "done").get(3, TimeUnit.SECONDS);
            Thread.sleep(450L);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String get(String url) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();
    }
}
