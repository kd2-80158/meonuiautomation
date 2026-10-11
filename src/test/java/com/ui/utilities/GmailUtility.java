package com.ui.utilities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class GmailUtility {

	private static final String APPLICATION_NAME = "Meon UI Automation";
	private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY = "tokens";
	private static final String CREDENTIALS_FILE = "credentials.json";

	private static Gmail getService() throws Exception {

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(new FileInputStream(CREDENTIALS_FILE)));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
				Collections.singleton(GmailScopes.GMAIL_READONLY))
				.setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY).toFile()))
				.setAccessType("offline").build();

		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");

		return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static String getLatestEmailBody() throws Exception {

		Gmail service = getService();

		List<Message> messages = service.users().messages().list("me").setMaxResults(1L).execute().getMessages();

		if (messages == null || messages.isEmpty()) {
			throw new RuntimeException("No emails found in inbox");
		}

		Message message = service.users().messages().get("me", messages.get(0).getId()).execute();

		return extractBody(message.getPayload());
	}

	private static String extractBody(MessagePart part) {

		if (part.getBody() != null && part.getBody().getData() != null) {
			return new String(Base64.getUrlDecoder().decode(part.getBody().getData()));
		}

		if (part.getParts() != null) {
			for (MessagePart subPart : part.getParts()) {
				String result = extractBody(subPart);
				if (!result.isEmpty()) {
					return result;
				}
			}
		}
		return "";
	}
}
