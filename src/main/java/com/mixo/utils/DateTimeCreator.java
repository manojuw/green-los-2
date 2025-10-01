package com.mixo.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeCreator {
	
	private static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter
			.ofPattern(Constants.YEAR_MONTH_DAY_FORMAT);

	public static int getLastDayOfMonth(String year, String month) {
		try {
			int yearValue = Integer.parseInt(year);
			int monthValue = Integer.parseInt(month);
			return YearMonth.of(yearValue, monthValue).lengthOfMonth();
		} catch (NumberFormatException e) {
			return 30; // Default value for days if year or month is not a valid number
		}
	}

	public static boolean isValidDateDiffernce(String inputDateString, int yearCount) {
		try {
			LocalDate parsedDate = LocalDate.parse(inputDateString, YYYYMMDD_FORMATTER);
			Period period = Period.between(parsedDate, LocalDate.now());
			int years = period.getYears();
			int months = period.getMonths();
			int days = period.getDays();
			log.info("InputDate:{}, YearCount:{}, Difference: Years:{},Months:{},Days:{}", inputDateString,
					yearCount, years, months, days);
			if (years < yearCount)
				return true;
			if (years == yearCount && months > 0 && days > 0)
				return false;
		} catch (Exception e) {
			log.error("Exception occured in calculating year difference", e);
			return false;
		}
		return false;
	}

}