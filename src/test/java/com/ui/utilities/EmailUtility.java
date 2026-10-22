package com.ui.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailUtility {

	private static Properties loadEmailProps() {
		Properties p = new Properties();
		try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/config/email.properties")) {
			p.load(fis);
		} catch (Exception e) {
			System.out.println("email.properties not found, using env variables if available");
		}
		return p;
	}

	private static String getPropOrEnv(Properties p, String key, String envKey, String defaultValue) {
		String env = System.getenv(envKey);
		if (env != null && !env.isBlank())
			return env;
		String val = p.getProperty(key);
		return val == null ? defaultValue : val;
	}

	public static String loadRecipientsFromPropsOrDefault() {
		String envRecipients = System.getenv("MAIL_RECIPIENTS");
		if (envRecipients != null && !envRecipients.trim().isEmpty()) {
			return envRecipients;
		}
		try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/config/email.properties")) {

			Properties p = new Properties();
			p.load(fis);
			String propertyRecipients = p.getProperty("mail.recipients");

			if (propertyRecipients != null && !propertyRecipients.trim().isEmpty()) {
				return propertyRecipients;
			}
		} catch (Exception ignored) {
		}
		return "qa-team@example.com";
	}

	public static void sendReportEmail(String subject, String htmlBody, String attachmentPath, String recipientsCsv) {

		try {
			Properties p = loadEmailProps();

			final String host = getPropOrEnv(p, "smtp.host", "SMTP_HOST", "smtp.gmail.com");
			final String port = getPropOrEnv(p, "smtp.port", "SMTP_PORT", "587");
			final String username = getPropOrEnv(p, "smtp.username", "SMTP_USERNAME", null);
			final String password = getPropOrEnv(p, "smtp.password", "SMTP_PASSWORD", null);
			final String from = getPropOrEnv(p, "smtp.from", "SMTP_FROM", username);

			System.out.println("---- EMAIL CONFIG ----");
			System.out.println("Host: " + host);
			System.out.println("Port: " + port);
			System.out.println("User: " + username);
			System.out.println("Recipients: " + recipientsCsv);
			System.out.println("----------------------");

			Properties props = new Properties();

			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");

			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");

			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			//session.setDebug(true);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			List<String> recipients = Arrays.stream(recipientsCsv.split(",")).map(String::trim)
					.filter(s -> !s.isEmpty()).collect(Collectors.toList());

			for (String r : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(r));
			}

			message.setSubject(subject);
			message.setSentDate(new Date());

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlBody == null ? "" : htmlBody, "text/html; charset=utf-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(htmlPart);

			if (attachmentPath != null && !attachmentPath.isBlank()) {

				File file = new File(attachmentPath);

				if (file.exists() && file.isFile()) {
					long fileSizeMB = file.length() / (1024 * 1024);
					if (fileSizeMB > 10) {
						System.out.println("Attachment too large (" + fileSizeMB + "MB), skipping attachment.");
					} else {
						MimeBodyPart attachPart = new MimeBodyPart();
						DataSource source = new FileDataSource(file);
						attachPart.setDataHandler(new DataHandler(source));
						attachPart.setFileName(file.getName());
						multipart.addBodyPart(attachPart);
					}
				} else {
					System.out.println("Attachment not found: " + attachmentPath);
				}
			}
			message.setContent(multipart);
			Transport.send(message);
			System.out.println("Email sent successfully to: " + recipientsCsv);
		} catch (Exception e) {
			System.err.println("EMAIL FAILED");
			e.printStackTrace(); // FULL STACKTRACE (VERY IMPORTANT)
			throw new RuntimeException("Failed to send email", e);
		}
	}
}