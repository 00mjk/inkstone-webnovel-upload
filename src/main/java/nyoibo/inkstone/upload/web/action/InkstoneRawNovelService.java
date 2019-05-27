package nyoibo.inkstone.upload.web.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.gui.SWTResourceManager;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.WebDriverUtils;
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

public class InkstoneRawNovelService implements Runnable{
	private final WebDriver driver;
	private final InkstoneHomePage inkstoneHomePage;
	private final InkstoneChapterPage inkstoneChapterPage;

	private final boolean needLogin;

	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneRawNovelService.class);

	private Thread homePageThread;
	private Thread chapterThread;
	
	
	public InkstoneRawNovelService(boolean foreign, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process, Map<String, String> bookCompareList, boolean needLogin,WebDriver driver,
			Map<String,String> chapterFileList)
			throws Exception {
		this.driver = driver;
		this.needLogin = needLogin;
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver, bookName);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl, bookName, bookCompareList, foreign,
				chapterFileList);
	}

	@Override
	public void run() {
		SWTResourceManager.lock.lock();
		
		homePageThread = new Thread(inkstoneHomePage);
		homePageThread.setDaemon(true);
		chapterThread = new Thread(inkstoneChapterPage);
		chapterThread.setDaemon(true);
		
		if (needLogin) {
			try {
				homePageThread.start();
				homePageThread.join();
				Thread.sleep(3000);
				chapterThread.start();
				chapterThread.join();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SWTResourceManager.lock.unlock();
			}

		} else {
			try {
				Thread.sleep(2000);
				driver.get(SeleniumInkstone.INKSTONE_PRO_DASHBOARD);

				WebDriverUtils.waitPageLoadComplete(new WebDriverWait(driver, 10));
				Thread.sleep(2000);
				LOGGER.begin().headerAction(MessageMethod.EVENT).info("start to do next chapter, loop to dashboard");

				WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, new WebDriverWait(driver, 10, 1000));
				chapterThread.start();
				chapterThread.join();
				Thread.sleep(2000);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SWTResourceManager.lock.unlock();
			}
		}
		Thread.currentThread().interrupt();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("raw main thread kill complete");

	}
}
