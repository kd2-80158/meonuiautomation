package com.ui.utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;

/** Keeps a test's screenshots, PDF preview, and step-replay video together. */
public final class JourneyEvidence {

	private static final int VIDEO_WIDTH = 1280;
	private static final int VIDEO_HEIGHT = 720;
	private static final DateTimeFormatter RUN_TIME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

	private final WebDriver driver;
	private final ExtentTest reportNode;
	private final Path directory;
	private final String relativeDirectory;
	private int frameCount;

	public JourneyEvidence(WebDriver driver, String testName) {
		this.driver = driver;
		ExtentTest parent = ExtentReporterUtility.getTest();
		this.reportNode = parent == null ? null : parent.createNode("Journey evidence - PDF generation");
		String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
		String runName = safeName + "_" + LocalDateTime.now().format(RUN_TIME);
		this.relativeDirectory = "evidence/" + runName;
		this.directory = Paths.get(System.getProperty("user.dir"), "logs", "evidence", runName);
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create journey evidence directory", e);
		}
	}

	public void capture(String stepName) {
		if (!(driver instanceof TakesScreenshot)) {
			throw new IllegalStateException("Browser does not support screenshots");
		}
		String imageName = String.format("step-%02d.png", frameCount + 1);
		Path screenshot = directory.resolve(imageName);
		try {
			Files.write(screenshot, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
			attachImage(stepName, imageName, screenshot);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot save evidence screenshot: " + stepName, e);
		}
	}

	/** Reopens the downloaded PDF, renders its first page, and attaches that image. */
	public void attachPdfPreview(Path pdf) {
		if (pdf == null || !Files.isRegularFile(pdf)) {
			throw new IllegalStateException("Verified PDF is not available for evidence");
		}
		Path preview = directory.resolve("pdf-first-page.png");
		try (PDDocument document = PDDocument.load(pdf.toFile())) {
			if (document.getNumberOfPages() < 1) {
				throw new IllegalStateException("Generated PDF has no pages");
			}
			BufferedImage firstPage = new PDFRenderer(document).renderImageWithDPI(0, 110);
			ImageIO.write(firstPage, "png", preview.toFile());
			if (reportNode != null) {
				reportNode.pass("PDF reopened successfully: " + document.getNumberOfPages()
						+ " pages, " + Files.size(pdf) + " bytes");
			}
			attachImage("Generated PDF - rendered first page", preview.getFileName().toString(), preview);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot reopen or render generated PDF", e);
		}
	}

	/** Render a verified cloud PDF directly from the browser response, without a Downloads folder. */
	public void attachPdfPreview(byte[] pdfBytes) {
		if (pdfBytes == null || pdfBytes.length == 0) {
			throw new IllegalStateException("Verified PDF response is not available for evidence");
		}
		Path preview = directory.resolve("pdf-first-page.png");
		try (PDDocument document = PDDocument.load(pdfBytes)) {
			if (document.getNumberOfPages() < 1) {
				throw new IllegalStateException("Generated PDF has no pages");
			}
			BufferedImage firstPage = new PDFRenderer(document).renderImageWithDPI(0, 110);
			ImageIO.write(firstPage, "png", preview.toFile());
			if (reportNode != null) {
				reportNode.pass("PDF response opened successfully: " + document.getNumberOfPages()
						+ " pages, " + pdfBytes.length + " bytes");
			}
			attachImage("Generated PDF - rendered first page", preview.getFileName().toString(), preview);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot reopen or render generated PDF response", e);
		}
	}

	private void attachImage(String label, String fileName, Path image) throws IOException {
		if (reportNode != null) {
			reportNode.info("Step " + (frameCount + 1) + ": " + label);
			reportNode.addScreenCaptureFromPath(relativeDirectory + "/" + fileName, label);
		}
		writeVideoFrame(image, label);
	}

	private void writeVideoFrame(Path image, String label) throws IOException {
		BufferedImage source = ImageIO.read(image.toFile());
		if (source == null) {
			throw new IOException("Unsupported evidence image: " + image);
		}
		BufferedImage frame = new BufferedImage(VIDEO_WIDTH, VIDEO_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = frame.createGraphics();
		try {
			graphics.setColor(new Color(20, 26, 39));
			graphics.fillRect(0, 0, VIDEO_WIDTH, VIDEO_HEIGHT);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			int availableHeight = VIDEO_HEIGHT - 80;
			double scale = Math.min((double) VIDEO_WIDTH / source.getWidth(),
					(double) availableHeight / source.getHeight());
			int width = (int) Math.round(source.getWidth() * scale);
			int height = (int) Math.round(source.getHeight() * scale);
			graphics.drawImage(source, (VIDEO_WIDTH - width) / 2, 0, width, height, null);
			graphics.setColor(new Color(29, 45, 73));
			graphics.fillRect(0, availableHeight, VIDEO_WIDTH, 80);
			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("SansSerif", Font.BOLD, 23));
			FontMetrics metrics = graphics.getFontMetrics();
			String caption = String.format("%02d  %s", frameCount + 1, label);
			while (metrics.stringWidth(caption) > VIDEO_WIDTH - 48 && caption.length() > 4) {
				caption = caption.substring(0, caption.length() - 2) + "…";
			}
			graphics.drawString(caption, 24, availableHeight + 49);
		} finally {
			graphics.dispose();
		}
		ImageIO.write(frame, "png", directory.resolve(String.format("frame-%03d.png", frameCount)).toFile());
		frameCount++;
	}

	/** Builds a narrated-by-captions step replay; failures are visible in the report. */
	public void finish() {
		if (frameCount == 0) {
			return;
		}
		Path video = directory.resolve("journey.mp4");
		Path encodeLog = directory.resolve("video-encode.log");
		try {
			Process process = new ProcessBuilder("ffmpeg", "-hide_banner", "-loglevel", "error", "-y",
					"-framerate", "1/2", "-i", "frame-%03d.png", "-c:v", "libx264",
					"-pix_fmt", "yuv420p", "journey.mp4")
					.directory(directory.toFile()).redirectErrorStream(true).redirectOutput(encodeLog.toFile()).start();
			if (!process.waitFor(60, TimeUnit.SECONDS)) {
				process.destroyForcibly();
				throw new IOException("Video encoder timed out");
			}
			if (process.exitValue() != 0 || !Files.isRegularFile(video) || Files.size(video) == 0) {
				throw new IOException("Video encoder failed; see " + encodeLog);
			}
			if (reportNode != null) {
				String videoUrl = relativeDirectory + "/journey.mp4";
				reportNode.info("Journey video (checkpoint replay, 2 seconds per step): "
						+ "<a href='" + videoUrl + "' target='_blank'>Open/download MP4</a>");
				reportNode.info("<video controls preload='metadata' width='800' src='" + videoUrl
						+ "'>Your browser cannot play this video.</video>");
			}
		} catch (IOException e) {
			if (reportNode != null) {
				reportNode.warning("Video unavailable; screenshots remain attached. " + e.getMessage());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			if (reportNode != null) {
				reportNode.warning("Video encoding interrupted; screenshots remain attached");
			}
		}
	}
}
