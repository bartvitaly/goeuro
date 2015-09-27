package com.goeuro.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author bartvitaly
 *
 */
public class WebDriverCommon {

	protected final WebDriver driver;
	protected final Actions action;
	final static Logger logger = Logger.getLogger(WebDriverCommon.class);
	
	public WebDriverCommon(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		this.action = new Actions(driver);
		logger.info("Wait for page to load");
		waitForPageLoaded(driver);
	}

	public static void takeScreenshot(WebDriver driver) {
		String fileName = "\\test-output\\screenshot.png";
		logger.info("Taking screenshot.");
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile,
					new File((new File(".")).getCanonicalPath() + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void waitForPageLoaded(WebDriver driver) throws InterruptedException {
		Thread.sleep(5000);
		ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
		for (int i = 0; i < tabs.size(); i++) {	
			driver.switchTo().window(tabs.get(i));
			ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript(
							"return document.readyState").equals("complete");
				}
			};
			org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(
					driver, 30);
			try {
				wait.until(expectation);
			} catch (Throwable error) {
				Assert.assertFalse(
						"Timeout waiting for Page Load Request to complete.", true);
			}
		}
	}

	protected static WebElement waitForElement(WebDriver driver, final By by,
			int timeout) {

		org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(
				driver, timeout * 1000);

		driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);

		ExpectedCondition<WebElement> expectation = new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(by);
			}
		};

		try {
			return wait.until(expectation);
		} catch (Throwable error) {
			return null;
		}
	}

	protected static List<WebElement> waitForElements(WebDriver driver, final By by,
			int timeout) {

		org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(
				driver, timeout * 1000);

		driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);

		ExpectedCondition<List<WebElement>> expectation = new ExpectedCondition<List<WebElement>>() {
			public List<WebElement> apply(WebDriver driver) {
				return driver.findElements(by);
			}
		};

		try {
			return wait.until(expectation);
		} catch (Throwable error) {
			return null;
		}
	}
	
	protected static boolean waitForElementNotShown(WebDriver driver, final By by,
			int timeout) {

		org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(
				driver, timeout);

		driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return !driver.findElement(by).isDisplayed();
			}
		};

		try {
			return wait.until(expectation);
		} catch (Throwable error) {
			return false;
		}
	}

	/**
	 * This method is to click on element using WebElement or By object 
	 * @param obj
	 */
	protected void click(Object obj) {
		logger.info("Clicking an element '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		WebElement element = getElement(obj);
		try {
			action.moveToElement(element).build().perform();
			action.click(element).build().perform();
//			element.click();
		} catch (Exception e) {
			logger.error("An element '" + obj + "' was not clicked on page '" + driver.getCurrentUrl() + "'");
		}
	}

	/**
	 * This method is to click on element using WebElement or By object and wait for page to load
	 * @param obj
	 */
	protected void click_wait(Object obj) {
		click(obj);
		logger.info("Waiting for page to load '" + driver.getCurrentUrl() + "'");
		try {
			waitForPageLoaded(driver);
		} catch (Exception e) {
			logger.error("An element '" + obj + "' was not clicked on page '" + driver.getCurrentUrl() + "'");
		}
	}
	
	/**
	 * This method is to type text into element using WebElement or By object 
	 * @param obj
	 * @param text
	 */
	protected void type(Object obj, Object text) {
		text = String.valueOf(text);
		logger.info("Filling a text '" + text + "' into the field '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		WebElement element = getElement(obj);
		try {
			element.click();
			element.sendKeys((String) text);
		} catch (Exception e) {
			logger.error("A text '" + text + "' was not filled in the element '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		}
	}

	/**
	 * This method is to select an option in element using WebElement or By object
	 * @param by
	 * @param text
	 */
	protected void select(Object obj, String text) {
		logger.info("Selecting a value '" + text + "' from select field '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		WebElement element = getElement(obj);
		Select selectBox = new Select(element);
		selectBox.selectByValue(text);
	}

	protected String getText(Object obj) {
		logger.info("Getting text from element '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		WebElement element = getElement(obj);
		return element.getText().toString();
	}
	
	protected void select(By by, By byChild, String text) {
		click(by);
		click(byChild);
	}

	protected WebElement getElement(Object obj, int timeout)
	{	
		logger.info("Getting element '" + obj + "' on page '" + driver.getCurrentUrl() + "'");
		if (obj.getClass().getName().contains(By.class.getName()))
		{
			return waitForElement(driver, (By) obj, timeout);
		}
		return (WebElement) obj;
	}
	
	protected WebElement getElement(Object obj)
	{	
		return getElement(obj, 30);
	}

	protected WebElement getElement(By by, int timeout)
	{
		logger.info("Getting element '" + by + "' on page '" + driver.getCurrentUrl() + "'");
		return waitForElement(driver, by, timeout);
	}
	
	protected WebElement getElement(By by)
	{
		return getElement(by, 30);
	}
	
	protected List<WebElement> getElements(By by)
	{
		return waitForElements(driver, by, 30);
	}

	/**
	 * This method checks an attribute of an element
	 * @param element
	 * @param attribute
	 * @param value
	 * @return Boolean
	 */
	protected boolean checkAttribute(Object obj, String attribute, Object value, boolean throwError) {
		value = String.valueOf(value);
		logger.info("Verify an attribute '" + attribute + "' equals value '" + value + "' for an object '" + obj + "'");
		String attributeActual = getAttribute(obj, attribute);
		if (!(attributeActual.contains((String) value)) && throwError) { // || ((String) value).contains(attributeActual)
			logger.error("Verify verification of attribute '" + attribute + "' failed. Expected value '" + value + "', actual is '" + attributeActual + "' for an object '" + obj + "'");
			return false;
		}
		logger.info("Verify verification of attribute '" + attribute + "' passed. Expected value '" + value + "', actual is '" + attributeActual + "' for an object '" + obj + "'");
		return true;
	}
	
	/**
	 * This method retrieves an attribute of an element
	 * @param element
	 * @param attribute
	 * @return String
	 */
	protected String getAttribute(Object obj, String attribute) {
		WebElement element = getElement(obj);
		return element.getAttribute(attribute);
	}
	
}
