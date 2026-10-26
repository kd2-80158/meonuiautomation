package com.ui.product.rekyc.pages.admin.meontechnologies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.ui.utilities.BrowserUtility;
import com.ui.utilities.LoggerUtility;

@Listeners({ com.ui.listeners.TestListener.class })
public class MakerCheckerPage_MeonTechnologies extends BrowserUtility {

	Logger logger = LoggerUtility.getLogger(this.getClass());
	private Path lastVerifiedPdf;
	private byte[] lastVerifiedPdfBytes;

	public MakerCheckerPage_MeonTechnologies(WebDriver driver) {
		super(driver);
	}

	private static final By WORKFLOW_DROPDOWN = By.xpath("//select[@id='mis_workflow']");

	private static final By ANNUALINCOME_OPTION_WORKFLOW = By.xpath("//option[text()='annual_income_modification']");

	private static final By PAGE_HEADING = By.xpath("//h2[@class='page-title']");

	private static final By VERIFY_MAKERCHEKER_BUTTON_SIDENAVIGATIONBAR = By
			.xpath("//button[text()='Verify' and @class='workflowInnerBtn']");

	private static final By GENERATEPDFBTN_MAKERCHECKER = By
			.xpath("//button[text()='Generate PDF' and @class='btn btn-info']");

	private static final By MAKER_CHECKER_MENU = By.cssSelector("a[href='#general-pages']");

	private static final By MAKER_CHECKER_SUBMENU = By.cssSelector("#general-pages.collapse.show");

	private static final By VERIFY_BUTTON = By
			.cssSelector("#general-pages button.workflowInnerBtn[data-type='verify']");

