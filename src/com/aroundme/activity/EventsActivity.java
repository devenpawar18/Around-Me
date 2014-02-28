package com.aroundme.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aroundme.ApplicationEx;
import com.aroundme.R;
import com.aroundme.adapters.CategoryListAdapter;
import com.aroundme.entities.Category;

public class EventsActivity extends AroundMeBaseActivity {
	private ActionBar actionBarSherlock;
	/**
	 * List View to display events based on user selected range
	 */
	private ListView categoryListView;
	private ProgressDialog pd;
	private CategoryListAdapter categoryListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		actionBarSherlock = getActionBar();

		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();

		/**
		 * get values for web service call
		 */
		String title = "<font color=#EDC999>"
				+ " "
				+ intent.getExtras().getString(
						getResources().getString(R.string.category))
				+ "</font>";
		actionBarSherlock.setTitle(Html.fromHtml(title));

		categoryListView = (ListView) findViewById(R.id.category_list_view);
		categoryListAdapter = new CategoryListAdapter(
				ApplicationEx.categoryList, this);
		categoryListView.setAdapter(categoryListAdapter);
		categoryListView.setOnItemClickListener(onItemClickListener);

	}

	/**
	 * Action for selecting menu items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		case R.id.events_map:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * List view item click listener for the location details
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Category category = (Category) view.getTag(R.id.around_me_id);
			Intent intent = new Intent(EventsActivity.this,
					EventsDetailActivity.class);
			ApplicationEx.category = category;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	/**
	 * Creating Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.around_me_home, menu);
		return true;
	}

}
