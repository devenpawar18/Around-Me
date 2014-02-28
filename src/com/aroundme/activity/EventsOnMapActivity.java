package com.aroundme.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aroundme.ApplicationEx;
import com.aroundme.R;
import com.aroundme.entities.Category;
import com.aroundme.services.RetrieveEventsService;
import com.aroundme.services.RetrieveEventsService.RetrieveEventsServiceListener;
import com.aroundme.utils.AroundMeUtils;
import com.aroundme.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventsOnMapActivity extends AroundMeBaseActivity implements
		OnInfoWindowClickListener, OnMarkerDragListener,
		RetrieveEventsServiceListener {
	private static final String STATE_ACTIVE_POSITION = "com.aroundme.EventsOnMapActivity.activePosition";
	private static final String STATE_CONTENT_TEXT = "com.aroundme.EventsOnMapActivity.contentText";

	private MenuDrawer mMenuDrawer;
	private CategoryAdapter adapter;

	private int mActivePosition = -1;
	private String mContentText;
	private ListView listView;

	/**
	 * For displaying Map
	 */
	private GoogleMap map;

	private ProgressDialog pd;

	private String searchQuery = "restaurant";

	private ActionBar actionBar;

	private static LatLng MY_LOCATION = null;

	private String myAddress = "";

	private final int RANGE_DIALOG = 1;

	private AlertDialog rangeDialog;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);

		actionBar = getActionBar();

		if (inState != null) {
			mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
			mContentText = inState.getString(STATE_CONTENT_TEXT);
		}

		mMenuDrawer = MenuDrawer.attach(EventsOnMapActivity.this,
				Position.RIGHT);
		mMenuDrawer.setContentView(R.layout.activity_google_map);
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
		mMenuDrawer.setMenuView(R.layout.activity_sliding_menu);

		String title = "<font color=#EDC999>" + " " + "Restaurants Around Me!"
				+ "</font>";

		actionBar.setTitle(Html.fromHtml(title));

		ApplicationEx.key = "Restaurants Around Me!";

		try {

			pd = ProgressDialog.show(EventsOnMapActivity.this, "",
					"Retrieving Your Location...", true);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			LocationManager locationManager;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			updateWithNewLocation(location);
		} catch (Exception e) {
			e.printStackTrace();
		}

		listView = (ListView) findViewById(R.id.list);
		adapter = new CategoryAdapter(this, AroundMeUtils.getList());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListner);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		/**
		 * Hide the zoom controls as the button panel will cover it.
		 */
		map.getUiSettings().setZoomControlsEnabled(false);

		/**
		 * Setting an info window adapter allows us to change the both the
		 * contents and look of the info window.
		 */
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		/**
		 * Set listeners for marker events. See the bottom of this class for
		 * their behavior.
		 */
		map.setOnInfoWindowClickListener(this);

		/**
		 * Zoom in, animating the camera.
		 */
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerDragListener(this);

	}

	public void getCategories() {
		Constants.setRangeInMeters();
		pd = ProgressDialog.show(EventsOnMapActivity.this, "",
				"Loading Events...", true);
		RetrieveEventsService service = new RetrieveEventsService(
				getApplicationContext(), searchQuery,
				ApplicationEx.currentLocation, ApplicationEx.rangeInMeters);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/** Demonstrates customizing the info window and/or its contents. */
	class CustomInfoWindowAdapter implements InfoWindowAdapter,
			OnMarkerClickListener {
		/**
		 * These a both viewgroups containing two TextViews with id" title" and
		 * "snippet".
		 */
		private final View mWindow;

		CustomInfoWindowAdapter() {
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
					null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		private void render(Marker marker, View view) {
			System.out.println("*****Clicked*****");
			String title = marker.getTitle();
			String address = marker.getSnippet();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				/**
				 * Spannable string allows us to edit the formatting of the
				 * text.
				 */
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0,
						titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}

			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippetUi != null) {
				/**
				 * Spannable string allows us to edit the formatting of the
				 * text.
				 */
				SpannableString addressText = new SpannableString(address);
				addressText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
						addressText.length(), 0);
				snippetUi.setText(addressText);
			} else {
				snippetUi.setText("");
			}

		}

		@Override
		public View getInfoContents(Marker marker) {
			System.out.println("*****Clicked*****");
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.android.gms.maps.GoogleMap.OnMarkerClickListener#onMarkerClick
		 * (com.google.android.gms.maps.model.Marker)
		 */
		@Override
		public boolean onMarkerClick(Marker marker) {
			System.out.println("*****Clicked*****");
			return false;
		}

	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	/**
	 * On Menu Click
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_CLOSED
					|| drawerState == MenuDrawer.STATE_CLOSING) {
				mMenuDrawer.openMenu();
			}
			break;
		case R.id.events_list:
			Intent intent = new Intent(EventsOnMapActivity.this,
					EventsActivity.class);
			intent.putExtra(getResources().getString(R.string.category),
					ApplicationEx.key);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.range:
			showDialog(RANGE_DIALOG);
			break;
		case R.id.favorite:
			Intent favoriteIntent = new Intent(EventsOnMapActivity.this,
					FavoriteEventsActivity.class);
			favoriteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(favoriteIntent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc) Display Dialogs for selecting Locations & Range for
	 * locations
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case RANGE_DIALOG:
			AlertDialog.Builder rangeBuilder = new AlertDialog.Builder(this);
			rangeBuilder.setTitle(R.string.select_range);
			rangeBuilder.setSingleChoiceItems(ApplicationEx.rangeItems,
					ApplicationEx.selectedRange,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
							case 0:
								ApplicationEx.selectedRange = 0;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 1:
								ApplicationEx.selectedRange = 1;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 2:
								ApplicationEx.selectedRange = 2;
								rangeDialog.dismiss();
								getCategories();
								break;
							case 3:
								ApplicationEx.selectedRange = 3;
								rangeDialog.dismiss();
								getCategories();
								break;
							}
						}
					});
			rangeDialog = rangeBuilder.create();
			return rangeDialog;
		default:
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Handles back press
	 */
	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}

		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
		outState.putString(STATE_CONTENT_TEXT, mContentText);
	}

	/**
	 * On click listener for the category list
	 */
	private OnItemClickListener onItemClickListner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			String selectedItem = (String) view.getTag(R.id.id_name);
			mActivePosition = position;
			mMenuDrawer.setActiveView(view, position);
			mMenuDrawer.closeMenu();
			ApplicationEx.key = selectedItem;
			System.out.println("*****Key******" + ApplicationEx.key);
			/**
			 * get values for web service call
			 */
			searchQuery = AroundMeUtils.getEventValue(ApplicationEx.key);

			if (searchQuery.equalsIgnoreCase("weather")) {
				Intent intent = new Intent(EventsOnMapActivity.this,
						WeatherActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				String title = "<font color=#EDC999>" + " " + ApplicationEx.key
						+ "</font>";
				actionBar.setTitle(Html.fromHtml(title));
				getCategories();
			}
		}
	};

	public class CategoryAdapter extends BaseAdapter {
		private List<String> categoryList = new ArrayList<String>();
		private Context context;

		public CategoryAdapter(Context applicationContext, List<String> teamList) {
			this.context = applicationContext;
			this.categoryList = teamList;
		}

		/**
		 * Updates list view whenever data is changed.
		 * 
		 * @param checkingAccountsList
		 */
		public void setActivityList(List<String> activeList) {
			this.categoryList = activeList;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return categoryList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return categoryList.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ActivitiesViewHolder activitiesViewHolder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater
						.inflate(R.layout.activity_list_row, null);
				activitiesViewHolder = new ActivitiesViewHolder();
				activitiesViewHolder.itemTextView = (TextView) convertView
						.findViewById(R.id.item);
			} else {
				activitiesViewHolder = (ActivitiesViewHolder) convertView
						.getTag();
			}
			String item = categoryList.get(position);
			activitiesViewHolder.itemTextView.setText(item);
			convertView.setTag(activitiesViewHolder);
			convertView.setTag(R.id.id_name, item);
			convertView.setTag(R.id.mdActiveViewPosition, position);
			if (position == mActivePosition) {
				mMenuDrawer.setActiveView(convertView, position);
			}

			return convertView;
		}

		/**
		 * Temporary holder class to hold references to the relevant Views in
		 * layout
		 * 
		 * @author devpawar
		 * 
		 */
		private class ActivitiesViewHolder {
			TextView itemTextView;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#
	 * onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub

	}

	/**
	 * Creating Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.rangemenu, menu);
		return true;
	}

	/**
	 * Update Location with current location
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		if (pd != null && pd.isShowing())
			pd.cancel();
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			System.out.println("***lattitude***" + lat
					+ "**** & longitude ******" + lng);
			MY_LOCATION = new LatLng(lat, lng);
			ApplicationEx.currentLocation.setLattitude(lat);
			ApplicationEx.currentLocation.setLongitude(lng);

			/**
			 * Pan to see all markers in view. Cannot zoom to bounds until the
			 * map has a final size.
			 */
			final View mapView = getFragmentManager()
					.findFragmentById(R.id.map).getView();
			if (mapView.getViewTreeObserver().isAlive()) {
				mapView.getViewTreeObserver().addOnGlobalLayoutListener(
						new OnGlobalLayoutListener() {
							@SuppressWarnings("deprecation")
							/**
							 *  We use the new method when supported
							 */
							@SuppressLint("NewApi")
							/**
							 *  We check which build version we are using.
							 */
							@Override
							public void onGlobalLayout() {
								LatLngBounds bounds = new LatLngBounds.Builder()
										.include(MY_LOCATION).build();
								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
									mapView.getViewTreeObserver()
											.removeGlobalOnLayoutListener(this);
								} else {
									mapView.getViewTreeObserver()
											.removeOnGlobalLayoutListener(this);
								}
								map.moveCamera(CameraUpdateFactory
										.newLatLngBounds(bounds, 20));
								map.animateCamera(
										CameraUpdateFactory.zoomTo(11), 3000,
										null);
							}
						});
			}

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			try {
				addresses = geocoder.getFromLocation(lat, lng, 1);
				myAddress = addresses.get(0).getAddressLine(0) + ", "
						+ addresses.get(0).getAddressLine(1);

				ApplicationEx.myLocation = myAddress;
				ApplicationEx.myCity = addresses.get(0).getLocality();
				getCategories();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			AlertDialog.Builder dialog = AroundMeUtils.getDialogForStatus(
					EventsOnMapActivity.this, "Please turn on the GPS",
					"Enable GPS");
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});
			dialog.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aroundme.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFinished(java.util.ArrayList)
	 */
	@Override
	public void onRetrieveEventsFinished(ArrayList<Category> resultList) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		ApplicationEx.categoryList = resultList;
		addEventMarkersToMap(resultList);

	}

	public void addEventMarkersToMap(List<Category> resultList) {
		map.clear();
		map.addMarker(new MarkerOptions()
				.title("My Location")
				.snippet(myAddress)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
				.position(
						new LatLng(
								ApplicationEx.currentLocation.getLattitude(),
								ApplicationEx.currentLocation.getLongitude())));

		for (int i = 0; i < resultList.size(); i++) {
			Category category = resultList.get(i);
			map.addMarker(new MarkerOptions()
					.title(category.getName())
					.snippet(category.getAddress())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.position(
							new LatLng(category.getLattitude(), category
									.getLongitude())));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aroundme.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFailed(int, java.lang.String)
	 */
	@Override
	public void onRetrieveEventsFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		AroundMeUtils.showStatus(EventsOnMapActivity.this, message, "Error");
	}

}
