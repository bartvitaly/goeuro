package test.java;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.goeuro.common.Common;
import com.goeuro.common.PropertiesUtils;
import com.goeuro.items.RoutePlan;
import com.goeuro.pages.HomePage;
import com.goeuro.pages.SearchPage;

/**
 * This class contains a test demo
 * @author bartvitaly
 *
 */
public class SearchDemo extends Initialize {

	final static Logger logger = Logger.getLogger(SearchDemo.class);
	
	@BeforeTest(groups = { "demo" })
	public void setUp() throws Exception
	{

	}
	
	/**
	 * This is data provider for running one test with different data
	 * @return
	 */
	@DataProvider(name = "demoProvider")
	public static Object[][] route() {
        return new Object[][] { 
    		{new RoutePlan(true, "Madrid", "Barcelona", new int[] {0, 0, 1}, Common.getDate(55), Common.getDate(66), new String[] {"Booking.com"})},
    		{new RoutePlan(false, "Madrid", "Barcelona", new int[] {0, 1, 0}, Common.getDate(0), Common.getDate(1), null)}};
	}
	
	/**
	 * This is a method that contains test and uses data provider
	 * @param routePlan
	 * @throws Exception
	 */
	@Test (groups = { "demo" }, dataProvider = "demoProvider")
	public void demoTest(RoutePlan routePlan) throws Exception {
		logger.info("Started demo test for");
		driver.get(PropertiesUtils.getProperty("home"));
		
		logger.info("Open home page, fill search data and click search");
		HomePage homePage = new HomePage(driver);
		SearchPage searchPage = homePage.doSearch(routePlan);
		searchPage.verifySearch(routePlan);
		
	}

}
