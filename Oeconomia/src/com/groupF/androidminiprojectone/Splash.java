package com.groupF.androidminiprojectone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {
	/**
	 * This is the splash of the application it displays the applications
	 * logo(for 3.5 seconds).
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		final long SleepTime = 2000;
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(SleepTime);
					Intent intent = new Intent(Splash.this, MenuPage.class);

					startActivity(intent);

					finish();
				} catch (InterruptedException e) {

				}

			}
		};
		new Thread(runnable).start();

	}
}
