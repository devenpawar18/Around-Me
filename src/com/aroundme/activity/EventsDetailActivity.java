package com.aroundme.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aroundme.ApplicationEx;
import com.aroundme.R;
import com.aroundme.data.DatabaseHandler;
import com.aroundme.utils.AroundMeUtils;

public class EventsDetailActivity extends AroundMeBaseActivity {
	private ActionBar actionBarSherlock;
	private TextView nameTextView;
	private TextView addressTextView;
	private TextView ratingTextView;
	private TextView statusTextView;
	private Button showMapButton;
	private TextView statusLabel;
	private RelativeLayout ratingLayout;
	private View ratingView;
	private ImageView favoriteImage;
	private Button favoriteButton;
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details);

		actionBarSherlock = getActionBar();

		/**
		 * get values for web service call
		 */
		String title = "<font color=#EDC999>" + " "
				+ getResources().getString(R.string.detail) + "</font>";
		actionBarSherlock.setTitle(Html.fromHtml(title));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);
		// actionBarSherlock.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.nav_bar));

		ratingLayout = (RelativeLayout) findViewById(R.id.rating_layout);
		nameTextView = (TextView) findViewById(R.id.name_value);
		addressTextView = (TextView) findViewById(R.id.address_value);
		ratingTextView = (TextView) findViewById(R.id.rating_value);
		statusTextView = (TextView) findViewById(R.id.status_value);
		showMapButton = (Button) findViewById(R.id.show_map);
		statusLabel = (TextView) findViewById(R.id.status);
		ratingView = (View) findViewById(R.id.rating_view);
		favoriteImage = (ImageView) findViewById(R.id.favorite);
		favoriteButton = (Button) findViewById(R.id.btn_favorite);

		if (ApplicationEx.category.isIsfavorite())
			favoriteImage.setVisibility(View.VISIBLE);
		else
			favoriteImage.setVisibility(View.INVISIBLE);

		if (ApplicationEx.category != null) {
			nameTextView.setText(ApplicationEx.category.getName());
			addressTextView.setText(ApplicationEx.category.getAddress());
			if (ApplicationEx.key.equalsIgnoreCase("ATM's")) {
				ratingLayout.setVisibility(View.GONE);
				ratingView.setVisibility(View.GONE);
				statusLabel.setText("Bank Status");

			} else {
				ratingLayout.setVisibility(View.VISIBLE);
				ratingView.setVisibility(View.VISIBLE);
			}

			if (ApplicationEx.category.getRating() == 0.0) {
				ratingTextView.setText("N/A");
			} else {
				ratingTextView.setText("" + ApplicationEx.category.getRating());
			}
			statusTextView.setText(AroundMeUtils.isOpen(ApplicationEx.category
					.isOpen()));

			favoriteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(EventsDetailActivity.this,
							"Event added to favorite", Toast.LENGTH_SHORT)
							.show();
					favoriteImage.setVisibility(View.VISIBLE);
					db = new DatabaseHandler(getApplicationContext());
					db.closeDB();
					db.openInternalDB();
					ApplicationEx.category.setIsfavorite(true);
					db.addFavorite(ApplicationEx.category);
					System.out.println("*****Cursor Count******"
							+ db.getCursorCount());
					db.close();
				}
			});
			showMapButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					/**
					 * Display Driving directions from Current location to the
					 * user selected event location
					 */
					Intent intent = new Intent(EventsDetailActivity.this,
							PlotEventActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}

	}

	/**
	 * Menu selection listener
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
