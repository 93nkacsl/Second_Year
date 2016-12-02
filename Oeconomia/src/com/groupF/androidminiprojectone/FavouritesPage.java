package com.groupF.androidminiprojectone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavouritesPage extends Activity {

	// things copied from country selector
	String data = new String();
	int count = 0;
	String selectedKeys;
	ArrayAdapter<String> aaFavCountry;
	TextView emptyList;
	// End of variables copied from country selector

	// Declaring the GestureDetector
	private GestureDetectorCompat mDetector;

	public SharedPreferences preferences;
	/**
	 * There will be 20 shared preferences
	 */

	String preferencesNames[] = new String[20];
	int preferecesNamesSize = preferencesNames.length;
	ArrayList<String> alFavCountry = new ArrayList<String>();
	ArrayList<String> alFavCountryKey = new ArrayList<String>();

	ListView lvFavourites;
	int selectedPostion = -1;

	/**
	 * This method is the first method which runs in this class, it sets the
	 * theme, the GUI and populates the list of favourites. It also has a click
	 * listener to reference what country was clicked on
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

		setContentView(R.layout.activity_favourites_page);

		// Instatntiation of the GestureDetector with the application context
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		populateArrayLists();
		populateListView();
		emptyListText();

		// Simply notes which item was most recently selected
		lvFavourites.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg2) {
				selectedPostion = position;
			}
		});

	}

	/**
	 * Populates the ArrayList preferencesNames with the key values used to
	 * access the SharedPreferences values for the favourite countries. Odd
	 * indexes access the keys of the countries, Even indexes access the country
	 * names of the countries (which are later displayed in a ListView.
	 */
	private void populateArrayPreferencesNamesFavourites() {
		preferencesNames[0] = "spFavCountryName1";
		preferencesNames[1] = "spFavCountryKey1";
		preferencesNames[2] = "spFavCountryName2";
		preferencesNames[3] = "spFavCountryKey2";
		preferencesNames[4] = "spFavCountryName3";
		preferencesNames[5] = "spFavCountryKey3";
		preferencesNames[6] = "spFavCountryName4";
		preferencesNames[7] = "spFavCountryKey4";
		preferencesNames[8] = "spFavCountryName5";
		preferencesNames[9] = "spFavCountryKey5";
		preferencesNames[10] = "spFavCountryName6";
		preferencesNames[11] = "spFavCountryKey6";
		preferencesNames[12] = "spFavCountryName7";
		preferencesNames[13] = "spFavCountryKey7";
		preferencesNames[14] = "spFavCountryName8";
		preferencesNames[15] = "spFavCountryKey8";
		preferencesNames[16] = "spFavCountryName9";
		preferencesNames[17] = "spFavCountryKey9";
		preferencesNames[18] = "spFavCountryName10";
		preferencesNames[19] = "spFavCountryKey10";
	}

	/**
	 * Populates the ArrayList which will fill the ListView which allows users
	 * to view their favourite countries.
	 */
	private void populateArrayLists() {
		// array of the names of the variables from SharedPreferences to be
		// accessed
		populateArrayPreferencesNamesFavourites();

		// Loop through the above array to return all populated cells,
		// this data can be stored in array lists, and therefore displayed
		// on a list view
		for (int i = 0; i < (preferecesNamesSize / 2); i++) {
			if (preferences.getString(preferencesNames[i * 2], "") == "") {
				// If nothing is held in a cell, skip it!
			} else {
				// Adds what is held in SharedPreferences to ArrayLists
				alFavCountry.add(preferences.getString(preferencesNames[i * 2],
						""));
				alFavCountryKey.add(preferences.getString(
						preferencesNames[(i * 2) + 1], ""));
			}
		}
	}

	/**
	 * Populates the ListView users see by using the product of the
	 * populateArrayLists method and
	 */
	// populate ListViews
	private void populateListView() {
		emptyList = (TextView) findViewById(R.id.emptyList);
		lvFavourites = (ListView) findViewById(R.id.lvFavourites);
		// highlighting listview
		lvFavourites.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvFavourites.setSelector(android.R.color.holo_blue_light);
		aaFavCountry = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, alFavCountry);
		lvFavourites.setAdapter(aaFavCountry);
	}
	/**
	 * Just sets text to tell the user how to add countries to their favourite's list
	 */
	public void emptyListText()
	{
		if(!(aaFavCountry.isEmpty()))
		{
			emptyList.setVisibility(View.GONE);
		}
		else
		{
			emptyList.setVisibility(View.VISIBLE);
			emptyList.setText("\n" + "No Favourites!" + "\n" + "\n"
					+ "Add more?" + "\n" + "\n" + " - Go back to the Menu page"
					+ "\n" + " - Press the Country button" + "\n"
					+ " - Select one country" + "\n"
					+ " - Click the \"Not Favourited\" button" + "\n"
					+ " - Now go back to your favourite's" + "\n" + "\n"
					+ "...AND HURRAY! IT'S THERE!");
		}
	}
	/**
	 * An onClick method which is activated when the user clicks the delete
	 * button. It deletes whichever value the user has selected, however returns
	 * a toast if no country has been selected
	 * 
	 * @param view
	 */
	public void deleteSelectedFavourite(View view) {
		if (selectedPostion == -1) {
			Context context = getApplicationContext();
			CharSequence warning = "Nothing has been selected";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, warning, duration);
			toast.show();
		} else {
			alFavCountry.remove(selectedPostion);
			alFavCountryKey.remove(selectedPostion);

			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(preferencesNames[selectedPostion * 2], "");
			editor.putString(preferencesNames[(selectedPostion * 2) + 1], "");

			updateSaved();
			populateListView();
			emptyListText();
			lvFavourites.setAdapter(aaFavCountry);
			selectedPostion = -1;
		}
	}

	/**
	 * Tidies array so all populated cells are first (no empties in the middle)
	 * Takes arraylist and populates savedpreferences with changes that have
	 * been made.
	 */
	public void updateSaved() {
		SharedPreferences.Editor editor = preferences.edit();

		for (int i = 0; i < (preferecesNamesSize / 2); i++) {
			if (i >= alFavCountry.size()) {
				editor.putString(preferencesNames[i * 2], "");
			} else {
				editor.putString(preferencesNames[i * 2], alFavCountry.get(i));
			}
		}
		for (int i = 0; i < (preferecesNamesSize / 2); i++) {
			if (i >= alFavCountryKey.size()) {
				editor.putString(preferencesNames[(i * 2) + 1], "");
			} else {
				editor.putString(preferencesNames[(i * 2) + 1],
						alFavCountryKey.get(i));
			}
		}

		editor.commit();

	}

	/**
	 * An onClick method attached to the ViewCountry button parses in data for
	 * the selected country and attaches it to a bundle and sends the user to
	 * the DataPage if a country is selected. If there is no internet connection
	 * or the user has not selected a country a toast is returned advising the
	 * user on how to rectify the issue.
	 * 
	 * @param view
	 */
	public void onClickViewCountry(View view) {

		if (selectedPostion == -1) {
			Context context = getApplicationContext();
			CharSequence warning = "Nothing has been selected";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, warning, duration);
			toast.show();
		} else {
			if (checkNetworkConnection()) {

				Intent intent = new Intent(this, DataPage.class);
				// get selected item
				intent.putExtra("CountryOne",
						parseThings(alFavCountryKey.get(selectedPostion), 1));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			} else {
				Toast.makeText(this, "No Network available!", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/**
	 * This method parses the required data using the ParseArray and
	 * ParseContent. It stores them in a bundle to use in the DataPage.class.
	 */
	public Bundle parseThings(String selectedItemString, int countryNumber) {

		ParseArray populationTotal = new ParseArray(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/SP.POP.TOTL?date=1960:2009&format=json");
		populationTotal.start();
		ParseArray populationGrowth = new ParseArray(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/SP.POP.GROW?date=1960:2009&format=json");
		populationGrowth.start();
		ParseContent femalePopulation = new ParseContent(
				"http://api.worldbank.org/countries/"
						+ selectedItemString
						+ "/indicators/SP.POP.TOTL.FE.ZS?date=1960:2009&format=json");
		femalePopulation.start();
		ParseArray netMigration = new ParseArray(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/SM.POP.NETM?date=1960:2009&format=json");
		netMigration.start();

		// finance section
		ParseArray gdp = new ParseArray("http://api.worldbank.org/countries/"
				+ selectedItemString
				+ "/indicators/NY.GDP.MKTP.CD?date=1960:2009&format=json");//
		gdp.start();
		ParseArray gdpGrowth = new ParseArray(
				"http://api.worldbank.org/countries/"
						+ selectedItemString
						+ "/indicators/NY.GDP.MKTP.KD.ZG?date=1960:2009&format=json");
		gdpGrowth.start();

		ParseContent inflation = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "FP.CPI.TOTL.ZG"
						+ "?date=1960:2009&format=json");
		inflation.start();
		ParseContent importsOfGoods = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "NE.IMP.GNFS.ZS"
						+ "?date=1960:2009&format=json");
		importsOfGoods.start();
		ParseContent foreignDirectInvestment = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "BX.KLT.DINV.CD.WD"
						+ "?date=1960:2009&format=json");
		foreignDirectInvestment.start();
		ParseContent taxRate = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "IC.TAX.TOTL.CP.ZS"
						+ "?date=1960:2009&format=json");
		taxRate.start();
		// energy and infrastructure
		ParseContent landArea = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "AG.LND.TOTL.K2"
						+ "?date=1960:2009&format=json");//
		landArea.start();
		ParseArray dieselPrice = new ParseArray(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "EP.PMP.DESL.CD"
						+ "?date=1960:2009&format=json");
		dieselPrice.start();
		ParseArray gasolinePrice = new ParseArray(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "EP.PMP.SGAS.CD"
						+ "?date=1960:2009&format=json");
		gasolinePrice.start();
		ParseContent agriculture = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "AG.LND.AGRI.ZS"
						+ "?date=1960:2009&format=json");
		agriculture.start();
		ParseContent airTransport = new ParseContent(
				"http://api.worldbank.org/countries/" + selectedItemString
						+ "/indicators/" + "IS.AIR.DPRT"
						+ "?date=1960:2009&format=json");//
		airTransport.start();
		try {
			netMigration.join();
			femalePopulation.join();
			populationGrowth.join();
			populationTotal.join();
			gdp.join();
			gdpGrowth.join();
			inflation.join();
			importsOfGoods.join();
			taxRate.join();
			foreignDirectInvestment.join();
			landArea.join();
			dieselPrice.join();
			gasolinePrice.join();
			agriculture.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		Bundle bundle = new Bundle();
		bundle.putStringArrayList("PopulationList", populationTotal.getList());
		bundle.putStringArrayList("PopultionGrowth", populationGrowth.getList());
		bundle.putString("femalePopulation", femalePopulation.getValue());
		bundle.putStringArrayList("netMigration", netMigration.getList());
		bundle.putString("countryName", populationGrowth.getCountryName());

		// Finance
		bundle.putStringArrayList("gdp", gdp.getList());
		bundle.putStringArrayList("gdpGrowth", gdpGrowth.getList());
		bundle.putString("inflation", inflation.getValue());
		bundle.putString("import", importsOfGoods.getValue());
		bundle.putString("foreignDirectInvestment",
				foreignDirectInvestment.getValue());
		bundle.putString("taxRate", taxRate.getValue());
		// energy section
		bundle.putString("landArea", landArea.getValue());
		bundle.putStringArrayList("desiel", dieselPrice.getList());
		bundle.putStringArrayList("gasoline", gasolinePrice.getList());
		bundle.putString("agriculture", agriculture.getValue());
		bundle.putString("air", airTransport.getValue());
		bundle.putString("CountryName", populationGrowth.getCountryName());
		bundle.putString("CountryKey", selectedItemString);

		return bundle;

	}

	/**
	 * Transition onBackPressed and finishes the current activity
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MenuPage.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onBackPressed();
	}

	/**
	 * Modified gesture detection from
	 * http://developer.android.com/training/gestures/detector.html and from
	 * http://stackoverflow.com/questions/937313/android-basic-gesture-detection
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";
		private static final float SWIPE_MIN_DISTANCE = 120;
		private static final float SWIPE_MAX_OFF_PATH = 250;
		private static final float SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onDown(MotionEvent event) {
			Log.d(DEBUG_TAG, "onDown: " + event.toString());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			Log.d(DEBUG_TAG,
					"onFling: " + event1.toString() + event2.toString());
			try {
				if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					finish();
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_right);
				}
			} catch (Exception e) {
				// nothing
			}
			return true;
		}
	}

	/**
	 * Method added to bypass ScrollView for gesture detection purposes. Also
	 * this method makes sure that the touch is detected everywhere on the
	 * screen.
	 * http://stackoverflow.com/questions/7137742/simpleongesturelistener
	 * -not-working-for-scrollview
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		return mDetector.onTouchEvent(event);
	}

	/**
	 * checks network connection, used for intent to single country data page
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
