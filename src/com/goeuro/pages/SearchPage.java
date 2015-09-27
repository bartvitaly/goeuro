package com.goeuro.pages;

import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.goeuro.common.Common;
import com.goeuro.common.WebDriverCommon;
import com.goeuro.items.RoutePlan;

/**
 * This class describes a search page of the web site
 * 
 * @author bartvitaly
 *
 */
public class SearchPage extends WebDriverCommon {

	private final WebDriver driver;
	final static Logger logger = Logger.getLogger(SearchPage.class);

	By from = By.cssSelector("#from_filter");
	By to = By.cssSelector("#to_filter");
	By departureDate = By.cssSelector("#departure_date");
	By returnDate = By.cssSelector("#return_date");
	By searchFromMore = By.cssSelector("#results-search-form-more-btn");
	By roundTrip = By.cssSelector("#trip_type_1");
	By oneWay = By.cssSelector("#trip_type_2");
	By personCounter = By.cssSelector("#person-counter");
	By personCounterAdults = By.cssSelector("#nbadults");
	By personCounterChildren = By.cssSelector("#nbchildren");
	By personCounterInfants = By.cssSelector("#nbinfants");

	public SearchPage(WebDriver driver) throws InterruptedException {
		super(driver);
		this.driver = driver;

		if (!driver.findElement(from).isDisplayed()) {
			throw new IllegalStateException("This is not the SearchPage page");
		}
	}

	public void verifySearch(RoutePlan routePlan) throws InterruptedException {
		// Check departure and destination points
		checkAttribute(from, "value", routePlan.from, true);
		checkAttribute(to, "value", routePlan.to, true);

		// Check departure and return date
		checkAttribute(departureDate, "value",
				Common.formatDate(routePlan.departureDate, "dd/MM/yyyy"), true);
		checkAttribute(returnDate, "value",
				Common.formatDate(routePlan.returnDate, "dd/MM/yyyy"), true);

		// Check persons count
		String personCountActual = getText(personCounter);
		String personCountExpected = String.valueOf(IntStream.of(
				routePlan.personCount).sum());
		if (!personCountActual.equals(personCountExpected)) {
			logger.error("Person count is incorrect, expected '"
					+ personCountExpected + "', actual '" + personCountActual
					+ "'");
		}

		// Check round or one way trip
		click_wait(searchFromMore);
		if (routePlan.roundTrip) {
			checkAttribute(roundTrip, "class", "active", true);
		} else {
			checkAttribute(oneWay, "class", "active", true);
		}

		// Check persons count
		checkAttribute(personCounterAdults, "value", routePlan.personCount[0],
				true);
		checkAttribute(personCounterChildren, "value",
				routePlan.personCount[1], true);
		checkAttribute(personCounterInfants, "value", routePlan.personCount[2],
				true);

	}

}
