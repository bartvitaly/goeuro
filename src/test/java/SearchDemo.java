package test.java;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.goeuro.common.Common;
import com.goeuro.common.PropertiesUtils;
import com.goeuro.common.SoftAssert;
import com.goeuro.common.TestNGReportAppender;
import com.goeuro.items.RoutePlan;
import com.goeuro.pages.HomePage;
import com.goeuro.pages.SearchPage;

/**
 * This class contains a test demo
 * 
 * @author bartvitaly
 *
 */
public class SearchDemo extends Initialize {

	private Logger logger;
	private SoftAssert m_assert;

	public void setUp() throws Exception {
		logger = Logger.getLogger(this.getClass());
		TestNGReportAppender appender = new TestNGReportAppender();
		m_assert = appender.getAssert();
		BasicConfigurator.configure(appender);
		logger.addAppender(appender);
	}

	/**
	 * This is data provider for running one test with different data
	 * 
	 * @return
	 */
	@DataProvider(name = "demoProvider")
	public static Object[][] route() {
		return new Object[][] {
				{ new RoutePlan(true, "Madrid", "Barcelona", new int[] { 0, 0,
						1 }, Common.getDate(55), Common.getDate(66), null) },
				{ new RoutePlan(false, "Madrid", "Barcelona", new int[] { 1, 1,
						0 }, Common.getDate(0), Common.getDate(1),
						new String[] { "Booking.com" }) } };
	}

	/**
	 * This is a method that contains test and uses data provider
	 * 
	 * @param routePlan
	 * @throws Exception
	 */
	@Test(groups = { "demo" }, dataProvider = "demoProvider")
	public void demoTest(RoutePlan routePlan) throws Exception {
		setUp();
		logger.info("Started demo test for");
		driver.get(PropertiesUtils.getProperty("home"));

		logger.info("Open home page, fill search data and click search");
		HomePage homePage = new HomePage(driver);
		SearchPage searchPage = homePage.doSearch(routePlan);

		logger.info("Check search parameters are correct at the search page");
		searchPage.verifySearch(routePlan);

		logger.info("Check sorting at the search page");
		searchPage.checkSorting("price");

		tearDown();
	}

	public void tearDown() throws Exception {
		m_assert.assertAll();
	}

}
