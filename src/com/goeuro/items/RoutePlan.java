package com.goeuro.items;

import org.joda.time.DateTime;

import com.goeuro.common.Common;

/**
 * 
 * @author bartvitaly
 *
 */
public class RoutePlan {

	public boolean roundTrip;
	public String from, to;
	public int[] personCount;
	public DateTime departureDate, returnDate;
	public String[] accomodation;

	public RoutePlan(boolean roundTrip, String from, String to,
			int[] personCount, DateTime departureDate, DateTime returnDate,
			String[] accomodation) {
		this.roundTrip = roundTrip;
		this.from = from;
		this.to = to;
		this.personCount = personCount;
		this.departureDate = getDate(departureDate, 1);
		this.returnDate = getDate(returnDate, 2);
		this.accomodation = accomodation;
	}

	DateTime getDate(DateTime date, int numberOfDays) {
		if (date == null) {
			return Common.getDate(numberOfDays);
		}
		return date;
	}

}
