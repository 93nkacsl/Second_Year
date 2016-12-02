package com.groupF.androidminiprojectone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//This is the menu Page where the four main buttons are shown
public class MenuPage extends Activity {

	Button B_Country, B_Favourites, B_Top_10, B_Options;
	public SharedPreferences preferences;
	boolean network;
	/**
	 * This method is the first method that gets called in this class, it sets the theme and GUI
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String themePreference = preferences.getString("ThemePreference", "");

		// Choosing theme, based on what variable themePreference contains
		if (themePreference.contains((CharSequence) "Theme1")) {
			setTheme(R.style.Theme1);
		} else if (themePreference.contains((CharSequence) "Theme2")) {
			setTheme(R.style.Theme2);
		} else if (themePreference.contains((CharSequence) "Theme3")) {
			setTheme(R.style.Theme3);
		}

		setContentView(R.layout.activity_menu_page);
		initialize();
	}
	/**
	 * This method just initialises the views
	 */
	private void initialize() {
		B_Country = (Button) findViewById(R.id.B_Country);
		B_Favourites = (Button) findViewById(R.id.B_Favourites);
		B_Top_10 = (Button) findViewById(R.id.B_Graph);
		B_Options = (Button) findViewById(R.id.B_Options);

	}
	/**
	 * This method goes to the country selector activity if the device has network otherwise it
	 * goes straight to the data page
	 */
	public void setOnclick(View view) {
		if (checkNetworkConnection()) {
			Intent intent = new Intent(MenuPage.this, CountrySelector.class);
			startActivity(intent);
			// transition screen
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			Intent intent = new Intent(MenuPage.this, DataPage.class);
			startActivity(intent);
			// transition screen
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);

		}

	}
	/**
	 * This method goes to the graph page if it has network, it displays a toast if no network
	 */
	public void graph_OnClick(View view) {
		if (checkNetworkConnection()) {
			Intent intent = new Intent(this, Graph.class);
			intent.putExtra("CountryName", "");
			startActivity(intent);

			// transition screen
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			Toast.makeText(this, "No Network available!", Toast.LENGTH_LONG)
					.show();
		}
	}
	/**
	 * This method goes to options page where you can set themes and look at help
	 */
	public void options_OnClick(View view) {
		Intent intent = new Intent(this, OptionsPage.class);
		startActivity(intent);
		// transition screen
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

	}
	/**
	 * This page goes to the favourites page if network is available, this is where you see the list of oyur favourite countries
	 */
	public void favourites_OnClick(View view) {
		if (checkNetworkConnection()) {
			Intent intent = new Intent(this, FavouritesPage.class);
			startActivity(intent);
			// transition screen
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			Toast.makeText(this, "No Network available!", Toast.LENGTH_LONG)
					.show();
		}
	}
	/**
	 * This method finishes the whole app on the back method but also finishes the duplicate home activity after changing themes
	 * The code has been used from: http://stackoverflow.com/questions/3226495/android-exit-application-code
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	/**
	 * checks network connection, used for intent to go straight to data page if user clicks country button and the device has no network
	 */
	public boolean checkNetworkConnection() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi.isConnected()) {
			return true;
		}
		if (mMobile.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
