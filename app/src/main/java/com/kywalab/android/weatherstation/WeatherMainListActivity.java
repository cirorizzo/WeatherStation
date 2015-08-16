package com.kywalab.android.weatherstation;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

/**
 * @author Ciro Rizzo
 * 
 * Main ListActivity Class to manage the UI controls
 * For this example I used the SwipeRefreshLayout implementation
 * to have the Swipe down refresh feature
 *
 */

public class WeatherMainListActivity extends ListActivity implements OnRefreshListener {
	private AsyncHandler mAsyncHandler;
	private WeatherListItemObj mWeatherListItemObj;
	private Intent mServiceIntent;
	
	private WeatherAsyncConnection mWeatherAsyncConnection;
	
	private long mTiming = 0;
	
	private double mLat = 0.0d;
	private double mLon = 0.0d;
	
	private boolean isDownloadingWeatherData = false;
	
	//private ProgressDialog ringProgressDialog;
	private SwipeRefreshLayout swipeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_main_list);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		initObjs();
	}

	
	
	/**
	 * Method to initialize objects used for
	 */
	private void initObjs() {
		mAsyncHandler = new AsyncHandler();
		mWeatherListItemObj = new WeatherListItemObj();
		mServiceIntent = new Intent(getApplicationContext(), WeatherGPSService.class);
		
		mWeatherAsyncConnection = 
				new WeatherAsyncConnection(mAsyncHandler, mWeatherListItemObj, getApplicationContext());
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.blueback, R.color.green_on, R.color.orange, R.color.red);
	}
	
	/**
	 * Main Activity Events to detect the different stages of it
	 */
	@Override
    public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * In case the activity come back, the startingService() is calling to update data
	 */
	public void onResume() {
		super.onResume();
		startingService();
	}
	
	/**
	 * On Pause Activity ensuring to close the WeatherGPSService class where is managed
	 * the LocationManager
	 */
	public void onPause() {
		stoppingService();
		super.onPause();
	}

	
	/**
	 * This is the Handler used to manage the events from the Asynchronous Threads
	 * This is only one of the method to communicate whit other application component 
	 * or services
	 * Another way is to use Intents, but for this case is more efficient using Handlers
	 */
	public class AsyncHandler extends Handler {
		public static final int WEATHER_CONTENT_STARTS_DOWNLOADING = 10;
		public static final int WEATHER_CONTENT_DOWNLOADED = 100;
		public static final int WEATHER_CONTENT_NO_CONNECTIVITY = -100;
		public static final int WEATHER_CONTENT_NO_CONNECTIVITY_NO_DB = -200;
		
		@Override
		public void handleMessage(Message msg) {
			stopWaitingProgress();
						
			switch (msg.what) {
			case WEATHER_CONTENT_STARTS_DOWNLOADING:
				//startWaitingProgress(getApplicationContext().getResources().getString(R.string.downloading_weather_data));
				startWaitingProgress();
				break;
			case WEATHER_CONTENT_DOWNLOADED:
				isDownloadingWeatherData = false;
				setAdapterContent();
				break;
			case WEATHER_CONTENT_NO_CONNECTIVITY:
				showDialog(R.string.dialog_title_network_unavailable, R.string.dialog_message_network_unavailable, false);
				break;
			case WEATHER_CONTENT_NO_CONNECTIVITY_NO_DB:
				showDialog(R.string.dialog_title_network_unavailable_db_too, R.string.dialog_message_network_unavailable_db_too, true);
				break;
			}
		}
	}
	
	/**
	 * Method to set the Custom Adapter (WeatherItemObjAdapter) used in the ListView
	 */
	private void setAdapterContent() {
		setListAdapter(new WeatherItemObjAdapter(this, R.layout.item_of_list, mWeatherListItemObj.weatherList));
	}

	// ------------------------------------------------------------------------
	// Service Management Section
	// ------------------------------------------------------------------------	
	/**
	 * Stopping the Location Service
	 */
	private void stoppingService() {
		stopServiceManagement();
		
		unregisterReceiver(responseReceiver);
	}
	
	/**
	 * Intent Filters Setting, Receiver Registering and Starting the Location Service 
	 */
	private void startingService() {
		setIntentFilterNReceiver();
		startServiceManagement();
		startWaitingProgress();
	}
	
	private void startServiceManagement()  {
		if (mServiceIntent == null)
			mServiceIntent = new Intent(getApplicationContext(), WeatherGPSService.class);
		
		mServiceIntent.setAction(WeatherConst.BROADCAST_ACTION_START_GPSUPDATES);
		
		getApplicationContext().startService(mServiceIntent);
	}
	
	private void stopServiceManagement() {
		if (mServiceIntent == null)
			mServiceIntent = new Intent(getApplicationContext(), WeatherGPSService.class);
		
		mServiceIntent.setAction(WeatherConst.BROADCAST_ACTION_STOP_GPSUPDATES);
		
		getApplicationContext().stopService(mServiceIntent);
	}
	
	// ------------------------------------------------------------------------
	// Intents Filter and Receiver Management
	// ------------------------------------------------------------------------
	private void setIntentFilterNReceiver() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WeatherConst.BROADCAST_GPSSERVICE);
		mIntentFilter.addAction(WeatherConst.KILLING_COMMAND);
		
		registerReceiver(responseReceiver, mIntentFilter);
	}
	// ------------------------------------------------------------------------


	/**
	 * Starting the Asynchronous Threads to Download OpenWeather API Data
	 * @param aLat
	 * @param aLon
	 */
	private void startWeatherContentDownload(double aLat, double aLon) {
		if (!isDownloadingWeatherData) {
			
			String[] mStrs = {String.valueOf(aLat), String.valueOf(aLon), String.valueOf(WeatherConst.MIN_CITIES)};

			isDownloadingWeatherData = true;
			
			mWeatherAsyncConnection.startDownload(mStrs);
		}
	}


	/**
	 * Broadcast Receiver to Catch Intents from Service and other Application Components
	 * This is only one of methods to communicate between two or more Application Components
	 * Especially for the Service also using iBind would be a good solution, but for this
	 * case where the communication is just for catch point coords is sufficient using Intents
	 */
	private BroadcastReceiver responseReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intentReceived) {
			// TODO Auto-generated method stub
			
			// Broadcast Action by WeatherGPSService Class
			if (intentReceived.getAction().equalsIgnoreCase(WeatherConst.BROADCAST_GPSSERVICE)) {
				int mServiceStatus = intentReceived.getExtras().getInt(WeatherConst.EXTENDED_DATA_STATUS);
				stopWaitingProgress();

				switch (mServiceStatus) {
				case WeatherConst.SERVICESTATUS_UNAVAILABLE:
					System.out.println("UNAVAILBLE GPS SERVICE");
					if (WeatherUtilityLib.isNewestPosition(mTiming)) {
						showDialog(R.string.dialog_title_gps_unavailble, R.string.dialog_message_gps_unavailble, false);
						mLat = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LATITUDE);
						mLon = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LONGITUDE);
						startWeatherContentDownload(mLat, mLon);
					}
					break;
				case WeatherConst.SERVICESTATUS_NOT_SIGNAL:
					System.out.println("NOT SIGNAL FROM GPS SERVICE");
					if (WeatherUtilityLib.isNewestPosition(mTiming)) {
						showDialog(R.string.dialog_title_gps_no_signal, R.string.dialog_message_gps_no_signal, false);
						mLat = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LATITUDE);
						mLon = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LONGITUDE);
						startWeatherContentDownload(mLat, mLon);
					}
					break;
				case WeatherConst.SERVICESTATUS_GPS_POINT_FOUND:
					System.out.println("POINT FOUND BY GPS SERVICE");
					if (WeatherUtilityLib.isNewestPosition(mTiming)) {
						mLat = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LATITUDE);
						mLon = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LONGITUDE);
						startWeatherContentDownload(mLat, mLon);
					}
					break;	
				case WeatherConst.SERVICESTATUS_GPS_POINT_CACHED:
					System.out.println("POINT CACHED GPS SERVICE");
					if (WeatherUtilityLib.isNewestPosition(mTiming)) {
						showDialog(R.string.dialog_title_gps_not_good_signal_yet, R.string.dialog_message_gps_not_good_signal_yet, false);
						mLat = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LATITUDE);
						mLon = intentReceived.getExtras().getDouble(WeatherConst.EXTENDED_DATA_LONGITUDE);
						startWeatherContentDownload(mLat, mLon);
					}
					break;	
				default:
					break;
				}
				
				mTiming = System.currentTimeMillis();
				System.out.println(String.format("(%1f, %1f)", mLat, mLon));

			}
			
			// Broadcast Action by UIDialogMessage Class
			if (intentReceived.getAction().equalsIgnoreCase(WeatherConst.KILLING_COMMAND)) {
				WeatherMainListActivity.this.finish();
			} 
		}
		
	};
	// ------------------------------------------------------------------------
	
	
	/**
	 * Originally these two following methods are used to show-up a a ring animated progress dialog,
	 * but at the end I implemented the SwipeRefreshLayout and I let the starting/stopping method as well
	 */
	private void startWaitingProgress() {
		swipeLayout.setRefreshing(true);
	}

	private void stopWaitingProgress() {
		swipeLayout.setRefreshing(false);
	}
	
	
	/**
	 * Methods to manage the DialogFragment to interact with users
	 * @param aTitleID
	 * @param aMessageID
	 */
	private void showDialog(int aTitleID, int aMessageID, boolean aKillIt) {
		DialogFragment uiDialogMessage = UIDialogMessage.newInstance(aTitleID, aMessageID, aKillIt);
		uiDialogMessage.show(getFragmentManager(), "dialog");
	}
	
	/*
	 * Starting Weather Content Download on Swipe Refresh made by users
	 * @see android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh()
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		//startWeatherContentDownload(mLat, mLon);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				startWeatherContentDownload(mLat, mLon);
				swipeLayout.setRefreshing(false);
			}
		});
	}
}
