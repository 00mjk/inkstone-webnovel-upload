package nyoibo.inkstone.upload.selenium.config;


import static org.openqa.selenium.Proxy.ProxyType.MANUAL;
import static org.openqa.selenium.remote.CapabilityType.PROXY;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


/**
 * <p>Title:DriverFactory.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-21 01:18
 */

public class DriverFactory {

    private RemoteWebDriver driver;
    private DriverType selectedDriverType;

    private final String operatingSystem = System.getProperty("os.name").toUpperCase();
    private final String systemArchitecture = System.getProperty("os.arch");
    private final boolean useRemoteWebDriver = Boolean.getBoolean("remoteDriver");
    private final boolean proxyEnabled = Boolean.getBoolean("proxyEnabled");
    private final String proxyHostname = System.getProperty("proxyHost");
    private final Integer proxyPort = Integer.getInteger("proxyPort");
    private final String proxyDetails = String.format("%s:%d", proxyHostname, proxyPort);

    private static final String CHROME_DRIVER_PATH = "src/main/resources/chromedriver.exe";
    
	public DriverFactory() {
		/*System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\Application\\chromedriver.exe");*/
		System.setProperty("webdriver.chrome.driver",
				CHROME_DRIVER_PATH);
		DriverType driverType = DriverType.CHROME;
	    
		String browser = System.getProperty("browser", driverType.toString()).toUpperCase();
		try {
			driverType = DriverType.valueOf(browser);
		} catch (IllegalArgumentException ignored) {
			System.err.println("Unknown driver specified, defaulting to '" + driverType + "'...");
		} catch (NullPointerException ignored) {
			System.err.println("No driver specified, defaulting to '" + driverType + "'...");
		}
		selectedDriverType = driverType;
	}

    public RemoteWebDriver getDriver() throws Exception {
        if (null == driver) {
            instantiateWebDriver(selectedDriverType);
        }
      
        return driver;
    }

    public RemoteWebDriver getStoredDriver() {
        return driver;
    }

    public void quitDriver() {
        if (null != driver) {
            driver.quit();
            driver = null;
        }
    }

    private void instantiateWebDriver(DriverType driverType) throws MalformedURLException {
        System.out.println(" ");
        System.out.println("Local Operating System: " + operatingSystem);
        System.out.println("Local Architecture: " + systemArchitecture);
        System.out.println("Selected Browser: " + selectedDriverType);
        System.out.println("Connecting to Selenium Grid: " + useRemoteWebDriver);
        System.out.println(" ");

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities = DesiredCapabilities.chrome();
        if (proxyEnabled) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(MANUAL);
            proxy.setHttpProxy(proxyDetails);
            proxy.setSslProxy(proxyDetails);
			desiredCapabilities.setCapability(PROXY, proxy);
		}

		if (useRemoteWebDriver) {
			URL seleniumGridURL = new URL(System.getProperty("gridURL"));
			String desiredBrowserVersion = System.getProperty("desiredBrowserVersion");
			String desiredPlatform = System.getProperty("desiredPlatform");

			if (null != desiredPlatform && !desiredPlatform.isEmpty()) {
				desiredCapabilities.setPlatform(Platform.valueOf(desiredPlatform.toUpperCase()));
			}

			if (null != desiredBrowserVersion && !desiredBrowserVersion.isEmpty()) {
				desiredCapabilities.setVersion(desiredBrowserVersion);
			}

			desiredCapabilities.setBrowserName(selectedDriverType.toString());
			driver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
		} else {
			driver = driverType.getWebDriverObject(desiredCapabilities);
		}
		desiredCapabilities.setCapability("pageLoadStrategy", "none");
		
	}
}

