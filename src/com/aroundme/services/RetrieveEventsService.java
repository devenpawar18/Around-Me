package com.aroundme.services;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aroundme.entities.Category;
import com.aroundme.entities.Location;
import com.aroundme.services.utils.HTTPRequest;
import com.aroundme.utils.Constants;

/**
 * Service to retrieve events list near a particular location.
 */
public class RetrieveEventsService implements Runnable {
	/**
	 * Listener for RetrieveEventsService
	 */
	public interface RetrieveEventsServiceListener {
		void onRetrieveEventsFinished(ArrayList<Category> resultList);

		void onRetrieveEventsFailed(int error, String message);
	}

	private static final String TAG = "RetrieveLocationService";
	/** Route Location URL */
	private static String RETRIEVE_EVENTS_URL = "";
	private RetrieveEventsServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private String searchQuery = "";
	private Context context;
	private Location location;
	private long range;
	private ArrayList<Category> resultList = new ArrayList<Category>();

	public RetrieveEventsService(Context context, String searchQuery,
			Location location, long range) {
		this.context = context;
		this.searchQuery = searchQuery;
		this.location = location;
		this.range = range;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {

		Message message = new Message();
		try {
			// if (searchQuery.contains(" "))
			// searchQuery = searchQuery.replace(" ", "+");
			
			searchQuery = URLEncoder.encode(searchQuery, "utf-8");
			RETRIEVE_EVENTS_URL = Services.EVENTS_API_URL
					+ location.getLattitude() + "," + location.getLongitude()
					+ Services.RADIUS + range + Services.TYPES + searchQuery
					+ Services.SENSOR + Services.KEY;
			HTTPRequest request = new HTTPRequest(RETRIEVE_EVENTS_URL, context);
			Log.d("Events Service", "URL::" + RETRIEVE_EVENTS_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			if (jsonResponse.contains("html"))
				message.what = Constants.AroundMeDialogCodes.NETWORK_ERROR;
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			eventHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			eventHandler.sendMessage(message);
			Log.e(TAG, "Event Service exception::" + e);
		}

	}

	private Handler eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.AroundMeDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					resultList = parseRetrievedSrcLocation(jsonResponse);
					Collections.sort(resultList);
					listener.onRetrieveEventsFinished(resultList);
				} else {
					listener.onRetrieveEventsFailed(
							Constants.AroundMeDialogCodes.NETWORK_ERROR,
							Constants.AroundMeDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.AroundMeDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveEventsFailed(
						Constants.AroundMeDialogCodes.DATA_NOT_FOUND,
						Constants.AroundMeDialogMessages.NOT_FOUND);
				break;
			case Constants.AroundMeDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.AroundMeDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.AroundMeDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.AroundMeDialogCodes.NETWORK_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.AroundMeDialogCodes.NETWORK_ERROR,
						Constants.AroundMeDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveEventsFailed(
						Constants.AroundMeDialogCodes.NETWORK_ERROR,
						Constants.AroundMeDialogMessages.NETWORK_ERROR);
				break;
			}
		}
	};

	/**
	 * Get listener
	 * 
	 * @return
	 */
	public RetrieveEventsServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveEventsServiceListener listener) {
		this.listener = listener;
	}

	private ArrayList<Category> parseRetrievedSrcLocation(String response) {

		try {

			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONArray resultArray = jsonObject.getJSONArray("results");
			for (int i = 0; i < resultArray.length(); i++) {
				Category category = new Category();
				JSONObject categoryObject = resultArray.getJSONObject(i);
				category.deserializeJSON(categoryObject);
				resultList.add(category);
			}

			return resultList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveEventsFailed(
					Constants.AroundMeDialogCodes.DATA_NOT_FOUND,
					Constants.AroundMeDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

}
