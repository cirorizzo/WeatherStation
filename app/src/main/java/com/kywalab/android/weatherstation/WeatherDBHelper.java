package com.kywalab.android.weatherstation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kywalab.android.weatherstation.WeatherDBEntry.WeatherEntryField;

/**
 * @author Ciro Rizzo
 * This is the Helper to Retrieve and Update Weather data in the SQLLite Database
 * I wouldn't use Preferences or any other files on Storage just to show how to 
 * manage a DataBase on Android platform
 *
 */

public class WeatherDBHelper extends SQLiteOpenHelper {
	private static final String TEXT_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " DOUBLE";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String BLOB_TYPE = " BLOB";
	private static final String COMMA_SEP = ",";
	private static final String NOT_NULL = " NOT NULL";
	private static final String PRIMARY_KEY = " PRIMARY KEY ";
	
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + WeatherEntryField.TABLE_NAME 				+ " (" +
					WeatherEntryField.COLUMN_NAME_WEATHER_ID 			+ INTEGER_TYPE + NOT_NULL + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_NAME 					+ TEXT_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_DISTANCE 				+ DOUBLE_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_ACT_TEMP 				+ DOUBLE_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_MIN_TEMP 				+ DOUBLE_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_MAX_TEMP 				+ DOUBLE_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_WEATHER_DESCRIPTION 	+ TEXT_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_WEATHER_ICON_STR 		+ TEXT_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_WEATHER_ICON_BMP		+ BLOB_TYPE + COMMA_SEP +
					WeatherEntryField.COLUMN_NAME_LAST_UPDATE 			+ INTEGER_TYPE + COMMA_SEP +

					PRIMARY_KEY + "(" + 
					WeatherEntryField.COLUMN_NAME_WEATHER_ID  + 
					")"+
					" )";
	
	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + WeatherEntryField.TABLE_NAME;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "WeatherStation.db";
	
	private String[] projection = {
			WeatherEntryField.COLUMN_NAME_WEATHER_ID,
			WeatherEntryField.COLUMN_NAME_NAME,
			WeatherEntryField.COLUMN_NAME_DISTANCE,
			WeatherEntryField.COLUMN_NAME_ACT_TEMP,
			WeatherEntryField.COLUMN_NAME_MIN_TEMP,
			WeatherEntryField.COLUMN_NAME_MAX_TEMP,
			WeatherEntryField.COLUMN_NAME_WEATHER_DESCRIPTION,
			WeatherEntryField.COLUMN_NAME_WEATHER_ICON_STR,
			WeatherEntryField.COLUMN_NAME_WEATHER_ICON_BMP,
			WeatherEntryField.COLUMN_NAME_LAST_UPDATE
	};
	

	
	/**
	 * Constructors
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	
	public WeatherDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public WeatherDBHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	public WeatherDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_ENTRIES);

	}

	/**
	 * This method manage the future Upgrading of DataBase
	 * but in this example is used just as caching data so
	 * No Version Checking at all (but we could do that here)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	/**
	 * Method to Update the Weather Data just retrieved form the Network
	 * @param aWeatherListItemObj
	 * @return
	 */
	public boolean fillUpDB(WeatherListItemObj aWeatherListItemObj) {
		if (getRowCount() > 0)
			getCleaning();
		
		if (aWeatherListItemObj != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			try {
				for (int i = 0; i < aWeatherListItemObj.getCount(); i++) {
					ContentValues values = new ContentValues();
					
					values.put(WeatherEntryField.COLUMN_NAME_WEATHER_ID, aWeatherListItemObj.getObj(i).id);
					values.put(WeatherEntryField.COLUMN_NAME_NAME, aWeatherListItemObj.getObj(i).name);
					values.put(WeatherEntryField.COLUMN_NAME_DISTANCE, aWeatherListItemObj.getObj(i).distance);
					values.put(WeatherEntryField.COLUMN_NAME_ACT_TEMP, aWeatherListItemObj.getObj(i).act_temp);
					values.put(WeatherEntryField.COLUMN_NAME_MIN_TEMP, aWeatherListItemObj.getObj(i).min_temp);
					values.put(WeatherEntryField.COLUMN_NAME_MAX_TEMP, aWeatherListItemObj.getObj(i).max_temp);
					values.put(WeatherEntryField.COLUMN_NAME_WEATHER_DESCRIPTION, aWeatherListItemObj.getObj(i).weather_description);
					values.put(WeatherEntryField.COLUMN_NAME_WEATHER_ICON_STR, aWeatherListItemObj.getObj(i).weather_icon_str);
					values.put(WeatherEntryField.COLUMN_NAME_WEATHER_ICON_BMP, WeatherUtilityLib.getBMPtoByteArray(aWeatherListItemObj.getObj(i).weather_icon_bmp));
					values.put(WeatherEntryField.COLUMN_NAME_LAST_UPDATE, aWeatherListItemObj.getObj(i).last_update);
					
					db.insert(WeatherEntryField.TABLE_NAME, null, values);					
				}
				
				db.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return false;
	}
	
	
	/**
	 * Method used to retrieve Weather Data from the DB with the last successfully
	 * downloading from the Network
	 * @param aWeatherListItemObj
	 * @return
	 */
	public WeatherListItemObj fillUpListFromDB(WeatherListItemObj aWeatherListItemObj) {
		try {

			SQLiteDatabase db = this.getReadableDatabase();

			Cursor curs = db.query(
					WeatherEntryField.TABLE_NAME,	// The table to query
					projection,						// The columns to return
					null,							// The columns for the WHERE clause
					null,							// The values for the WHERE clause
					null,							// don't group the rows
					null,							// don't filter by row groups
					null							// The sort order
					);
			
			if (curs != null) {
				if (curs.getCount() > 0) {
					while (curs.moveToNext()){
						
						int mID = curs.getInt(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_WEATHER_ID));
						String mName = curs.getString(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_NAME));
						double mDistance = curs.getDouble(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_DISTANCE));
						double mAct_Temp = curs.getDouble(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_ACT_TEMP));
						double mMin_Temp = curs.getDouble(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_MIN_TEMP));
						double mMax_Temp = curs.getDouble(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_MAX_TEMP));
						String mDescription = curs.getString(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_WEATHER_DESCRIPTION));
						String mIcon_Str = curs.getString(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_WEATHER_ICON_STR));
						byte[] byteArrayBMP = curs.getBlob(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_WEATHER_ICON_BMP));
						Bitmap mIconBMP  = BitmapFactory.decodeByteArray(byteArrayBMP, 0, byteArrayBMP.length);
						long mLast_Upd =curs.getLong(curs.getColumnIndexOrThrow(WeatherEntryField.COLUMN_NAME_LAST_UPDATE));
						
						if (aWeatherListItemObj !=null)
							aWeatherListItemObj.addItem(new WeatherItemObj(mID, mName, mDistance, 
									mAct_Temp, mMin_Temp, mMax_Temp, mDescription,
									mIcon_Str, mIconBMP, mLast_Upd));
						
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return aWeatherListItemObj;
	}
	
	// Getting Row Count
	public int getRowCount() {
		int resValue = 0;
		try {
			String countQuery = "SELECT * FROM " + WeatherEntryField.TABLE_NAME;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			resValue = cursor.getCount();
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

		return resValue;
	}
	
	// Getting Cleaned Table
	public int getCleaning() {
		int resValue = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			resValue = db.delete(WeatherEntryField.TABLE_NAME, null, null);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return resValue;
	}

}
