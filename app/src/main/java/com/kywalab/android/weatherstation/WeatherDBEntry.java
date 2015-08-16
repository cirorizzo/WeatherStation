package com.kywalab.android.weatherstation;

import android.provider.BaseColumns;

/**
 * @author Ciro Rizzo
 *
 * This class is used by WeatherDBHelper to manage the DB Table Name 
 * and its columns name
 *
 */

public class WeatherDBEntry {

	public WeatherDBEntry() {
		// TODO Auto-generated constructor stub
	}
	
	public static abstract class WeatherEntryField implements BaseColumns {
		public static final String TABLE_NAME = "weatherdb";
		public static final String COLUMN_NAME_WEATHER_ID = "id_weather";
		public static final String COLUMN_NAME_NAME = "location_name";
		public static final String COLUMN_NAME_DISTANCE = "location_distance";
		public static final String COLUMN_NAME_ACT_TEMP = "weather_act_temp";
		public static final String COLUMN_NAME_MIN_TEMP = "weather_min_temp";
		public static final String COLUMN_NAME_MAX_TEMP = "weather_max_temp";
		public static final String COLUMN_NAME_WEATHER_DESCRIPTION = "weather_description";
		public static final String COLUMN_NAME_WEATHER_ICON_STR = "weather_icon_str";
		public static final String COLUMN_NAME_WEATHER_ICON_BMP = "weather_icon_bmp";
		public static final String COLUMN_NAME_LAST_UPDATE = "last_update";
	}
	
}
