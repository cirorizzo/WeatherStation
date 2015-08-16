package com.kywalab.android.weatherstation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Ciro Rizzo
 * This Class is used just as Luncher Activity as Splash to introduce the App
 * to the user.
 * After a AUTO_HIDE_DELAY_MILLIS the activity finishing and the WeatherMainListActivity is started instead.
 * By tapping on the splash screen is possible to anticipate to the WeatherMainListActivity starting.
 * 
 * This class is just to showing how are flexible Handlers using Runnables on postDelay method
 *
 */
public class WeatherLunchActivity extends Activity {
	private Handler mSplashHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mSplashHandler = new Handler();
		mSplashHandler.postDelayed(runnableSplashHandler, WeatherConst.AUTO_HIDE_DELAY_MILLIS);
			
		RelativeLayout splash_container_layout = (RelativeLayout) findViewById(R.id.splash_container_layout);
		splash_container_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startMyWeatherUI() ;
			}
			
		});
		
	}
	
	private Runnable runnableSplashHandler = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startMyWeatherUI();
		}
	};
	
	private void startMyWeatherUI() {
		mSplashHandler.removeCallbacks(runnableSplashHandler);
		
		startActivity(new Intent(this, WeatherMainListActivity.class));
		
		WeatherLunchActivity.this.finish();
	}
}
