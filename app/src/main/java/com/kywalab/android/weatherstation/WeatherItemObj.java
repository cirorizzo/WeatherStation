package com.kywalab.android.weatherstation;

import android.graphics.Bitmap;

/**
 * @author Ciro Rizzo
 * This is the Class to manage the single Item Shown in the ListView by 
 * the Custom Adapter (WeatherItemObjAdapter)
 *
 */

public class WeatherItemObj {
	public int id;
	public String name;
	public double distance;
	public double act_temp;
	public double min_temp;
	public double max_temp;
	
	public String weather_description;
	public String weather_icon_str;
	public Bitmap weather_icon_bmp;
	public long last_update;

	
	public WeatherItemObj() {
		// TODO Auto-generated constructor stub
		initMembers(WeatherConst.DEF_INT, WeatherConst.DEF_STR, WeatherConst.DEF_INT,
				WeatherConst.DEF_DOUBLE, WeatherConst.DEF_DOUBLE, WeatherConst.DEF_DOUBLE,
				WeatherConst.DEF_STR,
				WeatherConst.DEF_STR, WeatherConst.DEF_BMP,
				WeatherConst.DEF_INT);
	}
	
	public WeatherItemObj(int aId, String aName, double aDistance,
			double aAct_temp, double aMin_temp, double aMax_temp,
			String aWeather_description,
			String aWeather_icon_str, Bitmap aWeather_icon_bmp,
			long aLast_update) {
		
		initMembers(aId, aName, aDistance,
			aAct_temp, aMin_temp, aMax_temp,
			aWeather_description,
			aWeather_icon_str, aWeather_icon_bmp,
			aLast_update);
	}

	
	private void initMembers(int aId, String aName, double aDistance,
		double aAct_temp, double aMin_temp, double aMax_tem,
		String aWeather_description,
		String aWeather_icon_str, Bitmap aWeather_icon_bmp,
		long aLast_update) {
		
		id = aId;
		name = aName;
		distance = aDistance;
		act_temp = aAct_temp;
		min_temp = aMin_temp;
		max_temp = aMax_tem;
		weather_description = aWeather_description;
		weather_icon_str = aWeather_icon_str;
		weather_icon_bmp = aWeather_icon_bmp;
		last_update = aLast_update;
	}
}
