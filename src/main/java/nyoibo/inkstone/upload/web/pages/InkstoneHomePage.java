package nyoibo.inkstone.upload.web.pages;

import static com.lazerycode.selenium.util.AssignDriver.initQueryObjects;

import org.openqa.selenium.By;

import com.lazerycode.selenium.util.Query;

import nyoibo.inkstone.upload.selenium.DriverBase;

/**
 * <p>Title:InkstoneHomePage.java</p>  
 * <p>Description:InkstoneHomePage </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-21 01:48
 */

public class InkstoneHomePage {
	private Query searchBar = new Query().defaultLocator(By.name("q"));
	private Query googleSearch = new Query().defaultLocator(By.name("btnK"));
	private Query imFeelingLucky = new Query().defaultLocator(By.name("btnI"));

	public InkstoneHomePage() throws Exception {
		initQueryObjects(this, DriverBase.getDriver());
	}

	public InkstoneHomePage enterSearchTerm(String searchTerm) {
		searchBar.findWebElement().clear();
		searchBar.findWebElement().sendKeys(searchTerm);

		return this;
	}

	public InkstoneHomePage submitSearch() {
		googleSearch.findWebElement().submit();

		return this;
	}

	public void getLucky() {
		imFeelingLucky.findWebElement().click();
	}
}