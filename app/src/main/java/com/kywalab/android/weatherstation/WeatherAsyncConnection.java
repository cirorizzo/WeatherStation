package com.kywalab.android.weatherstation;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;

import com.kywalab.android.weatherstation.WeatherMainListActivity.AsyncHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * @author Ciro Rizzo
 *
 * This is the most important class of the project 'cause managing different Threads
 * caring of Synchronizing executions, especially for the Threads for Downloading 
 * Weather Content and Icons
 * Also other two Threads are used to manage the data to be retrieved from DB and
 * updating it
 * 
 * All the Threads using Handler and Messages to communicate with other Application Components
 * An other way to communicate is to use Intents
 */

public class WeatherAsyncConnection {
	private AsyncHandler mAsyncConnectionHandler;
	private WeatherListItemObj mWeatherListItemObj;
	private Context mCtx;
	private Semaphore firstSemaphore;

	public WeatherAsyncConnection() {
		// TODO Auto-generated constructor stub
	}

	public WeatherAsyncConnection(AsyncHandler aAsyncHandler) {
		// TODO Auto-generated constructor stub

		mAsyncConnectionHandler = aAsyncHandler;
	}

	public WeatherAsyncConnection(AsyncHandler aAsyncHandler, WeatherListItemObj aWeatherListItemObj) {
		// TODO Auto-generated constructor stub

		mAsyncConnectionHandler = aAsyncHandler;
		mWeatherListItemObj = aWeatherListItemObj;
	}

	public WeatherAsyncConnection(AsyncHandler aAsyncHandler, WeatherListItemObj aWeatherListItemObj, Context context) {
		// TODO Auto-generated constructor stub

		mAsyncConnectionHandler = aAsyncHandler;
		mWeatherListItemObj = aWeatherListItemObj;
		mCtx = context;
	}

	public void startDownload(String[] aStrs) {
				
		if (WeatherUtilityLib.isConnectivityOn(mCtx)) {
			sendAsyncHandlerMessage(AsyncHandler.WEATHER_CONTENT_STARTS_DOWNLOADING);
			
			// Semaphore to Synchronize the following Threads
			firstSemaphore = new Semaphore(1, true);
			
			// Starting Thread to download the Weather Content from a given point
			DownloadWeatherContent mDownloadWeatherContent = new DownloadWeatherContent();
			mDownloadWeatherContent.execute(aStrs);
			
			// Starting Thread to download the Weather Icons of the Content
			DownloadWeatherIcons mDownloadWeatherIcons = new DownloadWeatherIcons();
			mDownloadWeatherIcons.execute(mWeatherListItemObj);
			
			// Starting Thread to save the Weather data in DB
			UploadDBWeatherContent mUploadDBWeatherContent = new UploadDBWeatherContent();
			mUploadDBWeatherContent.execute(mWeatherListItemObj);

			
		} else {
			// Loading Data from DB
			DownloadDBWeatherContent mDownloadDBWeatherContent = new DownloadDBWeatherContent();
			mDownloadDBWeatherContent.execute(mWeatherListItemObj);
			
			sendAsyncHandlerMessage(AsyncHandler.WEATHER_CONTENT_NO_CONNECTIVITY);
		}

	}

	/**
	 * Main Thread to Download Weather Contents
	 */
	public class DownloadWeatherContent extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			firstSemaphore.acquireUninterruptibly();
			
			// TODO Auto-generated method stub
			String mResult = null;

			String mURL = WeatherUtilityLib.getHttpCityURL(params);

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet(mURL);

