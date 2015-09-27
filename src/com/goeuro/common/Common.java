package com.goeuro.common;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * @author bartvitaly
 *
 */
public class Common {

	public static String randomString(int length) {
		return RandomStringUtils.random(length, "abcdefghijklmnopqrstuvwxyz");
	}

	public static String arrayToString(String[] arr) {
		StringBuilder builder = new StringBuilder();
		for (String s : arr) {
			builder.append(s + " ");
		}
		return builder.toString();
	}

	public static String timestamp() {
		DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		long time = date.getTime();
		return String.valueOf(time);
	}

	public static Locale getLocale() {
		Pattern countryPattern = Pattern.compile(
				PropertiesUtils.getProperty("country"),
				Pattern.CASE_INSENSITIVE);
		Pattern languagePattern = Pattern.compile(
				PropertiesUtils.getProperty("language"),
				Pattern.CASE_INSENSITIVE);

		for (Locale locale : Locale.getAvailableLocales()) {
			boolean isPrint = countryPattern
					.matcher(locale.getDisplayCountry()).find()
					&& languagePattern.matcher(locale.getDisplayName()).find();
			if (!isPrint)
				continue;
			else
				return locale;
		}
		return null;
	}

	public static DateTime getDate(int numberOfDaysFromToday) {
		DateTime dateTime = DateTime.now();
		return dateTime.plusDays(numberOfDaysFromToday);
	}

	public static String formatDate(DateTime dateTime, String format) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return dateTime.toString(fmt);
	}

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void writeToFile(String path, String text) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path), "utf-8"));
			writer.write(text);
		} catch (IOException ex) {
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}

}
