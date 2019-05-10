package nyoibo.inkstone.upload.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.AssignDriver;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.Query;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;

/**
 * <p>Title:InkstoneProjectPage.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-09 14:49
 */

public class InkstoneChapterPage {

	private final Query rawDiv;
	private final Query transBtn;
	private final Query conFirmTransBtn;

	private WebDriver driver;
	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneChapterPage.class);
	private final String bookUrl;

	private WebDriverWait wait;

	public InkstoneChapterPage(WebDriver driver, String bookUrl) throws Exception {
		this.driver = driver;
		this.rawDiv = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_PROJECT_RAW_DIV_CLASS + "']"));
		this.transBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_ID));
		this.conFirmTransBtn = new Query().defaultLocator(By.cssSelector("[class='"+SeleniumInkstone.INKSTONE_TRANSLATE_ALERT_CLASS+"']"));
		this.bookUrl = bookUrl;

		wait = new WebDriverWait(driver, 10);

		AssignDriver.initQueryObjects(this, DriverBase.getDriver());
	}

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}

	private WebElement switchTransDialog(WebDriverWait wait) {
		WebElement element = wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(
						By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_ALERT_CLASS + "']"));
			}
		});
		return element;
	}

	public void editLatestRaw() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("get to book chapters");

		driver.get(bookUrl);
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_DASHBOARD));
		driver.get(bookUrl);
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_CHAPTERS));

		String xpath = "//div[@class='" + SeleniumInkstone.INKSTONE_PROJECT_RAW_DIV_CLASS + "']/p/child::node()[1]";
		driver.findElement(By.xpath(xpath)).click();

		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_TRANSLATION));
		selectTranslate();
	}

	private void editChapter() {

	}

	private void toBookChapterViews() {

	}

	private void selectTranslate() {
		transBtn.findWebElement().click();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click translate button");
		switchTransDialog(wait);
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		conFirmTransBtn.findWebElement().click();
	}

	private void saveChapter() {

	}

}