			try {
				HttpResponse hResponse = httpClient.execute(httpGet);

				if (hResponse.getStatusLine().getStatusCode() == 200) {

					HttpEntity hEntity = hResponse.getEntity();
					if (hEntity != null) 
						mResult = EntityUtils.toString(hEntity);
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return mResult;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {

			try {
				long mLastUpd = System.currentTimeMillis();

				JSONObject jObjRoot;
				try {
					jObjRoot = new JSONObject(result);
					if (jObjRoot != null) {

						JSONArray jArray = new JSONArray();
						jArray = jObjRoot.getJSONArray("list");

						mWeatherListItemObj.weatherList.clear();

						for (int i = 0; i < jArray.length(); i++) {
							int mId = jArray.getJSONObject(i).getInt("id");
							String mName = jArray.getJSONObject(i).getString("name");
							
							// Used in OpenWeather API 2.1
							//double mDistance = jArray.getJSONObject(i).getDouble("distance");
							double mDistance = i;
							
							double mTemp = jArray.getJSONObject(i).getJSONObject("main").getDouble("temp");
							double mTemp_Min = jArray.getJSONObject(i).getJSONObject("main").getDouble("temp_min");
							double mTemp_Max = jArray.getJSONObject(i).getJSONObject("main").getDouble("temp_max");
							String mMain = jArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main");
							String mDescription = jArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
							String mIcon_Str = jArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");

							if (mWeatherListItemObj != null)
								mWeatherListItemObj.addItem(new WeatherItemObj(mId, mName, mDistance, 
										mTemp, mTemp_Min, mTemp_Max, 
										String.format("%1s: %1s", mMain, mDescription),
										mIcon_Str, null, mLastUpd));
						}

					}


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



			} finally {
				// TODO: handle exception
				firstSemaphore.release();
			}
			System.out.println(result);
		}
	}
	
	/**
	 * Thread to Download Weather Icon (it can starts only after the DownloadWeatherContent is finished)
	 */
	public class DownloadWeatherIcons extends AsyncTask<WeatherListItemObj, Void, WeatherListItemObj> {

		@Override
		protected WeatherListItemObj doInBackground(WeatherListItemObj... params) {
			firstSemaphore.acquireUninterruptibly();
			
			WeatherListItemObj syncWeatherListItemObj = params[0];
			HttpClient httpClient = new DefaultHttpClient();


			for (int i = 0; i < syncWeatherListItemObj.getCount(); i++) {
				String iconURL = WeatherUtilityLib.getHttpIconURL(syncWeatherListItemObj.getObj(i).weather_icon_str);

				HttpGet httpGet = new HttpGet(iconURL);

				try {
					HttpResponse hResponse = httpClient.execute(httpGet);

					if (hResponse.getStatusLine().getStatusCode() == 200) {

						HttpEntity hEntity = hResponse.getEntity();
						if (hEntity != null) {
							byte[] bytes = EntityUtils.toByteArray(hEntity);

							syncWeatherListItemObj.setWeatherIcon(i, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

			}

			return syncWeatherListItemObj;
		}

		@Override
		protected void onPostExecute(WeatherListItemObj dbWeatherListItemObj) {
			// TODO All data available in the List
			
			//	Ready To save on DB
			//  Ready to update the View
			sendAsyncHandlerMessage(AsyncHandler.WEATHER_CONTENT_DOWNLOADED);
			firstSemaphore.release();			
		}
	}
	
	/**
	 * Thread to Upload/Inserting all the Weather data collected in the previous Threads
	 * to the DataBase
	 * This Thread can starts only when both the previous Threads are finished
	 */
	public class UploadDBWeatherContent extends AsyncTask<WeatherListItemObj, Void, WeatherListItemObj> {
		private WeatherDBHelper mDbHelper; 
		
		@Override
		protected WeatherListItemObj doInBackground(WeatherListItemObj... params) {
			firstSemaphore.acquireUninterruptibly();
			WeatherListItemObj syncWeatherListItemObj = params[0];
			
			try {							
				mDbHelper = new WeatherDBHelper(mCtx);
				
				mDbHelper.fillUpDB(syncWeatherListItemObj);				
			} catch (Exception e) {
				e.printStackTrace();
				// TODO Something wrong with DB Uploading Task
			} finally {
				// TODO: handle exception
				firstSemaphore.release();
			}

			return syncWeatherListItemObj;
		}
	}
	
	/**
	 * Threads to Retrieve previous Weather Data saved in the DataBase during 
	 * the last successfully Weather Data Downloading from the Network
	 */
	public class DownloadDBWeatherContent extends AsyncTask<WeatherListItemObj, Void, WeatherListItemObj> {
		private WeatherDBHelper mDbHelper; 

		@Override
		protected WeatherListItemObj doInBackground(
				WeatherListItemObj... params) {

			WeatherListItemObj syncWeatherListItemObj = params[0];
			syncWeatherListItemObj.weatherList.clear();

			mDbHelper = new WeatherDBHelper(mCtx);
			syncWeatherListItemObj = mDbHelper.fillUpListFromDB(syncWeatherListItemObj);


			return syncWeatherListItemObj;
		}
		
		@Override
		protected void onPostExecute(WeatherListItemObj aSyncWeatherListItemObj) {
			if (aSyncWeatherListItemObj.getCount() > 0)
				sendAsyncHandlerMessage(AsyncHandler.WEATHER_CONTENT_DOWNLOADED);
			else
				// This is the case No Network is available e no DB Data can be retrieved (probably is the first time the App is running)
				sendAsyncHandlerMessage(AsyncHandler.WEATHER_CONTENT_NO_CONNECTIVITY_NO_DB);
		}
	}
	
	private void sendAsyncHandlerMessage(int aMessageID) {
		Message msg = Message.obtain(mAsyncConnectionHandler, aMessageID);
		mAsyncConnectionHandler.sendMessage(msg);
	}
}
