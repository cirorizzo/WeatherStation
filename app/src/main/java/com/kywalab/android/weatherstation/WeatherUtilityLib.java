package com.kywalab.android.weatherstation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * @author Ciro Rizzo
 * 
 * This Class is used to collect the main utility functions
 * used in the project
 * 
 */


@SuppressLint("DefaultLocale")
public class WeatherUtilityLib {

	public WeatherUtilityLib() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method implemented to print out degree format
	 * Originally the idea was to manage the Locale information to get 
	 * the right Temperature scale to be used (not so much 
	 * time available to do that)  
	 */
	
	@SuppressLint("DefaultLocale")
	public static String getTempLocale(double aTemp) {
		return String.format(WeatherConst.FMT_TEMP_CEL, aTemp);
	}

	/** 
	 * Manage the right formatting String for the HTTP URL of
	 * OpenWeather API
	 * @param HTTP params
	 * @return URL String to execute the call to OpenWeather API
	 */
	public static String getHttpCityURL(String... params) {
		return WeatherConst.HTTP_OPENWEATHER_API_CITY + String.format(WeatherConst.HTTP_OPENWEATHER_API_CITY_PARAMS,
				params[0], params[1], params[2], WeatherConst.OPENWEATHER_APPID);
	}
	
	/** 
	 * Manage the right formatting String for the HTTP URL of
	 * OpenWeather API Icon
	 * @param HTTP params
	 * @return URL String to execute the call to OpenWeather API Icon
	 */
	public static String getHttpIconURL(String param) {
		return String.format(WeatherConst.HTTP_OPENWEATHER_API_ICON, param);
	}
	
	/** 
	 * Checking the available Data Connectivity
	 * @param Context
	 * @return boolean value (true = Connectivity is On)
	 */
	public static boolean isConnectivityOn(Context ctx) {
    	boolean resCode = false;

    	try {
    		ConnectivityManager cm =
    				(ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

    		resCode = cm.getActiveNetworkInfo().isConnectedOrConnecting();

    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    	}

    	return resCode;
    }
	
	/**
	 * Conversion method to manage Bitmap Object to Byte Array
	 * @param aBMP
	 * @return byte[]
	 */
	public static byte[] getBMPtoByteArray(Bitmap aBMP) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		aBMP.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	/**
	 * Simple method to establish if the GPS/Network point is newest
	 * @param aTiming
	 * @return boolean
	 */
	public static boolean isNewestPosition(long aTiming) {
		return ((System.currentTimeMillis() - aTiming) > WeatherConst.TIMEOUT_FOR_GPSPOINT);
	}
	
	/**
	 * Method to manage in easy way Logs
	 * @param aStr
	 */
	public static void logMe(String aStr) {
		Log.d(WeatherConst.TAG_LOG, aStr);
	}
}
