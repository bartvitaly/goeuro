package com.goeuro.common;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.Reporter;

/**
 * 
 * @author bartvitaly
 *
 */
public class TestNGReportAppender extends AppenderSkeleton {
	private SoftAssert m_assert = new SoftAssert();

	@Override
	protected void append(final LoggingEvent event) {
		Reporter.log(eventToString(event));
	}

	private String eventToString(final LoggingEvent event) {
		m_assert.assertFalse(event.getLevel() == Level.ERROR, event
				.getMessage().toString());
		final StringBuilder result = new StringBuilder(
				event.getRenderedMessage());

		final String[] s = event.getThrowableStrRep();
		if (s != null) {
			for (final String value : s) {
				result.append(value).append(Layout.LINE_SEP);
			}
		}
		return result.toString();
	}

	public SoftAssert getAssert() {
		return m_assert;
	}

	@Override
	public void close() {

	}

	@Override
	public boolean requiresLayout() {
		return true;
	}
}