package com.groupF.androidminiprojectone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CountrySelector extends Activity {
	String data = new String();
	ArrayList<String> countries = new ArrayList<String>();
	ArrayList<String> keys = new ArrayList<String>();
	EditText editText = null;
	String selectedKeys;
	RadioButton radioButton1;
	RadioButton radioButton2;
	TextView textViewCountry1;
	TextView textViewCountry2;
	Button buttonClear;
	Button buttonCompare;
	String countryOne = null;
	String countryTwo = null;

	// Declaring the GestureDetector
	private GestureDetectorCompat mDetector;

	public SharedPreferences preferences;
	String themePreference;

	/**
	 * This method is the start method for this class, it sets the themes and
	 * creates the GUI
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		themePreference = preferences.getString("ThemePreference", "");

		// Choosing theme, based on what variable themePreference contains
		if (themePreference.contains((CharSequence) "Theme1")) {
			setTheme(R.style.Theme1);
		} else if (themePreference.contains((CharSequence) "Theme2")) {
			setTheme(R.style.Theme2);
		} else if (themePreference.contains((CharSequence) "Theme3")) {
			setTheme(R.style.Theme3);
		}

		setContentView(R.layout.activity_country_selector);

		// Instatntiation of the GestureDetector with the application context
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		createGUI();
	}

	/**
	 * This method instantiates all views, adds a filter for the search function
	 * and does the initial prodecure for storing country names depending on the
	 * radio button selected
	 * 
	 * Filter method modified from:
	 * http://stackoverflow.com/questions/1737009/how
	 * -to-make-a-nice-looking-listview-filter-on-android
	 */
	public void createGUI() {
		// using this method to fill the array so the code is clearer
		fillArrayList();

		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);

		radioButton1.setChecked(true);

		textViewCountry1 = (TextView) findViewById(R.id.textViewCountry1);
		textViewCountry2 = (TextView) findViewById(R.id.textViewCountry2);
		textViewCountry1.setText(null);
		textViewCountry2.setText(null);

		buttonClear = (Button) findViewById(R.id.button2);
		buttonCompare = (Button) findViewById(R.id.button1);

		buttonClear.setVisibility(View.INVISIBLE);
		buttonCompare.setVisibility(View.INVISIBLE);
		textViewCountry1.setVisibility(View.INVISIBLE);
		textViewCountry2.setVisibility(View.INVISIBLE);

		editText = (EditText) findViewById(R.id.editText1);
		final ListView listView = (ListView) findViewById(R.id.listView1);

		if (themePreference.contains((CharSequence) "Theme1")) {

			textViewCountry1.setBackgroundColor(Color.BLACK);
			textViewCountry1.setTextColor(Color.WHITE);
			textViewCountry2.setBackgroundColor(Color.BLACK);
			textViewCountry2.setTextColor(Color.WHITE);
			// repeat for other textview
		}

		listView.setTextFilterEnabled(true);
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, countries);
		listView.setAdapter(arrayAdapter);

		TextWatcher textFilter = new TextWatcher() {

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				arrayAdapter.getFilter().filter(s);
			}

		};
		editText.addTextChangedListener(textFilter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				// this would parse the population of a country
				Object selectedItemObject = listView.getAdapter().getItem(
						position);
				String selectedItem = selectedItemObject.toString();
				int count = 0;
				for (String item : countries) {
					if (item.equals(selectedItem)) {
						selectedKeys = keys.get(count);
						break;
					}
					count++;
				}

				if (radioButton1.isChecked()) {
					if (checkNetworkConnection()) {
						Intent intent = new Intent(getBaseContext(),
								DataPage.class);
						intent.putExtra("CountryOne",
								parseThings(selectedKeys, 1));
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
					} else {
						Toast.makeText(getBaseContext(),
								"No Network available!", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					if (textViewCountry1.getText().equals("")) {
						countryOne = selectedKeys;
						textViewCountry1.setText(selectedItem);
						textViewCountry1.setVisibility(View.VISIBLE);
						buttonClear.setVisibility(View.VISIBLE);
						buttonCompare.setVisibility(View.VISIBLE);
						editText.setText("");

					} else if (textViewCountry2.getText().equals("")) {
						if (((String) textViewCountry1.getText())
								.contains(selectedItem)) {
							Context context = getApplicationContext();
							CharSequence warning = "You can't compare the same country!";
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, warning,
									duration);
							toast.show();
						} else {
							countryTwo = selectedKeys;
							textViewCountry2.setText(selectedItem);
							textViewCountry2.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});
	}

	/**
	 * This method parses the required data using the ParseArray and
	 * ParseContent. It stores them in a bundle to use in the DataPage.class and
	 * CompareDataPage.class.
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
	 * This method Parses the list of countries using the ParseCountries.class
	 * and it orders them alphabetically.
	 */
	public void fillArrayList() {
		ParseCountry parseCountries = new ParseCountry(
				"http://api.worldbank.org/countries//all/?format=json&per_page=256");
		parseCountries.start();
		try {
			parseCountries.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		parseCountries.orderList();
		countries = parseCountries.getCountries();
		keys = parseCountries.getKeys();

	}

	/**
	 * OnResume method clears all variables and data structures and starts the
	 * activity again
	 */
	public void onResume() {
		super.onResume();
		countries.clear();
		keys.clear();
		clearGUI();
		createGUI();
	}

	/**
	 * clears GUI part of GUI for if radioButton 1 is checked
	 */
	public void clearGUI() {
		data = new String();
		selectedKeys = null;
		editText.setText("");
	}

	/**
	 * hides widgets for 2 country selector
	 */
	public void clearTwoCountryView(View view) {
		clearWidgets();
	}

	/**
	 * resets relevent Strings and views if radioButton 1 is pressed
	 */
	public void clearWidgets() {
		textViewCountry1.setText(null);
		textViewCountry2.setText(null);
		countryOne = null;
		countryTwo = null;
		buttonClear.setVisibility(View.INVISIBLE);
		buttonCompare.setVisibility(View.INVISIBLE);
		textViewCountry1.setVisibility(View.INVISIBLE);
		textViewCountry2.setVisibility(View.INVISIBLE);
	}

	/**
	 * An onClick method which takes the two countries selected, whose names
	 * have been added to two TextViews (textViewCountry1 & textViewCountry2)
	 * and finds their corresponding key in the keys array. If two countries
	 * haven't been selected, a toast is returned advising the user to select
	 * two countries. Otherwise, the data for these two countries is parsed from
	 * WorldBank using the method parseThings and it is sent in bundles along
	 * with the user to the CompareDataPage activity.
	 * 
	 * @param view
	 */
	public void onClickCompare(View view) {
		if (checkNetworkConnection()) {
			Intent intent = new Intent(this, CompareDataPage.class);

			String str1Key = "";
			String str2Key = "";

			if (radioButton2.isChecked()) {
				String str1 = (String) textViewCountry1.getText();
				String str2 = (String) textViewCountry2.getText();

				for (int i = 0; i < countries.size(); i++) {
					if (str1.contains((CharSequence) countries.get(i))) {
						str1Key = keys.get(i);
						break;
					}
				}

				for (int i = 0; i < countries.size(); i++) {
					if (str2.contains((CharSequence) countries.get(i))) {
						str2Key = keys.get(i);
						break;
					}
				}
			}

			if (str1Key == "" || str2Key == "") {
				Context context = getApplicationContext();
				CharSequence warning = "Please select two countries";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, warning, duration);
				toast.show();
			} else {
				intent.putExtra("CountryOne", parseThings(str1Key, 1));
				intent.putExtra("CountryTwo", parseThings(str2Key, 2));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		} else {
			Toast.makeText(getBaseContext(), "No Network available!",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * resets widgets and variables for 1 country selection
	 */
	public void onClickRadioButton1(View view) {
		if (radioButton1.isChecked()) {
			clearGUI();
			clearWidgets();

		}
	}

	/**
	 * Transition onBackPressed and finishes the current activity
	 */
	@Override
	public void onBackPressed() {
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
	 * checks network connection, used for intent to relevent data pages
	 * Reference:
	 * http://stackoverflow.com/questions/10009804/check-network-connection
	 * -android
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