package com.aroundme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;

import com.aroundme.entities.Category;
import com.aroundme.entities.Location;
import com.aroundme.utils.AroundMeUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * Application level class to initialize and maintain various application life
 * cycle specific details
 */
public class ApplicationEx extends android.app.Application {
	/**
	 * used to set core number of threads
	 */
	private static final int CORE_POOL_SIZE = 6;
	public static boolean isNetworkAvailableFlag;
	/**
	 * used to set the maximum allowed number of threads.
	 */
	private static final int MAXIMUM_POOL_SIZE = 6;
	/**
	 * executes each submitted task using one of possibly several pooled threads
	 */
	public static ThreadPoolExecutor operationsQueue;
	/** Shared Preference to store login credentials. */
	public static SharedPreferences sharedPreference;
	/** Application Context */
	public static Context context;

	/**
	 * Current location of the user (Lat, Long)
	 */
	public static Location currentLocation = new Location();

	/**
	 * Current Location as String
	 */
	public static String presentLocation;

	/**
	 * Different Categories available to the user while traveling.
	 */
	public static Category category = new Category();

	/**
	 * Range for the user in terms of miles.
	 */
	public static final CharSequence[] rangeItems = { "5", "10", "15", "20" };

	/**
	 * User selected Range from the drop down
	 */
	public static int selectedRange = 0;

	/**
	 * Range in terms of meters
	 */
	public static long rangeInMeters = 0;

	/**
	 * My City as String
	 */
	public static String myCity = "";
	
	public static String myLocation;

	/**
	 * String that identifies user selected category
	 */
	public static String key;

	/**
	 * ArrayList containg all the intermediate points along the route
	 */
	public static List<LatLng> routePointsList = new ArrayList<LatLng>();

	/**
	 * counter to increment while calling simultaneous web services.
	 */
	public static int increment = 0;

	/**
	 * Main ArrayList of ArrayList of category containing results of multiple
	 * simultaneous web service calls
	 */
	public static ArrayList<Category> categoryList;

	public static int count = 0;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		rangeInMeters = 0;
		/**
		 * Check if the Network is available before making any network call.
		 */
		isNetworkAvailableFlag = AroundMeUtils.isConnectionAvailable(context);
		operationsQueue = new ThreadPoolExecutor(CORE_POOL_SIZE,
				MAXIMUM_POOL_SIZE, 100000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

	}

}
