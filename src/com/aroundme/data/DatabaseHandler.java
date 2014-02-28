package com.aroundme.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aroundme.entities.Category;

/**
 * 
 * @author DEVEN Database Handler class to manage database activities
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "aroundme.db";

	/**
	 * Database Tables & Columns
	 */

	private static final String KEY_ID = "id";

	/**
	 * Table Data
	 */
	public static final String TABLE_FAVORITE = "favorite";
	private static final String KEY_NAME = "NAME";
	private static final String KEY_LAT = "LAT";
	private static final String KEY_LONG = "LONG";
	private static final String KEY_ADDRESS = "ADDRESS";
	private static final String KEY_OPEN = "OPEN";
	private static final String KEY_RATING = "RATING";
	private static final String KEY_FAVORITE = "FAVORITE";

	private Context context;

	private SQLiteDatabase m_db;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out
				.println("###############################Creating tables########################");
		String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_FAVORITE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_LAT + " INTEGER," + KEY_LONG + " INTEGER," + KEY_ADDRESS
				+ " TEXT," + KEY_OPEN + " INTEGER," + KEY_RATING + " INTEGER,"
				+ KEY_FAVORITE + " INTEGER" + ")";

		db.execSQL(CREATE_DATA_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**
		 * Drop older table if existed
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);

		/**
		 * Create tables again
		 */
		onCreate(db);
	}

	/**
	 * Opens the Database.
	 */
	public void openInternalDB() {
		if ((m_db == null) || (m_db.isOpen() == false)) {
			m_db = this.getWritableDatabase();
		}
	}

	/**
	 * Closes the Database.
	 */
	public void closeDB() {
		if (m_db != null) {
			m_db.close();
			m_db = null;
		}
	}

	/**
	 * Deletes one Record from the Table with given table Name & Condition.
	 * 
	 * @param tableName
	 * @param whereConition
	 * @param values
	 */
	public void deleteRow(String tableName, String whereConition,
			String[] values) {
		SQLiteDatabase db = this.getWritableDatabase();
		int deletedItems = 0;
		deletedItems = db.delete(tableName, whereConition, values);
		Log.d("", "===No. of deleted items===" + deletedItems);
		db.close();
	}

	/**
	 * Deletes all table entries of the table with given Table Name.
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteTableEntries(String TABLE_NAME) {
		int rows = m_db.delete(TABLE_NAME, null, null);
	}

	/**
	 * Adds Message to Database.
	 */
	public void addFavorite(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, category.getName());
		values.put(KEY_LAT, category.getLattitude());
		values.put(KEY_LONG, category.getLongitude());
		values.put(KEY_ADDRESS, category.getAddress());
		values.put(KEY_OPEN, category.isOpen());
		values.put(KEY_RATING, category.getRating());
		values.put(KEY_FAVORITE, category.isIsfavorite());

		db.insert(TABLE_FAVORITE, null, values);
		db.close();
	}

	/**
	 * 
	 * @return numbers of rows in Table Data
	 */
	public int getCursorCount() {
		String selectQuery = "SELECT  * FROM " + TABLE_FAVORITE;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 
	 * @return Message list from database.
	 */
	public List<Category> getFavoriteList() {

		List<Category> favoriteList = new ArrayList<Category>();
		String selectQuery = "SELECT  * FROM " + TABLE_FAVORITE;
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setName(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_NAME)));
				category.setLattitude(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_LAT)));
				category.setLongitude(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_LONG)));
				category.setAddress(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_ADDRESS)));
				if (cursor.getInt(cursor
						.getColumnIndex(DatabaseHandler.KEY_OPEN)) == 1)
					category.setOpen(true);
				else
					category.setOpen(false);
				category.setRating(cursor.getFloat(cursor
						.getColumnIndex(DatabaseHandler.KEY_RATING)));

				if (cursor.getInt(cursor
						.getColumnIndex(DatabaseHandler.KEY_FAVORITE)) == 1)
					category.setIsfavorite(true);
				else
					category.setIsfavorite(false);

				favoriteList.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return favoriteList;
	}

}
