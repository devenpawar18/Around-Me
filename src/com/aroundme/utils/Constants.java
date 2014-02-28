package com.aroundme.utils;

import com.aroundme.ApplicationEx;

public final class Constants {

	/** Error dialog codes */
	public static final class AroundMeDialogCodes {
		public static final int ACTIVITYSUCCESS = 201;
		public static final int SUCCESS = 200;
		public static final int TIMEOUT = 1;
		public static final int NETWORK_ERROR = 2;
		public static final int BAD_REQUEST = 400;
		public static final int NOT_FOUND = 404;
		public static final int DATA_NOT_FOUND = 0;
		public static final int INTERNAL_SERVER_ERROR = 500;
	}

	/** Error dialog Messages */
	public static final class AroundMeDialogMessages {
		public static final String TIMEOUT = "Timeout occurred. Please try again...";
		public static final String NETWORK_ERROR = "Network error. Please try again...";
		public static final String BAD_REQUEST = "Bad request. Please try again...";
		public static final String NOT_FOUND = "Data not found. Please try again...";
		public static final String INTERNAL_SERVER_ERROR = "Internal Server error. Pleas try again...";
	}

	/**
	 * sets range in meters for the web service call
	 */
	public static void setRangeInMeters() {
		switch (ApplicationEx.selectedRange) {
		case 0:
			ApplicationEx.rangeInMeters = 8045;
			break;
		case 1:
			ApplicationEx.rangeInMeters = 16090;
			break;

		case 2:
			ApplicationEx.rangeInMeters = 24135;
			break;

		case 3:
			ApplicationEx.rangeInMeters = 32180;
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @param temp
	 * @return temperature in degree Fahrenheit
	 */
	public static int convertKelvinToDegreeFahrenheit(double temp) {

		double celcTemp = 0;
		celcTemp = ((temp - 273) * 1.8) + 32;
		return (int) Math.round(celcTemp);
	}

}
