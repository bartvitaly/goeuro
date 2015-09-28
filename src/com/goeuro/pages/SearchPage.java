package com.goeuro.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.goeuro.common.Common;
import com.goeuro.common.WebDriverCommon;
import com.goeuro.items.RoutePlan;
import com.google.common.collect.Ordering;

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
	By sortbySmart = By.cssSelector("#sortby-smart");
	By sortbyPrice = By.cssSelector("#sortby-price");
	By sortbyTraveltime = By.cssSelector("#sortby-traveltime");
	// By sortbyoutboundDepartureTime =
	// By.cssSelector("#sortby-outboundDepartureTime");
	// By sortbyoutboundArrivalTime =
	// By.cssSelector("#sortby-outboundArrivalTime");
	By tabTrain = By.cssSelector("#tab_train");
	By tabFlight = By.cssSelector("#tab_flight");
	By tabBus = By.cssSelector("#tab_bus");
	By results = By.cssSelector("#results .active .result");
	By currencyBeforecomma = By.cssSelector(".price-no .currency-beforecomma");
	By currencyDecimals = By.cssSelector(".price-no .currency-decimals");

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
		if (routePlan.roundTrip) {
			checkAttribute(returnDate, "value",
					Common.formatDate(routePlan.returnDate, "dd/MM/yyyy"), true);
		}

		// Check persons count
		String personCountActual = getText(personCounter);
		if (routePlan.personCount[0] < 1) {
			routePlan.personCount[0] = 1;
		}

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

	/**
	 * @param sortingEntity
	 *            , possible values: smart, price, traveltime
	 **/
	public void checkSorting(String sortingEntity) throws InterruptedException {
		switch (sortingEntity.toLowerCase()) {
		case "smart":
			click(sortbySmart);
			break;
		case "price":
			click(sortbyPrice);
			waitForPageLoaded(driver);
			ArrayList<Double> sortingValues = new ArrayList<>();
			sortingValues = getPriceValues();
			logger.info("Checking " + sortingEntity + " sorting of: "
					+ sortingValues);
			if (!Ordering.natural().isOrdered(sortingValues)) {
				logger.error("Sorting by " + sortingEntity
						+ " is incorrect. Values: " + sortingValues);
			}
			break;
		case "traveltime":
			click(sortbyTraveltime);
			break;
		case "outboudDeparture":
			break;
		case "outboudArrival":
			break;
		default:
			break;
		}
	}

	public ArrayList<Double> getPriceValues() {
		List<WebElement> resultItems = getElements(results);
		ArrayList<Double> sortingValues = new ArrayList<Double>();
		for (int i = 0; i < resultItems.size(); i++) {
			String priceBeforeComma = Common
					.extractNumbers(
							getText(resultItems.get(i).findElement(
									currencyBeforecomma))).get(0);
			int j = 0;
			String priceDecimal = "";
			List<WebElement> currencyDecimalsElements = resultItems.get(i)
					.findElements(currencyDecimals);
			for (j = 0; j < currencyDecimalsElements.size(); j++) {
				ArrayList<String> numbersExtracted = Common
						.extractNumbers(getText(currencyDecimalsElements.get(j)));
				if (numbersExtracted.size() > 0) {
					priceDecimal += numbersExtracted.get(0);
				}
			}
			sortingValues.add(Double.valueOf(priceBeforeComma + "."
					+ priceDecimal));
		}
		return sortingValues;
	}

}