	public MakerCheckerPage_MeonTechnologies clickOn(String text) {
		By locator;
		switch (text.toLowerCase()) {
		case "workflow_dd":
			locator = WORKFLOW_DROPDOWN;
			break;
		case "aim_option_workflow":
			locator = ANNUALINCOME_OPTION_WORKFLOW;
			break;
		case "verify_btn_mc":
			locator = VERIFY_MAKERCHEKER_BUTTON_SIDENAVIGATIONBAR;
			break;
		case "generate_pdf_btn":
			locator = GENERATEPDFBTN_MAKERCHECKER;
			break;
		default:
			throw new IllegalArgumentException("Unable to find the locator " + text);
		}
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(locator));
		option.click();
		return this;
	}

	public boolean isDisplayed() {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(PAGE_HEADING));
		if (option.getText().equalsIgnoreCase("Maker-Checker"))
			return true;
		return false;
	}

	public MakerCheckerPage_MeonTechnologies selectWorkflow(String workflow) {
		WebDriverWait selectionWait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		selectionWait.pollingEvery(Duration.ofMillis(250));
		selectionWait.until(driver -> {
			WebElement dropdown = ExpectedConditions.elementToBeClickable(WORKFLOW_DROPDOWN).apply(driver);
			if (dropdown == null || !isUnobstructed(driver, dropdown)) {
				return false;
			}
			try {
				Select select = new Select(dropdown);
				select.selectByVisibleText(workflow);
				return workflow.equals(select.getFirstSelectedOption().getText().trim());
			} catch (ElementClickInterceptedException | StaleElementReferenceException e) {
				return false;
			}
		});
		return this;
	}

	public MakerCheckerPage_MeonTechnologies clickGeneratePdfForClient(String clientCode) {
		By generatePdfButton = By.xpath(
				"//tr[td[normalize-space()='" + clientCode + "']]" + "//button[normalize-space()='Generate PDF']");
		clickWhenUnobstructed(generatePdfButton);
		return this;
	}

	/** Observe the existing AJAX and fetch calls without regenerating the PDF. */
	public void prepareGeneratedPdfCapture() {
		((JavascriptExecutor) getDriver()).executeScript(
				"window.__meonPdfResult = null; window.__meonPdfFetches = [];"
				+ "jQuery(document).off('ajaxComplete.meonPdfTest').on('ajaxComplete.meonPdfTest', function(e,xhr,settings) {"
				+ "if (settings.url && settings.url.indexOf('/v1/company/generate_pdf/') !== -1) {"
				+ "window.__meonPdfResult = {httpStatus:xhr.status, body:xhr.responseJSON || null}; }});"
				+ "if (!window.__meonOriginalFetch) { window.__meonOriginalFetch = window.fetch;"
				+ "window.fetch = function() { var args = arguments; return window.__meonOriginalFetch.apply(this,args).then(function(response) {"
				+ "var url = String(args[0] && args[0].url ? args[0].url : args[0]);"
				+ "if (!(/\\.pdf(?:\\?|$)|download_pdf|generate_pdf/i.test(url) || /pdf/i.test(response.headers.get('content-type') || ''))) return response;"
				+ "var copy = response.clone(); copy.blob().then(function(blob) {"
				+ "if (blob.size > 15000000) return;"
				+ "var reader = new FileReader(); reader.onload = function() {"
				+ "window.__meonPdfFetches.push({url:url, httpStatus:response.status, mime:blob.type, size:blob.size,"
				+ "base64:String(reader.result).split(',')[1] || ''}); }; reader.readAsDataURL(blob);"
				+ "}).catch(function(){}); return response; }); }; }");
	}

	/** Confirm the generation API and reopen every PDF returned by the page's own fetch. */
	@SuppressWarnings("unchecked")
	public boolean isGeneratedPdfResponseOpenable() {
		lastVerifiedPdfBytes = null;
		try {
			Map<String, Object> result = new WebDriverWait(getDriver(), Duration.ofSeconds(60)).until(driver -> {
				Object value = ((JavascriptExecutor) driver).executeScript("return window.__meonPdfResult;");
				return value instanceof Map ? (Map<String, Object>) value : null;
			});
			if (((Number) result.get("httpStatus")).intValue() != 200) {
				logger.error("Generate PDF API returned HTTP {}", result.get("httpStatus"));
				return false;
			}
			Map<String, Object> body = (Map<String, Object>) result.get("body");
			if (body == null || !Boolean.TRUE.equals(body.get("status")) || !(body.get("data") instanceof List)) {
				logger.error("Generate PDF API did not report successful PDF generation");
				return false;
			}
			List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("data");
			if (documents.isEmpty()) {
				logger.error("Generate PDF API returned no documents");
				return false;
			}
			for (Map<String, Object> document : documents) {
				if (!(document.get("url") instanceof String) || ((String) document.get("url")).isEmpty()) {
					logger.error("Generate PDF API returned a document without a URL");
					return false;
				}
				String url = (String) document.get("url");
				Map<String, Object> proof = new WebDriverWait(getDriver(), Duration.ofSeconds(60)).until(driver -> {
					Object value = ((JavascriptExecutor) driver).executeScript(
							"return (window.__meonPdfFetches || []).find(p => new URL(p.url, location.href).href === new URL(arguments[0], location.href).href) || null;", url);
					return value instanceof Map ? (Map<String, Object>) value : null;
				});
				if (((Number) proof.get("httpStatus")).intValue() != 200) {
					logger.error("Generated PDF fetch returned HTTP {}", proof.get("httpStatus"));
					return false;
				}
				byte[] bytes = Base64.getDecoder().decode(String.valueOf(proof.get("base64")));
				if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D'
						|| bytes[3] != 'F' || bytes[4] != '-') {
					logger.error("Generated response is not a PDF document");
					return false;
				}
				try (PDDocument pdf = PDDocument.load(bytes)) {
					if (pdf.getNumberOfPages() < 1) {
						logger.error("Generated PDF has no pages");
						return false;
					}
					logger.info("Generated PDF verified from browser response: {} pages, {} bytes",
							pdf.getNumberOfPages(), bytes.length);
					lastVerifiedPdfBytes = bytes;
				}
			}
			return true;
		} catch (Exception error) {
			logger.error("Generated PDF response could not be verified: {}", error.toString());
			return false;
		}
	}

	public byte[] getLastVerifiedPdfBytes() {
		return lastVerifiedPdfBytes;
	}

	public boolean isPdfDownloadedAndOpenable(String downloadDirectory, String expectedFileNamePart,
			long downloadStartTime) {
		lastVerifiedPdf = null;

		Path downloadPath = Paths.get(downloadDirectory);

		if (!Files.isDirectory(downloadPath)) {
			logger.error("Download directory does not exist: {}", downloadPath);
			return false;
		}

		try {
			FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
					.pollingEvery(Duration.ofMillis(500));

			Function<WebDriver, Path> findPdf = driver -> {

				try (Stream<Path> files = Files.list(downloadPath)) {

					return files.filter(Files::isRegularFile).filter(path -> {
						String name = path.getFileName().toString();

						return name.toLowerCase().endsWith(".pdf") && name.contains(expectedFileNamePart);
					}).filter(path -> {
						try {
							return Files.getLastModifiedTime(path).toMillis() >= downloadStartTime;
						} catch (IOException e) {
							return false;
						}
					}).filter(path -> {
						try {
							if (Files.size(path) <= 0) {
								return false;
							}

							Path partialFile = path.resolveSibling(path.getFileName().toString() + ".part");

							return !Files.exists(partialFile);

						} catch (IOException e) {
							return false;
						}
					}).findFirst().orElse(null);

				} catch (IOException e) {
					logger.debug("Waiting for PDF: {}", e.getMessage());
					return null;
				}
			};

			Path pdfFile = wait.until(findPdf);

			if (pdfFile == null) {
				return false;
			}

			logger.info("Downloaded PDF found: {}", pdfFile);

			try (PDDocument pdf = PDDocument.load(pdfFile.toFile())) {

				int pageCount = pdf.getNumberOfPages();

				if (pageCount <= 0) {
					logger.error("PDF has no pages");
					return false;
				}

				logger.info("PDF opened successfully. Pages: {}, Size: {} bytes", pageCount, Files.size(pdfFile));
				lastVerifiedPdf = pdfFile;

				return true;
			}

		} catch (Exception e) {
			logger.error("PDF download or validation failed: {}", e.getMessage());
			return false;
		}
	}

	public Path getLastVerifiedPdf() {
		return lastVerifiedPdf;
	}

	public MakerCheckerPage_MeonTechnologies openMakerCheckerVerify() {

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

		By menuLocator = MAKER_CHECKER_MENU;

		By submenuLocator = MAKER_CHECKER_SUBMENU;

		By verifyLocator = VERIFY_BUTTON;

		logger.info("Current URL: {}", getDriver().getCurrentUrl());
		logger.info("Page title: {}", getDriver().getTitle());

		int menuCount = getDriver().findElements(menuLocator).size();

		logger.info("Maker & Checker locator matches: {}", menuCount);

		if (menuCount == 0) {
			throw new IllegalStateException(
					"Maker & Checker menu missing from current tab. " + "URL: " + getDriver().getCurrentUrl());
		}

		WebElement menu = getDriver().findElement(menuLocator);

		logger.info("Menu displayed: {}", menu.isDisplayed());
		logger.info("Menu enabled: {}", menu.isEnabled());
		logger.info("Menu expanded: {}", menu.getAttribute("aria-expanded"));

		if (!menu.isDisplayed()) {
			throw new IllegalStateException(
					"Maker & Checker exists but sidebar is hidden. " + "Check responsive/offcanvas navigation.");
		}

		if (!"true".equalsIgnoreCase(menu.getAttribute("aria-expanded"))) {
			clickWhenUnobstructed(menuLocator);
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(submenuLocator));

		logger.info("Maker & Checker expanded successfully");

		clickWhenUnobstructed(verifyLocator);

		logger.info("Verify clicked successfully");

		wait.until(ExpectedConditions.visibilityOfElementLocated(WORKFLOW_DROPDOWN));

		logger.info("Workflow dropdown visible");

		return this;
	}

	private void clickWhenUnobstructed(By locator) {
		WebDriverWait clickWait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
		clickWait.pollingEvery(Duration.ofMillis(250));
		clickWait.until(driver -> {
			WebElement element = ExpectedConditions.elementToBeClickable(locator).apply(driver);
			if (element == null) {
				return false;
			}
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);

			if (!isUnobstructed(driver, element)) {
				return false;
			}

			try {
				element.click();
				return true;
			} catch (ElementClickInterceptedException | StaleElementReferenceException e) {
				return false;
			}
		});
	}

	private boolean isUnobstructed(WebDriver driver, WebElement element) {
		return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
				"const r = arguments[0].getBoundingClientRect();"
						+ "const top = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);"
						+ "return top === arguments[0] || arguments[0].contains(top);",
				element));
	}

}
