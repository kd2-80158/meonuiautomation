package com.ui.product.rekyc.pages.admin.meontechnologies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.openqa.selenium.By;
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
		WebElement dropdown = new WebDriverWait(getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(WORKFLOW_DROPDOWN));
		new Select(dropdown).selectByVisibleText(workflow);
		return this;
	}

	public MakerCheckerPage_MeonTechnologies clickGeneratePdfForClient(String clientCode) {
		By generatePdfButton = By.xpath(
				"//tr[td[normalize-space()='" + clientCode + "']]" + "//button[normalize-space()='Generate PDF']");
		WebElement button = new WebDriverWait(getDriver(), Duration.ofSeconds(20))
				.until(ExpectedConditions.elementToBeClickable(generatePdfButton));
		button.click();
		return this;
	}

	public boolean isPdfDownloadedAndOpenable(String downloadDirectory, String expectedFileNamePart,
			long downloadStartTime) {

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

				return true;
			}

		} catch (Exception e) {
			logger.error("PDF download or validation failed: {}", e.getMessage());
			return false;
		}
	}

}
