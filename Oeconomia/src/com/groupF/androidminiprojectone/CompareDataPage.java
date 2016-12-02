package com.groupF.androidminiprojectone;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class CompareDataPage extends Activity {

	Button b_comapre, b_Home, b_People, b_Finance, b_Energy;
	WebView wv_Finance, wv_People, wv_Energy;
	TextView tv_FirstCountry, tv_SecondCountry;
	int people_Counter, finance_Counter, Energy_Counter;
	ArrayList<String> firstPopulationTotal, secondPopulationTotal;
	ArrayList<String> firstPopulationGrowth, secondPopulationGrowth;
	String firstCountryName, secondCountryName;
	ArrayList<String> first_GDP, second_GDP;
	ArrayList<String> first_GDP_Growth, second_GDP_Growth;
	String first_Land_Area, second_Land_Area;
	ArrayList<String> first_Diesel_Price, second_Diesel_Price;
	ArrayList<String> first_Gasoline_Price, second_Gasoline_Price;
	String first_Female_Pop, second_Female_Pop;
	ArrayList<String> first_NetMigration, second_NetMigration;
	String first_Inflation, second_Inflation;
	String first_Imports, second_Imports;
	String first_Dinvestment, second_Dinvestment;
	String first_TaxRate, second_TaxRate;
	String first_Agriculture, second_Agriculture;
	String first_Air, second_Air;
	Bundle bundle1, bundle2;

	String peopleInfoSmall, financeInfoSmall, energyInfoSmall;

	// Declaring the GestureDetector
	private GestureDetectorCompat mDetector;

	public SharedPreferences preferences;
	/**
	 * This method is where the class starts, it calls other methods and creates the GUI including themes
	 * It does minimal formatting but it does create and display the initial information when the button is
	 * at its initial state (minimized state)
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

		setContentView(R.layout.activity_compare_data_page);

		// Instatntiation of the GestureDetector with the application context
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		initialize();
		collectBundles();

		tv_FirstCountry.setText(firstCountryName);
		tv_SecondCountry.setText(secondCountryName);
		
		finance_Counter = 0;
		people_Counter = 0;
		Energy_Counter = 0;

		String setting1 = "";
		if (!first_GDP.get(0).equals("null")) 
		{
			setting1 = setCommas(first_GDP.get(0));
		} 
		else 
		{
			setting1 = "N/A";
		}
		String setting2 = "";
		if (!second_GDP.get(0).equals("null")) 
		{
			setting2 = setCommas(second_GDP.get(0));
		} 
		else 
		{
			setting2 = "N/A";
		}
		
		peopleInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>Population</center></td></tr><tr><td><center>"
				+ setCommas(firstPopulationTotal.get(0))
				+ "</center></td><td><center>"
				+ setCommas(secondPopulationTotal.get(0))
				+ "</center></td></tr><tr><td colspan=\"2\"><center>Population Growth</center></td></tr><tr><td><center>"
				+ FormatNull(formatTo2Decimal(firstPopulationGrowth.get(0)), " %")
				+ "</center></td><td><center>"
				+ FormatNull(formatTo2Decimal(secondPopulationGrowth.get(0)), " %")
				+ "</center></td></tr></table>";

		wv_People.loadData(peopleInfoSmall, "text/html", null);

		financeInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>GDP</center></td></tr><tr><td><center>"
				+ setting1
				+ "</center></td><td><center>"
				+ setting2
				+ "</center></td></tr><tr><td colspan=\"2\"><center>GDP Growth</center></td></tr><tr><td><center>"
				+ FormatNull(formatTo2Decimal(first_GDP_Growth.get(0)), " %")
				+ "</center></td><td><center>"
				+ FormatNull(formatTo2Decimal(second_GDP_Growth.get(0)), " %")
				+ "</center></td></tr></table>";

		wv_Finance.loadData(financeInfoSmall, "text/html", null);

		energyInfoSmall = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>Land Area</center></td></tr><tr><td><center>"
				+ FormatNull(checkForNull(setCommas(first_Land_Area)), " km<sup>2</sup>")
				+ "</center></td><td><center>"
				+ FormatNull(checkForNull(setCommas(second_Land_Area)), " km<sup>2</sup>")
				+ "</center></td></tr><tr><td colspan=\"2\"><center>Diesel Price</center></td></tr><tr><td><center>"
				+ FormatNull(formatTo2Decimal(first_Diesel_Price.get(1)), " $")
				+ "</center></td><td><center>"
				+ FormatNull(formatTo2Decimal(second_Diesel_Price.get(1)), " $")
				+ "</center></td></tr><tr><td colspan=\"2\"><center>Gasoline Price</center></td></tr><tr><td><center>"
				+ FormatNull(formatTo2Decimal(first_Gasoline_Price.get(1)), " $")
				+ "</center></td><td><center>"
				+ FormatNull(formatTo2Decimal(second_Gasoline_Price.get(1)), " $")
				+ "</center></td></tr></table>";

		wv_Energy.loadData(energyInfoSmall, "text/html", null);

	}
	/**
	 * This method collects the data from the country selector page and unwraps it for each country
	 * so that it can be used for other methods, this method is called from the onCreate method
	 */
	public void collectBundles() {

		Bundle bundle1 = getIntent().getExtras().getBundle("CountryOne");
		firstPopulationTotal = bundle1.getStringArrayList("PopulationList");
		firstPopulationGrowth = bundle1.getStringArrayList("PopultionGrowth");
		firstCountryName = bundle1.getString("countryName");
		first_GDP = bundle1.getStringArrayList("gdp");
		first_GDP_Growth = bundle1.getStringArrayList("gdpGrowth");
		first_Land_Area = bundle1.getString("landArea");
		first_Diesel_Price = bundle1.getStringArrayList("desiel");
		first_Gasoline_Price = bundle1.getStringArrayList("gasoline");
		first_Female_Pop = bundle1.getString("femalePopulation");
		first_NetMigration = bundle1.getStringArrayList("netMigration");
		first_Inflation = bundle1.getString("inflation");
		first_Imports = bundle1.getString("import");
		first_Dinvestment = bundle1.getString("foreignDirectInvestment");
		first_TaxRate = bundle1.getString("taxRate");
		first_Agriculture = bundle1.getString("agriculture");
		first_Air = bundle1.getString("air");

		bundle2 = getIntent().getExtras().getBundle("CountryTwo");

		secondPopulationTotal = bundle2.getStringArrayList("PopulationList");
		secondPopulationGrowth = bundle2.getStringArrayList("PopultionGrowth");
		secondCountryName = bundle2.getString("countryName");
		second_GDP = bundle2.getStringArrayList("gdp");
		second_GDP_Growth = bundle2.getStringArrayList("gdpGrowth");
		second_Land_Area = bundle2.getString("landArea");
		second_Diesel_Price = bundle2.getStringArrayList("desiel");
		second_Gasoline_Price = bundle2.getStringArrayList("gasoline");
		second_Female_Pop = bundle2.getString("femalePopulation");
		second_NetMigration = bundle2.getStringArrayList("netMigration");
		second_Inflation = bundle2.getString("inflation");
		second_Imports = bundle2.getString("import");
		second_Dinvestment = bundle2.getString("foreignDirectInvestment");
		second_TaxRate = bundle2.getString("taxRate");
		second_Agriculture = bundle2.getString("agriculture");
		second_Air = bundle2.getString("air");
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
	 * This modified method from http://www.daniweb.com/software-development/java
	 * /threads/205639/put-comma-in-number-format
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
	 * @param population
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
	 * Shows the full table of the People section when the appropriate is
	 * pressed (maximized state), if pressed again, reverts back to minimized state.
	 * 
	 * @param view
	 */
	public void peopleOnClick(View view) {

		if (people_Counter == 0) {
			wv_People.loadUrl("about:blank");
			
			String peopleInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>Population</center></td></tr><tr><td><center>"
					+ setCommas(firstPopulationTotal.get(0))
					+ "</center></td><td><center>"
					+ setCommas(secondPopulationTotal.get(0))
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Population Growth</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(firstPopulationGrowth.get(0)), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(secondPopulationGrowth.get(0)), " %")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Female Population</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Female_Pop), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Female_Pop), " %")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Net Migration</center></td></tr><tr><td><center>"
					+ setCommas(first_NetMigration.get(2))
					+ "</center></td><td><center>"
					+ setCommas(second_NetMigration.get(2))
					+ "</center></td></tr></table>";

			wv_People.loadData(peopleInfoFull, "text/html", null);

			people_Counter = 1;
		} else {
			wv_People.loadUrl("about:blank");
			
			wv_People.loadData(peopleInfoSmall, "text/html", null);

			people_Counter = 0;
		}

	}
	/**
	 * Shows the full table of the Finance section when the appropriate button
	 * is pressed, works in same way as peopleOnClick method.
	 * 
	 * @param view
	 */
	public void financeOnClick(View view) {
		if (finance_Counter == 0) {
			wv_Finance.loadUrl("about:blank");
			
			String setting1 = "";
					checkForNull(first_Dinvestment);
					if (!first_GDP.get(0).equals("null")) 
					{
						setting1 = setCommas(first_GDP.get(0));
					} 
					else 
					{
						setting1 = "N/A";
					}
			String setting2 = "";
					checkForNull(second_Dinvestment);
					if (!second_GDP.get(0).equals("null")) 
					{
						setting2 = setCommas(second_GDP.get(0));
					} 
					else 
					{
						setting2 = "N/A";
					}
			String financeInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>GDP</center></td></tr><tr><td><center>"
					+ setting1
					+ "</center></td><td><center>"
					+ setting2
					+ "</center></td></tr><tr><td colspan=\"2\"><center>GDP Growth</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_GDP_Growth.get(0)), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_GDP_Growth.get(0)), " %")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Inflation</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Inflation), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Inflation), " %")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Imports</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Imports), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Imports), " %")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Foreign Direct Investment</center></td></tr><tr><td><center>"
					+ FormatNull(checkForNull(setCommas(first_Dinvestment)), " $")
					+ "</center></td><td><center>"
					+ FormatNull(checkForNull(setCommas(second_Dinvestment)), " $")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Tax Rate</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_TaxRate), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_TaxRate), " %")
					+ "</center></td></tr></table>";

			wv_Finance.loadData(financeInfoFull, "text/html", null);

			finance_Counter = 1;
		} else {
			wv_Finance.loadUrl("about:blank");
			
			wv_Finance.loadData(financeInfoSmall, "text/html", null);

			finance_Counter = 0;
		}
	}
	/**
	 * Shows the full table of the Energy section when the appropriate button is
	 * pressed, works in same way as peopleOnClick method.
	 * 
	 * @param view
	 */
	public void energyOnClick(View view) {
		if (Energy_Counter == 0) {
			wv_Energy.loadUrl("about:blank");
			
			String energyInfoFull = "<table border=\"1\" bordercolor=\"#CCCCCC\" style=\"background-color:#80000000\" width=\"100%\" cellpadding=\"3\" cellspacing=\"3\"><tr><td colspan=\"2\"><center>Land Area</center></td></tr><tr><td><center>"
					+ FormatNull(checkForNull(setCommas(first_Land_Area)), " km<sup>2</sup>")
					+ "</center></td><td><center>"
					+ FormatNull(checkForNull(setCommas(second_Land_Area)), " km<sup>2</sup>")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Diesel Price</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Diesel_Price.get(1)), " $")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Diesel_Price.get(1)), " $")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Gasoline Price</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Gasoline_Price.get(1)), " $")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Gasoline_Price.get(1)), " $")
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Air Transport</center></td></tr><tr><td><center>"
					+ setCommas(first_Air)
					+ "</center></td><td><center>"
					+ setCommas(second_Air)
					+ "</center></td></tr><tr><td colspan=\"2\"><center>Agriculture</center></td></tr><tr><td><center>"
					+ FormatNull(formatTo2Decimal(first_Agriculture), " %")
					+ "</center></td><td><center>"
					+ FormatNull(formatTo2Decimal(second_Agriculture), " %") + "</center></td></tr></table>";

			wv_Energy.loadData(energyInfoFull, "text/html", null);

			Energy_Counter = 1;
		} else {
			wv_Energy.loadUrl("about:blank");
			
			wv_Energy.loadData(energyInfoSmall, "text/html", null);

			Energy_Counter = 0;
		}
	}
	/**
	 * an intent which goes to the menu class, this finishes the current activity too
	 */
	public void homeOnCLick(View view) {
		Intent intent = new Intent(this, MenuPage.class);
		startActivity(intent);
		finish();
	}

	/**
	 * This method initialises the objects in the layout
	 */
	public void initialize() {
		b_Home = (Button) findViewById(R.id.B_Home);
		b_People = (Button) findViewById(R.id.B_People);
		b_Finance = (Button) findViewById(R.id.B_Finance);
		b_Energy = (Button) findViewById(R.id.B_Energy);
		wv_People = (WebView) findViewById(R.id.wv_People);
		wv_Finance = (WebView) findViewById(R.id.wv_Finance);
		wv_Energy = (WebView) findViewById(R.id.wv_Energy);
		tv_FirstCountry = (TextView) findViewById(R.id.tv_FirstCountry);
		tv_SecondCountry = (TextView) findViewById(R.id.tv_SecondCountry);
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

