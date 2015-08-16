package com.kywalab.android.weatherstation;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * @author Ciro Rizzo
 * This Class is used to manage the list of items of Weather Data caught from the OpenWeather/DB
 * and shown on UI by WeatherItemObjectAdapter (the Custom Adapter)
 */

public class WeatherListItemObj {
	public ArrayList<WeatherItemObj> weatherList;

	public WeatherListItemObj() {
		weatherList = new ArrayList<WeatherItemObj>();
	}
	
	public void addItem(WeatherItemObj aWeatherItemObj) {
		weatherList.add(aWeatherItemObj);
	}
	
	public int getCount() {
		return weatherList.size();
	}
	
	public WeatherItemObj getObj(int aIdx) {
		return weatherList.get(aIdx);
	}
	
	/**
	 * This method is used to set in the ArrayList the icon of Weather in a second stage
	 * @param aIdx
	 * @param aWeather_icon_bmp
	 */
	public void setWeatherIcon(int aIdx, Bitmap aWeather_icon_bmp) {
		weatherList.get(aIdx).weather_icon_bmp = aWeather_icon_bmp;
	}

}
