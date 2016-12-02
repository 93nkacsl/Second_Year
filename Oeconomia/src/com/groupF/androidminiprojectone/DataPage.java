package com.groupF.androidminiprojectone;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataPage extends Activity {
	// compare button
	Button b_Graph, b_Home, b_People, b_Finance, b_Energy;
	LinearLayout buttonsLayout;
	TextView tv_CountryName;
	WebView wv_Finance1, wv_People1, wv_Energy1;

	int people_Counter, finance_Counter, Energy_Counter;

	ArrayList<String> populationTotal, populationGrowth, gdp, gdpGrowth,
			dieselPrice, gasolinePrice, netMigration;

	String countryname, landArea, femalePop, inflation, imports, Dinvestment,
			taxRate, agriculture, air;

	boolean connection;
	String popInternet, popGrowInternet, nameInternet, gdpInternet,
			gdpGrowInternet, landInternet, dieselInternet, gasInternet,
			femInternet, netInternet, inflateInternet, importInternet,
			investInternet, taxInternet, agricultureInternet, airInternet;

	String tableColor;

	String peopleInfoSmall, financeInfoSmall, energyInfoSmall;

	// Declaring the GestureDetector
	private GestureDetectorCompat mDetector;

	public SharedPreferences preferences;
	public static final String variable = "com.groupF.androidminiprojectone";

	Button b_Favourites;
	String preferencesNames[] = new String[20];
	int preferecesNamesSize = preferencesNames.length;
	String countryKey;
	String countryName;
	Boolean isFavourite = false;
	Bundle bundle1;
	/**
	 * This method is the first method for this class, it sets the theme and GUI related tasks
	 * and it displays the initial data (minimised state)
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

		if (this.getTheme().equals("Theme1")) {
			tableColor = "#000000";
		} else {
			tableColor = "";
		}

		setContentView(R.layout.activity_data_page_html);

		// Instantiation of the GestureDetector with the application context
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		initialize();

		finance_Counter = 0;
		people_Counter = 0;
		Energy_Counter = 0;


		// Initial TABLE
		if (connection) {
			collectBundle();
			initialTextConnections(countryname, populationTotal.get(0),
					populationGrowth.get(0), gdp.get(0), gdpGrowth.get(0),
					landArea, dieselPrice.get(1), gasolinePrice.get(1));
		} else {
			initialTextConnections(nameInternet, popInternet, popGrowInternet,
					gdpInternet, gdpGrowInternet, landInternet, dieselInternet,
					gasInternet);
		}

	}
	/**
	 * Universal method for the visualisation of the initial tables. Used every
	 * time when the initial connections are set with or without Internet.
	 * @param countryname
	 * @param pop
	 * @param popGrow
	 * @param gdp
	 * @param gdpGrow
	 * @param landArea
	 * @param diesel
	 * @param gas
	 */
	public void initialTextConnections(String countryname, String pop,
			String popGrow, String gdp, String gdpGrow, String landArea,
			String diesel, String gas) {
		String setting = "";
		if (!gdp.equals("null")) 
		{
			setting = setCommas(gdp);
		} 
		else 
		{
			setting = "N/A";
		}

		wv_People1.loadUrl("about:blank");
		wv_Finance1.loadUrl("about:blank");
		wv_Energy1.loadUrl("about:blank");
		tv_CountryName.setText(countryname);
		peopleInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">Population:</td><td>"
				+ setCommas(pop)
				+ "</td></tr><tr><td colspan=\"2\">Population Growth</td><td>"
				+ FormatNull(formatTo2Decimal(popGrow), " %")+ "</td></tr></table>";

		financeInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">GDP:</td><td>"
				+ setting
				+ "</td></tr><tr><td colspan=\"2\">GDP Growth</td><td>"
				+ FormatNull(formatTo2Decimal(gdpGrow), " $") + "</td></tr></table>";

		energyInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">Land Area:</td><td>"
				+ FormatNull(checkForNull(setCommas(landArea)), " km<sup>2</sup>")
				+ "</td></tr><tr><td colspan=\"2\">Diesel Price</td><td>"
				+ FormatNull(formatTo2Decimal(diesel), " $")
				+ "</td></tr><tr><td colspan=\"2\">Gasoline Price</td><td>"
				+ FormatNull(formatTo2Decimal(gas), " $") + "</td></tr></table>";
		wv_People1.loadData(peopleInfoSmall, "text/html", null);
		wv_Finance1.loadData(financeInfoSmall, "text/html", null);
		wv_Energy1.loadData(energyInfoSmall, "text/html", null);
	}
	/**
	 * This method collects the bundles being passed from the countrySelector class and
	 * assigns it to variables which can be used, it also uses these pieces of data
	 * to populate the data when there is no connection
	 */
	public void collectBundle() {

		Bundle bundle1 = getIntent().getExtras().getBundle("CountryOne");
		populationTotal = bundle1.getStringArrayList("PopulationList");
		populationGrowth = bundle1.getStringArrayList("PopultionGrowth");
		countryname = bundle1.getString("countryName");
		gdp = bundle1.getStringArrayList("gdp");
		gdpGrowth = bundle1.getStringArrayList("gdpGrowth");
		landArea = bundle1.getString("landArea");
		dieselPrice = bundle1.getStringArrayList("desiel");
		gasolinePrice = bundle1.getStringArrayList("gasoline");
		femalePop = bundle1.getString("femalePopulation");
		netMigration = bundle1.getStringArrayList("netMigration");
		inflation = bundle1.getString("inflation");
		imports = bundle1.getString("import");
		Dinvestment = bundle1.getString("foreignDirectInvestment");
		taxRate = bundle1.getString("taxRate");
		agriculture = bundle1.getString("agriculture");
		air = bundle1.getString("air");

		// http://www.youtube.com/watch?v=wG3XPdVPsxU
		SharedPreferences sharedPrefsInternet = getSharedPreferences(variable, 0);
		Editor editorInternet = sharedPrefsInternet.edit();
		editorInternet.putString("PopTot", populationTotal.get(0));
		editorInternet.putString("PopGrow", populationGrowth.get(0));
		editorInternet.putString("Name", countryname);
		editorInternet.putString("Gdp", gdp.get(0));
		editorInternet.putString("GdpGrow", gdpGrowth.get(0));
		editorInternet.putString("Land", landArea);
		editorInternet.putString("Diesel", dieselPrice.get(1));
		editorInternet.putString("Gas", gasolinePrice.get(1));
		editorInternet.putString("FemPop", femalePop);
		editorInternet.putString("Net", netMigration.get(2));
		editorInternet.putString("Inflate", inflation);
		editorInternet.putString("Import", imports);
		editorInternet.putString("Invest", Dinvestment);
		editorInternet.putString("Tax", taxRate);
		editorInternet.putString("Agriculture", agriculture);
		editorInternet.putString("Air", air);
		editorInternet.commit();
	}
	/**
	 * Checks if the given input/resource is available. If available, outputs
	 * resource. Outputs N/A if not.
	 * 
	 * @param content
	 * @return
	 */
	public String checkForNull(String content) {
		if (content == null || content.equals("null") || content.equals("N/A")) {
			return "N/A";
		} else {
			return content;
		}
	}
	/**
	 * Checks if the input/resource is available. If available, outputs resource
	 * formatted to 2 decimal places after the point. Outputs N/A if not.
	 * 
	 * @param value
	 * @return
	 */
	public String formatTo2Decimal(String value) {
		String a = "";
		if (value == null || value.equals("null")) {
			return "N/A";
		} else {
			double f = Double.parseDouble(value);
			DecimalFormat format = new DecimalFormat("###.##");
			a = format.format(f) + "";
			return a;
		}
	}
	/**
	 * Checks if the input/resource is available. If available, outputs resource
	 * formatted with commas by calling on the {@link #insertCommas(String)}
	 * method. Outputs N/A if not.
	 * 
	 * @param str
	 * @return
	 */
	public String setCommas(String population) {
		String value = "";
		if (population == null || population.equals("null")
				|| population.equals("N/A")) {
			value = "N/A";
		} else {
			value = insertCommas(population);
		}
		return value;
	}

	/**
	 * Gets called by the @link {@link #setCommas(String)}. Formats the input
	 * with commas for every three digits. This modified method from
	 * http://www.daniweb.com/software-development/java
	 * /threads/205639/put-comma-in-number-format
	 * 
	 * @param str
	 * @return
	 */
	private String insertCommas(String str) {
		if (str.contains(".")) {
			double myDouble = Double.parseDouble(str);
			DecimalFormat formatter = new DecimalFormat("#,###.##");
			String myString = "" + formatter.format(myDouble);
			return myString;
		} else {
			double myDouble = Double.parseDouble(str);
			DecimalFormat formatter = new DecimalFormat("#,###");
			String myString = "" + formatter.format(myDouble);
			return myString;
		}
	}

	/**
	 * Shows the full table of the People section when the appropriate is pressed.
	 * 
	 * @param view
	 */
	public void peopleOnClick(View view) {
		if (people_Counter == 0) {
			wv_People1.loadUrl("about:blank");
			if (connection) {
				peopleConnections(populationTotal.get(0),
						populationGrowth.get(0), femalePop, netMigration.get(2));
			} else {
				peopleConnections(popInternet, popGrowInternet, femInternet,
						netInternet);
			}

			people_Counter = 1;
		} else {
			wv_People1.loadUrl("about:blank");
			wv_People1.loadData(peopleInfoSmall, "text/html", null);
			people_Counter = 0;
		}

	}
	/**
	 * Universal method for using the variables from Internet/non-Internet
	 * states to load the people section.
	 * 
	 * @param pop
	 * @param popGrow
	 * @param femalePop
	 * @param netMigration
	 */
	public void peopleConnections(String pop, String popGrow, String femalePop,
			String netMigration) {
		String peopleInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">Population:</td><td>"
				+ setCommas(pop)
				+ "</td></tr><tr><td colspan=\"2\">Population Growth</td><td>"
				+ FormatNull(formatTo2Decimal(popGrow),  " %")
				+ "</td></tr><tr><td colspan=\"2\">Female Population</td><td>"
				+ FormatNull(formatTo2Decimal(femalePop), " %")
				+ "</td></tr><tr><td colspan=\"2\">Net Migration</td><td>"
				+ setCommas(netMigration) + "</td></tr></table>";

		wv_People1.loadData(peopleInfoFull, "text/html", null);
	}

	/**
	 * Shows the full table of the Finance section when the appropriate button
	 * is pressed.
	 * 
	 * @param view
	 */
	public void financeOnClick(View view) {
		if (finance_Counter == 0) {
			wv_Finance1.loadUrl("about:blank");
			if (connection) {
				financeConnections(gdp.get(0), gdpGrowth.get(0), inflation,
						imports, Dinvestment, taxRate);
			} else {
				financeConnections(gdpInternet, gdpGrowInternet,
						inflateInternet, importInternet, investInternet,
						taxInternet);
			}

			finance_Counter = 1;
		} else {
			wv_Finance1.loadUrl("about:blank");
			wv_Finance1.loadData(financeInfoSmall, "text/html", null);
			finance_Counter = 0;
		}
	}
	/**
	 * Universal method for using the variables from Internet/non-Internet
	 * states to load the finance section.
	 * 
	 * @param gdp
	 * @param gdpGrow
	 * @param inflation
	 * @param imports
	 * @param Dinvestment
	 * @param taxRate
	 */
	public void financeConnections(String gdp, String gdpGrow,
			String inflation, String imports, String Dinvestment, String taxRate) {
		String setting = "";
		checkForNull(Dinvestment);
		if (!gdp.equals("null")) {
			setting = setCommas(gdp);
		} else {
			setting = "N/A";
		}
		String financeInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">GDP:</td><td>"
				+ setting
				+ "</td></tr><tr><td colspan=\"2\">GDP Growth</td><td>"
				+ FormatNull(formatTo2Decimal(gdpGrow),  " %")
				+ "</td></tr><tr><td colspan=\"2\">Inflation</td><td>"
				+ FormatNull(formatTo2Decimal(inflation), " %")
				+ "</td></tr><tr><td colspan=\"2\">Imports</td><td>"
				+ FormatNull(formatTo2Decimal(imports), " %")
				+ "</td></tr><tr><td colspan=\"2\">Foreign Direct Investment</td><td>"
				+ FormatNull(checkForNull(setCommas(Dinvestment)),  " $")
				+ "</td></tr><tr><td colspan=\"2\">Tax Rate</td><td>"
				+ FormatNull(formatTo2Decimal(taxRate), " %") + "</td></tr></table>";

		wv_Finance1.loadData(financeInfoFull, "text/html", null);
		finance_Counter = 0;
	}

	/**
	 * Shows the full table of the Energy section when the appropriate button is pressed.
	 * @param view
	 */
	public void energyOnClick(View view) {
		if (Energy_Counter == 0) {
			wv_Energy1.loadUrl("about:blank");
			if (connection) {
				energyConnections(landArea, dieselPrice.get(1),
						gasolinePrice.get(1), air, agriculture);
			} else {
				energyConnections(landInternet, dieselInternet, gasInternet,
						airInternet, agricultureInternet);
			}
			Energy_Counter = 1;
		} else {
			wv_Energy1.loadUrl("about:blank");
			wv_Energy1.loadData(energyInfoSmall, "text/html", null);
			Energy_Counter = 0;
		}
	}
	/**
	 * Universal method for using the variables from Internet/non-Internet
	 * states to load the energy section.
	 * 
	 * @param landArea
	 * @param diesel
	 * @param gas
	 * @param air
	 * @param agriculture
	 */
	public void energyConnections(String landArea, String diesel, String gas,
			String air, String agriculture) {

		String energyInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\">Land Area:</td><td>"
				+ FormatNull(checkForNull(setCommas(landArea)), " km<sup>2</sup>")
				+ "</td></tr><tr><td colspan=\"2\">Diesel Price</td><td>"
				+ FormatNull(formatTo2Decimal(diesel), " $")
				+ "</td></tr><tr><td colspan=\"2\">Gasoline Price</td><td>"
				+ FormatNull(formatTo2Decimal(gas), " $")
				+ "</td></tr><tr><td colspan=\"2\">Air Transport</td><td>"
				+ setCommas(air)
				+ "</td></tr><tr><td colspan=\"2\">Agriculture</td><td>"
				+ FormatNull(formatTo2Decimal(agriculture), " %") + "</td></tr></table>";

		wv_Energy1.loadData(energyInfoFull, "text/html", null);
	}

	/**
	 * intent to take the user back to the menu page and finishes current activity
	 * @param view
	 */
	public void homeOnCLick(View view) {
		Intent intent = new Intent(this, MenuPage.class);
		startActivity(intent);
		finish();
	}

	/**
	 * This method initialises the objects in the layout, it also gets relevant data 
	 * depending on connection, if it has connection it will show the country the user
	 * clicked on when at the country selector page, if it doesnt, it will display the
	 * last viewed country's data.
	 * DisplayMetrics was taken from the example: http://developer.android.com/reference/android/util/DisplayMetrics.html
	 */

	public void initialize() {
		b_Home = (Button) findViewById(R.id.B_Home);
		b_Graph = (Button) findViewById(R.id.B_Graph);
		b_People = (Button) findViewById(R.id.B_People);
		b_Finance = (Button) findViewById(R.id.B_Finance);
		b_Energy = (Button) findViewById(R.id.B_Energy);
		wv_People1 = (WebView) findViewById(R.id.wv_People1);
		wv_Finance1 = (WebView) findViewById(R.id.wv_Finance1);
		wv_Energy1 = (WebView) findViewById(R.id.wv_Energy1);
		tv_CountryName = (TextView) findViewById(R.id.tv_CountryName);
		b_Favourites = (Button) findViewById(R.id.B_Favourites);

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

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi.isConnected() || mMobile.isConnected()) {
			connection = true;
			Bundle bundle1 = getIntent().getExtras().getBundle("CountryOne");
			countryKey = bundle1.getString("CountryKey");
			countryName = bundle1.getString("CountryName");

			// This states whether currently viewed country is already a
			// favourite
			for (int i = 0; i < (preferecesNamesSize / 2); i++) {
				if (preferences.getString(preferencesNames[(i * 2) + 1], "")
						.contains(countryKey)) {
					isFavourite = true;
				}
			}

			if (isFavourite == true) {
				// This WILL be an image later
				b_Favourites.setText("Remove from Favorites");
			} else {
				b_Favourites.setText("Add to Favorites");
			}
		} else {
			Toast.makeText(this, "No Network available. Recent Data uploaded!",
					Toast.LENGTH_LONG).show();
			connection = false;
			b_Favourites.setVisibility(View.GONE);
			b_Graph.setVisibility(View.GONE);
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
		    b_Home.setWidth(metrics.widthPixels);
			SharedPreferences sharedP = getSharedPreferences(variable, 0);
			popInternet = sharedP.getString("PopTot", "null");
			popGrowInternet = sharedP.getString("PopGrow", "null");
			nameInternet = sharedP.getString("Name", "No Saved Data");
			gdpInternet = sharedP.getString("Gdp", "null");
			gdpGrowInternet = sharedP.getString("GdpGrow", "null");
			landInternet = sharedP.getString("Land", "null");
			dieselInternet = sharedP.getString("Diesel", "null");
			gasInternet = sharedP.getString("Gas", "null");
			femInternet = sharedP.getString("FemPop", "null");
			netInternet = sharedP.getString("Net", "null");
			inflateInternet = sharedP.getString("Inflate", "null");
			importInternet = sharedP.getString("Import", "null");
			investInternet = sharedP.getString("Invest", "null");
			taxInternet = sharedP.getString("Tax", "null");
			agricultureInternet = sharedP.getString("Agriculture", "null");
			airInternet = sharedP.getString("Air", "null");

		}
	}

	/**
	 * Tests if variable @isFavourite is true if true: deletes strings from
	 * sharedpreferences by overwriting them with "" changes text on button if
	 * false: checks if favourites is full if full toast is sent which informs
	 * user if not full:
	 * 
	 * @param view
	 */
	public void onFavouriteButtonClick(View view) {

		if (isFavourite == true) {
			// Simply loop through all sharedpreferences, find & delete
			// Problem could arise on the Favourites page with how it's
			// displayed if there's a gap
			// Shouldn't do actually..
			for (int i = 0; i < (preferecesNamesSize / 2); i++) {
				if (preferences.getString(preferencesNames[((i * 2) + 1)], "")
						.contains(countryKey)) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(preferencesNames[i * 2], "");
					editor.putString(preferencesNames[((i * 2) + 1)], "");
					editor.commit();
					isFavourite = false;
					b_Favourites.setText("Add to Favorites");
				}
			}

		} else {
			// Checks if favourites are full by checking the
			if (preferences.getString(
					preferencesNames[preferencesNames.length - 1], "") != "") {
				// Communication stating Favourites is full
				Context context = getApplicationContext();
				CharSequence warning = "Favourites full - you must remove some favourites before adding more.";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, warning, duration);
				toast.show();
			}
			// add to favourites
			else {
				// loop to find first available
				int i = 0;
				while (preferences.getString(preferencesNames[i], "") != "") {
					i++;
				}
				if (i > preferecesNamesSize) {
				}
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(preferencesNames[i], countryName);
				editor.putString(preferencesNames[i + 1], countryKey);
				editor.commit();

				isFavourite = true;
				b_Favourites.setText("Remove from Favorites");
			}
		}
	}

	/**
	 * an intent which goes to the menu class, this finishes the current activity too
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
	 * intent that takes you to graph page, also passes the country name through
	 * so it can be added to the graph page list
	 * @param view
	 */
	public void onGraphClick(View view) {
		Intent intent = new Intent(this, Graph.class);
		intent.putExtra("CountryName", countryname);
		startActivity(intent);
	}
	
	/**
	 * Checks if data is null, it it is, it returns it, if it isnt, it adds the symbol
	 * @param data
	 * @param symbol
	 * @return
	 */
	public String FormatNull(String data, String symbol)
	{
		if(data.equals("N/A"))
		{
			return data;
		}
		else if(symbol.contains("%")||symbol.contains("km"))
		{
			return data + symbol;
		}
		else
		{
			return symbol + data;
		}
	}
}