package com.kywalab.android.weatherstation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Ciro Rizzo
 *
 * This is the class to achieve from the ArrayList of Weather Data collected in the WeatherListItemObj
 * The Custom Adapter manage the right formatting of the single converted View in the parent group 
 * inflating the data in every single component View 
 */

public class WeatherItemObjAdapter extends ArrayAdapter<WeatherItemObj> {

	public WeatherItemObjAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}

	public WeatherItemObjAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public WeatherItemObjAdapter(Context context, int resource,
			WeatherItemObj[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	public WeatherItemObjAdapter(Context context, int resource,
			List<WeatherItemObj> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	public WeatherItemObjAdapter(Context context, int resource,
			int textViewResourceId, WeatherItemObj[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public WeatherItemObjAdapter(Context context, int resource,
			int textViewResourceId, List<WeatherItemObj> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		convertView = inflater.inflate(R.layout.item_of_list, null);
		
		ImageView imgVwThumbnail = (ImageView) convertView.findViewById(R.id.imgVwThumbnail);
		TextView txtVwLocationName = (TextView) convertView.findViewById(R.id.txtVwLocationName);
		TextView txtVwActTemp = (TextView) convertView.findViewById(R.id.txtVwActTemp);
		TextView txtVwMinTemp = (TextView) convertView.findViewById(R.id.txtVwMinTemp);
		TextView txtVwMaxTemp = (TextView) convertView.findViewById(R.id.txtVwMaxTemp);
		TextView txtVwDescription = (TextView) convertView.findViewById(R.id.txtVwDescription);
		
		WeatherItemObj mWeatherItemObj = getItem(position);
		
		imgVwThumbnail.setImageBitmap(mWeatherItemObj.weather_icon_bmp);
		txtVwLocationName.setText(mWeatherItemObj.name);
		txtVwActTemp.setText(WeatherUtilityLib.getTempLocale(mWeatherItemObj.act_temp));
		txtVwMinTemp.setText(String.format(getContext().getResources().getString(R.string.min_label),
				WeatherUtilityLib.getTempLocale(mWeatherItemObj.min_temp)));
		
		txtVwMaxTemp.setText(String.format(getContext().getResources().getString(R.string.max_label),
				WeatherUtilityLib.getTempLocale(mWeatherItemObj.max_temp)));
		txtVwDescription.setText(mWeatherItemObj.weather_description);

		if (WeatherConst.DEBUG_MODE) {
			WeatherUtilityLib.logMe("Name: " + mWeatherItemObj.name);
			WeatherUtilityLib.logMe("Min: " + WeatherUtilityLib.getTempLocale(mWeatherItemObj.min_temp));
			WeatherUtilityLib.logMe("Max: " + WeatherUtilityLib.getTempLocale(mWeatherItemObj.max_temp));
			WeatherUtilityLib.logMe("Descr: " + mWeatherItemObj.weather_description);
		}
		
		return convertView;
	}
}
