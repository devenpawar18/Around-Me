package com.aroundme.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Utils class of Around Me that contains common and utility methods.
 * 
 * @author DEVEN
 * 
 */
public class AroundMeUtils {
	public static List<Location> list;

	/**
	 * 
	 * @param checkNull
	 * @return empty string if the value is null
	 */
	public static String checkForNull(String checkNull) {
		if (checkNull != null && !checkNull.equalsIgnoreCase("null"))
			return checkNull;
		else
			return "";
	}

	/**
	 * method to check for network availability. returns true for available and
	 * false for unavailable
	 */
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiNetwork != null && wifiNetwork.isAvailable() == true
				&& wifiNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else if (mobileNetwork != null && mobileNetwork.isAvailable() == true
				&& mobileNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else
			return false;
	}

	/**
	 * Shows the status in Dialog.
	 * 
	 * @param context
	 * @param message
	 */
	public static void showStatus(Activity activity, String message,
			String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		dialog.show();
	}

	/**
	 * Category List
	 */
	public static List<String> getList() {
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("Restaurants Around Me!");
		categoryList.add("Cafes Around Me!");
		categoryList.add("Weather Around Me!");
		categoryList.add("ATM's Around Me!");
		categoryList.add("Sightseeing Around Me!");
		categoryList.add("Lodging Around Me!");
		categoryList.add("Services Around Me!");

		return categoryList;

	}

	/**
	 * 
	 * @param key
	 * @return event values for the web service call
	 */
	public static String getEventValue(String key) {
		String value = "";

		if (key.equalsIgnoreCase("Restaurants Around Me!"))
			value = "restaurant";
		else if (key.equalsIgnoreCase("Cafes Around Me!"))
			value = "cafe";
		else if (key.equalsIgnoreCase("Sightseeing Around Me!"))
			value = "amusement_park|aquarium|art_gallery|museum|zoo";
		else if (key.equalsIgnoreCase("ATM's Around Me!"))
			value = "atm";
		else if (key.equalsIgnoreCase("Services Around Me!"))
			value = "gas_station|car_repair|car_wash|hospital";
		else if (key.equalsIgnoreCase("Lodging Around Me!"))
			value = "lodging";
		else if (key.equalsIgnoreCase("Weather Around Me!"))
			value = "weather";

		return value;
	}

	/**
	 * 
	 * @param flag
	 * @return true if the object is open
	 */
	public static String isOpen(boolean flag) {
		if (flag)
			return "Open";
		else
			return "Closed";
	}

	/**
	 * Will construct the AlertDialog.Builder object for convenience
	 * 
	 * @param context
	 *            Application Context
	 * @param message
	 *            Message to be displayed.
	 * @return AlertDialog.Builder object with the message set.
	 */
	public static AlertDialog.Builder getDialogForStatus(Activity activity,
			String message, String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		if (!TextUtils.isEmpty(title)) {
			dialog.setTitle(title);
		}

		dialog.setMessage(message);
		return dialog;
	}
}
