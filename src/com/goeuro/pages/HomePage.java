package com.goeuro.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.goeuro.common.Common;
import com.goeuro.common.WebDriverCommon;
import com.goeuro.items.RoutePlan;

/**
 * This class describes a home page of the web site
 * and page elements	
 * @author bartvitaly
 * @version 1.1
 */
public class HomePage extends WebDriverCommon {

	private final WebDriver driver;
	final static Logger logger = Logger.getLogger(HomePage.class);
	
	By roundTrip 			 = By.cssSelector(".analytics-round-trip-btn");
	By oneWay 				 = By.cssSelector(".analytics-oneway-trip-btn");
	By from 				 = By.cssSelector("#from_filter");
	By to 					 = By.cssSelector("#to_filter");
	By departureDate 		 = By.cssSelector("#departure_date");
	By returnDate 			 = By.cssSelector("#return_date");
	By datepickerMonth 		 = By.cssSelector(".ui-datepicker-month");
	By datepickerYear 		 = By.cssSelector(".ui-datepicker-year");
	By datepickerDay 		 = By.cssSelector(".ui-datepicker-calendar [data-handler='selectDay']");
	By dateNext 			 = By.cssSelector("[data-handler='next']");
	By datePrev				 = By.cssSelector("[data-handler='prev']");
	By personCounter 		 = By.cssSelector("#person-counter");
	By personCounterAdults   = By.cssSelector("#nbadults");
	By personCounterChildren = By.cssSelector("#nbchildren");
	By personCounterInfants  = By.cssSelector("#nbinfants");
	By accomodation          = By.cssSelector(".hotel-checkbox:not(.hidden) label");
	By accomodationCheckbox  = By.cssSelector(".hotel-checkbox:not(.hidden) label");
	By search 				 = By.cssSelector("#search-form__submit-btn");

	/**
	 * This is constructor that sets a web driver for the page object  
	 * @param driver
	 * @throws InterruptedException 
	 */
	public HomePage(WebDriver driver) throws InterruptedException {
		super(driver);
		this.driver = driver;
	}

	/**
	 * This method fills the fields on the home page
	 * 
	 * @param routePlan
	 */
	public void fillSearchData(RoutePlan routePlan) {
		// Set round trip or one-way trip
		logger.info("Set round trip or one-way trip");
		if (routePlan.roundTrip) { click(roundTrip); }
		else { click(oneWay); }
		
		// Set departure and destination points
		logger.info("Set departure and destination points");
		type(from, routePlan.from);
		type(to, routePlan.to);
		
		// Set persons count
		logger.info("Set persons count");
		click(personCounter);
		type(personCounterAdults, routePlan.personCount[0]);
		type(personCounterChildren, routePlan.personCount[1]);
		type(personCounterInfants, routePlan.personCount[2]);
		action.sendKeys(Keys.ESCAPE).perform();
		
		// Set departure and return date
		logger.info("Set departure and return date");
		click(departureDate);
		setDate(routePlan.departureDate);
		action.sendKeys(Keys.ESCAPE).perform();
		
		// Set round or one way trip
		logger.info("Set round or one way trip");
		if (routePlan.roundTrip) {
			click(returnDate);
			setDate(routePlan.returnDate);
			action.sendKeys(Keys.ESCAPE).perform();
		}
		
		// Set hotel
		List<WebElement> allElements = getElements(accomodation);
		ArrayList<String> accommodationsFound = new ArrayList<String>();
		for (WebElement element: allElements) {
			WebElement accommodationCheckbox = element.findElement(By.cssSelector("span"));
			if (routePlan.accomodation == null && checkAttribute(accommodationCheckbox, "class", "checked", false)) {
				click(accommodationCheckbox);
				continue;
			}
			if (Arrays.asList(routePlan.accomodation).contains(element.getText()) && checkAttribute(accommodationCheckbox, "class", "checked", false)) {
				if (!accommodationsFound.contains(element.getText())) 
					accommodationsFound.add(element.getText());
			}
			if (Arrays.asList(routePlan.accomodation).contains(element.getText()) && !checkAttribute(accommodationCheckbox, "class", "checked", false)) {
				click(accommodationCheckbox);
				if (!accommodationsFound.contains(element.getText())) 
					accommodationsFound.add(element.getText());
			}
			if (!Arrays.asList(routePlan.accomodation).contains(element.getText()) && checkAttribute(accommodationCheckbox, "class", "checked", false)) {
				click(accommodationCheckbox);
			}
		}
		if (routePlan.accomodation != null && accommodationsFound.size() != routePlan.accomodation.length) {
			logger.error("Not all the accommodations were found.");
			logger.info("Accommodations found: " + accommodationsFound);
			logger.info("Accomodations: " + Common.arrayToString(routePlan.accomodation));
		}
	}
	
	/**
	 * This method is to set date into departure and return time fields
	 * @param date
	 */
	public void setDate(DateTime date) {
		int i = 0;
		boolean reverse = true;
		String monthLocal = new SimpleDateFormat("MMMM", Common.getLocale()).format(new Date(date.getMillis()));
		while (!(Integer.parseInt(getText(datepickerYear)) == date.getYear()) || !getText(datepickerMonth).equalsIgnoreCase(monthLocal)) {
			if (getElement(datePrev, 0) != null && reverse) {
				click(datePrev);
			}
			else {
				click(dateNext);
				reverse = false;
			}
			i++;
			if (i > 100) { 
				logger.error("Can't set date '" + Common.formatDate(date, "dd/MM/YYYY"));
				break;
			}
		}
		List<WebElement> allElements = getElements(datepickerDay);
		for (WebElement element: allElements) {
			if (Integer.parseInt(getText(element)) == date.getDayOfMonth()) {
				click(element);
				break;
			}
		}
		action.sendKeys(Keys.ESCAPE).perform();
	}

	/**
	 * This method executes fillSearchData and clicks search button
	 * it closes any other tabs that are opened after clicking Search button
	 * @param routePlan
	 * @return SearchPage object
	 * @throws InterruptedException
	 */
	public SearchPage doSearch(RoutePlan routePlan) throws InterruptedException {
		fillSearchData(routePlan);
		click_wait(search);
		closeAccommodationTabs(routePlan.accomodation);
		return new SearchPage(driver);
	}
	
	public void closeAccommodationTabs(String[] accommodation) {
		ArrayList<String> tabsClosed = new ArrayList<String>();
		ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
		if (accommodation != null && accommodation.length > 0 && tabs.size() > 1) {
			for (int i = 0; i < accommodation.length; i++) {
				for (int j = 0; j < tabs.size(); j++) {
					String title = driver.switchTo().window(tabs.get(j)).getTitle();
					if (title.toLowerCase().contains(accommodation[i].toLowerCase())) {
						driver.close();
						j = 0;
						tabsClosed.add(title);
						tabs = new ArrayList<String> (driver.getWindowHandles());
					}
				}
			}
			if (tabsClosed.size() != accommodation.length) {
				logger.error("The number of closed tabs is not equal to accomodations number.");
				logger.info("Tabs closed: " + tabsClosed);
				logger.info("Accomodations: " + Common.arrayToString(accommodation));
			}
		}
	}
	
}
