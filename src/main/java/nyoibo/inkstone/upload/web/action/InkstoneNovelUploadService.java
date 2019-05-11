package nyoibo.inkstone.upload.web.action;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.web.pages.InkstoneChapterPage;
import nyoibo.inkstone.upload.web.pages.InkstoneHomePage;

/**
 * <p>Title:InkstoneNovelUploadService.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-08 11:00
 */

public class InkstoneNovelUploadService {
	private final WebDriver driver;
	private final InkstoneHomePage inkstoneHomePage;
	private final InkstoneChapterPage inkstoneChapterPage;

	private WebDriverWait wait;

	public InkstoneNovelUploadService(boolean foreign, String bookUrl) throws Exception {
		DriverBase.instantiateDriverObject();
		this.driver = DriverBase.getDriver();
		
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl);
	}

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}

	public void start() throws Exception {
		inkstoneHomePage.login();
		inkstoneChapterPage.editLatestRaw();

	}
	
	public static void main(String[] args) {
		InkstoneNovelUploadService test = null;
		try {
			String bookUrl = "https://inkstone.webnovel.com/book/detail/cbid/8628176105001205";
			test = new InkstoneNovelUploadService(false, bookUrl);
			test.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//test.driver.quit();
		}
	}
}
