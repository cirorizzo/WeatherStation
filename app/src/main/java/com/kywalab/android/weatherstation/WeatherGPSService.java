package com.kywalab.android.weatherstation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author Ciro Rizzo
 * This is the Service to manage the LocationManager and its events by LocationListner implementation
 * This is the easiest way to grab Location from GPS/Network
 */

public class WeatherGPSService extends Service implements LocationListener {
	
	private LocationManager mLocationMngr = null;

	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		sendCallBack(WeatherConst.SERVICESTATUS_GPS_POINT_FOUND, 
				location.getLatitude(), location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public int onStartCommand (final Intent intent, final int flags, final int startId) {
		if (intent.getAction().equalsIgnoreCase(WeatherConst.BROADCAST_ACTION_START_GPSUPDATES))
			startGPSDevice();
		else 
			if (intent.getAction().equalsIgnoreCase(WeatherConst.BROADCAST_ACTION_STOP_GPSUPDATES))				
				stopSelf();

		return START_REDELIVER_INTENT;
	}
	
	@Override
    public void onDestroy() {
		stopGPSDevice();
        super.onDestroy();
    }
	
	private void startGPSDevice() {
		mLocationMngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		
		crit.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = mLocationMngr.getBestProvider(crit, true);
		
		//LocationManager.GPS_PROVIDER
		//LocationManager.NETWORK_PROVIDER
		mLocationMngr.requestLocationUpdates(provider, 0, WeatherConst.METERS_GPS_UPDATING, this);
		
		boolean isGPSProviderEnabled = mLocationMngr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = mLocationMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        double mLat = 0;
        double mLon = 0;
        
        Location cachedLocation = mLocationMngr.getLastKnownLocation(provider);
        if (cachedLocation != null) {
        	mLat = cachedLocation.getLatitude();
        	mLon = cachedLocation.getLongitude();
        }
        
        if ((!isGPSProviderEnabled) && (!isNetworkProviderEnabled)) 
        	sendCallBack(WeatherConst.SERVICESTATUS_UNAVAILABLE, mLat, mLon);
        else 
        	sendCallBack(WeatherConst.SERVICESTATUS_GPS_POINT_CACHED, mLat, mLon);
	}
	
	private void stopGPSDevice() {
		mLocationMngr.removeUpdates(this);
	}

	// This is the best and easiest solution to get back information grabbed by GPS/Network
	private void sendCallBack(int aStatusCode, double aLat, double aLon) {
		Intent mBroadcastCallBack = new Intent();
		mBroadcastCallBack.setAction(WeatherConst.BROADCAST_GPSSERVICE);
		mBroadcastCallBack.putExtra(WeatherConst.EXTENDED_DATA_STATUS, aStatusCode);
		mBroadcastCallBack.putExtra(WeatherConst.EXTENDED_DATA_LATITUDE, aLat);
		mBroadcastCallBack.putExtra(WeatherConst.EXTENDED_DATA_LONGITUDE, aLon);
		
		sendBroadcast(mBroadcastCallBack);
	}

	// This is not the case to Bind Application Components 'cause we do not need to interact with this service at all
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
