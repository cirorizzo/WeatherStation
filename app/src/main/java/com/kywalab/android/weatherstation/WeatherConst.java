package com.kywalab.android.weatherstation;

import android.graphics.Bitmap;

/**
 * @author Ciro Rizzo
 *
 * This class collect all the main constants used in the project
 * I used to do that to have a simple access to a single part of the code
 * when I have to change to have tuning of the App
 */

public class WeatherConst {
	
	public static final String TAG_LOG = "WeatherSatation";
	public static final boolean DEBUG_MODE = false;

	public static final String OPENWEATHER_APPID = "4ae590b8032bf397cce699a1e270bd25";
	
	public static final String DEF_STR = "";
	public static final int DEF_INT = -1;
	public static final double DEF_DOUBLE = 0.0d;
	public static final Bitmap DEF_BMP = null;
	
	public static final String FMT_TEMP_FHT = "%1.1f �F";
	public static final String FMT_TEMP_CEL = "%1.1f �C";
	
	// Changed the API call from 2.1 to 2.5 to have new features as Units (metric in this case)
	//public static final String HTTP_OPENWEATHER_API_CITY = "http://api.openweathermap.org/data/2.1/find/city";
	public static final String HTTP_OPENWEATHER_API_CITY = "http://api.openweathermap.org/data/2.5/find";
		
	public static final String HTTP_OPENWEATHER_API_CITY_PARAMS = "?lat=%1s&lon=%1s&cnt=%1s&units=metric&APPID=%1s";
	
	public static final String HTTP_OPENWEATHER_API_ICON = "http://openweathermap.org/img/w/%1s.png";
	
	
	// Defines custom Intent actions and Extended Data
    public static final String BROADCAST_GPSSERVICE = "com.kywalab.android.weatherstation.BROADCAST_GPSSERVICE";
   
    public static final String EXTENDED_DATA_STATUS = "com.kywalab.android.weatherstation.GPSSERVICE_STATUS";
    public static final String EXTENDED_DATA_LATITUDE = "com.kywalab.android.weatherstation.EXTENDED_DATA_LATITUDE";
    public static final String EXTENDED_DATA_LONGITUDE = "com.kywalab.android.weatherstation.EXTENDED_DATA_LONGITUDE";
    
    public static final String BROADCAST_ACTION_START_GPSUPDATES = "com.kywalab.android.weatherstation.BROADCAST_ACTION_START_GPSUPDATES";
    public static final String BROADCAST_ACTION_STOP_GPSUPDATES = "com.kywalab.android.weatherstation.BROADCAST_ACTION_STOP_GPSUPDATES";
    
    public static final int SERVICESTATUS_UNAVAILABLE = -1;
    public static final int SERVICESTATUS_NOT_SIGNAL = 0; 
    public static final int SERVICESTATUS_GPS_POINT_CACHED = 10;
    public static final int SERVICESTATUS_GPS_POINT_FOUND = 100;
    
    public static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    public static final int TIMEOUT_FOR_GPSPOINT = 15000;
    public static final int TIMEOUT_FOR_HTTP = 5000;
    public static final int TIMEOUT_FOR_SWIPE_REFRESH = 8000;
    public static final int METERS_GPS_UPDATING = 2000;
    
    public static final int WAITING_TIME = 1500;
    
    public static final String KILLING_COMMAND = "KILLING_COMMAND";
    public static final int MIN_CITIES = 15;
}
