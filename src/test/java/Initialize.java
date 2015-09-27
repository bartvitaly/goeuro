package test.java;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.goeuro.common.PropertiesUtils;
import com.goeuro.common.WebDriverCommon;

/**
 * 
 * @author bartvitaly
 *
 */
public class Initialize {

	public WebDriver driver;
	final static Logger logger = Logger.getLogger(Initialize.class);
	
	@BeforeMethod(groups = { "demo" })
	public WebDriver init() {

		BasicConfigurator.configure();
		String logLevel = PropertiesUtils.getProperty("log.level");
		Logger.getRootLogger().setLevel(Level.toLevel(logLevel));
		
		String browser = PropertiesUtils.getProperty("browser");
		String chromeDriver = PropertiesUtils.getProperty("webdriver.chrome.driver");

		if (browser.equals("chrome")) {
			File file = new File(chromeDriver);
			System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
			driver = new ChromeDriver();
		} else if (browser.equals("firefox")) {
			driver = new FirefoxDriver();
		} else {
			driver = new HtmlUnitDriver();
		}

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		return driver;

	}

	@AfterMethod(groups = { "demo" })
	public void tear() {
		WebDriverCommon.takeScreenshot(driver);
//		driver.close();
//		driver.quit();
	}

}
