package com.groupF.androidminiprojectone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class allows the users to compare the data between different countries
 * visually. There is a set of widgets to give the user to compare data for
 * particular year and for up to 5 countries.
 * 
 * @author Group F
 * 
 */
public class Graph extends Activity {

	Spinner spinner_countries, spinner_indicators;

	SeekBar seekbar_sYear, seekbar_eYear;

	TextView tv_Start, tv_End;
	ListView lv_favourites;
	ArrayList<String> countries, keys, favouriteCountries, indicators,
			indicatorKey;
	Button B_plus, B_negative;
	HashMap<String, String> hashMap;
	ArrayAdapter<String> favAdapter, indicatorAdapter;
	String value = "";
	RadioButton Scatter, barChart, LineChart;
	SharedPreferences preferences;

	boolean network = false;

	boolean nullGraph = false;

	// Declaring the GestureDetector
	private GestureDetectorCompat mDetector;

	/**
	 * This method traverses through the content of an array and returns true if
	 * all the values are not "null"
	 * 
	 * @param content
	 *            This is the arraylist to check!
	 * @return
	 */
	public boolean checkOneArray(ArrayList<String> content, String countryName) {
		boolean bool = false;
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i) != "null") {
				return true;
			}
		}
		if (bool == false) {
			Toast.makeText(
					this,
					"There was no data for " + countryName
							+ " please remove it and continue",
					Toast.LENGTH_SHORT).show();
		}
		return bool;

	}

	/**
	 * This method checks if there is a network connection.
	 * http://stackoverflow.
	 * com/questions/9570237/android-check-internet-connection
	 * 
	 * @return It returns a true value if there is connection and false if there
	 *         isn't.
	 */
	public boolean checkNetworkConnecttion() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo InternetConnection = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi.isConnected()) {
			// Do whatever
			return true;
		}
		if (InternetConnection.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method links the DataPage.class 'getGraph' button to the current
	 * activity,
	 */
	public void getCountryName() {
		String countryName = getIntent().getExtras().getString("CountryName");
		if (countryName.equals("")) {

		} else {
			for (int i = 0; i < countries.size(); i++) {
				if (countryName.equals(countries.get(i))) {
					String key = keys.get(i);
					hashMap.put(countryName, key);

					favouriteCountries.add(countryName);
					lv_favourites.setAdapter(favAdapter);
				}
			}
		}

	}

	/**
	 * This is the action listener on the 'get Graph' button it would generate a
	 * graph depending on user's choice.
	 * 
	 * @param view
	 */
	public void getGraphButtonOnCLick(View view) {
		if (Integer.parseInt(tv_Start.getText().toString()) < Integer
				.parseInt(tv_End.getText().toString())) {
			if (checkNetworkConnecttion()) {
				String htmlvalue = "";
				if (favouriteCountries.size() == 0) {
					Toast.makeText(this, "No country has been selected ",
							Toast.LENGTH_SHORT).show();
				}
				if (favouriteCountries.size() == 1) {

					ParseData getData = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(0))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getData.start();

					try {
						getData.join();
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					htmlvalue = "['Year','"
							+ spinner_indicators.getSelectedItem() + "'],";
					for (int i = 0; i < getData.getList().size(); i++) {
						if (i == getData.getList().size() - 1) {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + "]";
						} else {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + "],";
						}
					}

					if (checkOneArray(getData.getList(),
							getData.getCountryName())) {
						Intent intent = null;

						String title = spinner_indicators.getSelectedItem()
								+ " of " + lv_favourites.getItemAtPosition(0);
						if (Scatter.isChecked()) {
							intent = new Intent(this, ScatterDiagram.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title", title);
						}
						if (LineChart.isChecked()) {
							intent = new Intent(this, LineChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title", title);
						}
						if (barChart.isChecked()) {
							intent = new Intent(this, BarChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title", title);
						}

						startActivity(intent);
					} else {
						Toast.makeText(this, "There was no data to use ",
								Toast.LENGTH_LONG).show();
					}

				}
				if (favouriteCountries.size() == 2) {
					// http://api.worldbank.org/countries/GB/indicators/SP.POP.TOTL?date=1960:2009&format=json
					ParseData getData = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(0))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getData.start();

					ParseData getDataTwo = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(1))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataTwo.start();

					try {
						getData.join();
						getDataTwo.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					htmlvalue = "['Year','"
							+ lv_favourites.getItemAtPosition(0) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(1) + " "
							+ spinner_indicators.getSelectedItem() + "'],";

					for (int i = 0; i < getData.getList().size(); i++) {
						if (i == getData.getList().size() - 1) {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + "]";
						} else {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + "],";
						}

					}

					if (checkOneArray(getData.getList(),
							getData.getCountryName())
							&& checkOneArray(getDataTwo.getList(),
									getDataTwo.getCountryName())) {
						Intent intent = null;

						if (Scatter.isChecked()) {
							intent = new Intent(this, ScatterDiagram.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (LineChart.isChecked()) {
							intent = new Intent(this, LineChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (barChart.isChecked()) {
							intent = new Intent(this, BarChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}

						startActivity(intent);
					} else {
						Toast.makeText(this, "There was no data to use ",
								Toast.LENGTH_SHORT).show();

					}
				}
				if (favouriteCountries.size() == 3) {
					// http://api.worldbank.org/countries/GB/indicators/SP.POP.TOTL?date=1960:2009&format=json
					ParseData getData = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(0))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getData.start();
					// String myValue = getData.url;

					ParseData getDataTwo = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(1))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataTwo.start();

					ParseData getDataThree = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(2))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataThree.start();
					try {
						getData.join();
						getDataTwo.join();
						getDataThree.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					htmlvalue = "['Year','"
							+ lv_favourites.getItemAtPosition(0) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(1) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(2) + " "
							+ spinner_indicators.getSelectedItem() + "'],";

					for (int i = 0; i < getData.getList().size(); i++) {
						if (i == getData.getList().size() - 1) {

							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + "]";
						} else {

							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + "],";
						}

					}

					if (checkOneArray(getData.getList(),
							getData.getCountryName())
							&& checkOneArray(getDataTwo.getList(),
									getDataTwo.getCountryName())
							&& checkOneArray(getDataThree.getList(),
									getDataThree.getCountryName())) {
						Intent intent = null;

						if (Scatter.isChecked()) {
							intent = new Intent(this, ScatterDiagram.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (LineChart.isChecked()) {
							intent = new Intent(this, LineChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (barChart.isChecked()) {
							intent = new Intent(this, BarChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}

						startActivity(intent);
					} else {
						Toast.makeText(this, "There was no data to use ",
								Toast.LENGTH_SHORT).show();
					}
				}
				if (favouriteCountries.size() == 4) {
					// http://api.worldbank.org/countries/GB/indicators/SP.POP.TOTL?date=1960:2009&format=json
					ParseData getData = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(0))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getData.start();

					ParseData getDataTwo = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(1))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataTwo.start();

					ParseData getDataThree = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(2))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataThree.start();
					ParseData getDataFour = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(3))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataFour.start();
					try {

						getData.join();
						getDataTwo.join();
						getDataThree.join();
						getDataFour.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					htmlvalue = "['Year','"
							+ lv_favourites.getItemAtPosition(0) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(1) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(2) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(3) + " "
							+ spinner_indicators.getSelectedItem() + "'],";

					for (int i = 0; i < getData.getList().size(); i++) {
						if (i == getData.getList().size() - 1) {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + ","
									+ getDataFour.getList().get(i) + "]";
						} else {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + ","
									+ getDataFour.getList().get(i) + "],";
						}

					}

					if (checkOneArray(getData.getList(),
							getData.getCountryName())
							&& checkOneArray(getDataTwo.getList(),
									getDataTwo.getCountryName())
							&& checkOneArray(getDataThree.getList(),
									getDataThree.getCountryName())
							&& checkOneArray(getDataFour.getList(),
									getDataFour.getCountryName())) {
						Intent intent = null;

						if (Scatter.isChecked()) {
							intent = new Intent(this, ScatterDiagram.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (LineChart.isChecked()) {
							intent = new Intent(this, LineChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (barChart.isChecked()) {
							intent = new Intent(this, BarChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}

						startActivity(intent);
					} else {
						Toast.makeText(this, "There was no data to use ",
								Toast.LENGTH_SHORT).show();
					}
				}
				if (favouriteCountries.size() == 5) {
					// http://api.worldbank.org/countries/GB/indicators/SP.POP.TOTL?date=1960:2009&format=json
					ParseData getData = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(0))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getData.start();

					ParseData getDataTwo = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(1))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataTwo.start();

					ParseData getDataThree = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(2))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataThree.start();
					ParseData getDataFour = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(3))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataFour.start();
					ParseData getDataFive = new ParseData(
							"http://api.worldbank.org/countries/"
									+ hashMap.get(lv_favourites
											.getItemAtPosition(4))
									+ "/indicators/"
									+ indicatorKey.get(spinner_indicators
											.getSelectedItemPosition())
									+ "?date=" + tv_Start.getText() + ":"
									+ tv_End.getText() + "&format=json");
					getDataFive.start();
					try {

						getData.join();
						getDataTwo.join();
						getDataThree.join();
						getDataFour.join();
						getDataFive.join();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					htmlvalue = "['Year','"
							+ lv_favourites.getItemAtPosition(0) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(1) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(2) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(3) + " "
							+ spinner_indicators.getSelectedItem() + "','"
							+ lv_favourites.getItemAtPosition(4) + " "
							+ spinner_indicators.getSelectedItem() + "'],";

					for (int i = 0; i < getData.getList().size(); i++) {
						if (i == getData.getList().size() - 1) {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + ","
									+ getDataFour.getList().get(i) + ","
									+ getDataFive.getList().get(i) + "]";
						} else {
							if (getData.getList().get(i) != "null") {
								nullGraph = true;
							}
							htmlvalue += "['" + getData.getDatesList().get(i)
									+ "'," + getData.getList().get(i) + ","
									+ getDataTwo.getList().get(i) + ","
									+ getDataThree.getList().get(i) + ","
									+ getDataFour.getList().get(i) + ","
									+ getDataFive.getList().get(i) + "],";
						}

					}

					if (checkOneArray(getData.getList(),
							getData.getCountryName())
							&& checkOneArray(getDataTwo.getList(),
									getDataTwo.getCountryName())
							&& checkOneArray(getDataThree.getList(),
									getDataThree.getCountryName())
							&& checkOneArray(getDataFour.getList(),
									getDataFour.getCountryName())
							&& checkOneArray(getDataFive.getList(),
									getDataFive.getCountryName())) {
						Intent intent = null;

						if (Scatter.isChecked()) {
							intent = new Intent(this, ScatterDiagram.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (LineChart.isChecked()) {
							intent = new Intent(this, LineChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}
						if (barChart.isChecked()) {
							intent = new Intent(this, BarChart.class);
							intent.putExtra("value", htmlvalue);
							intent.putExtra("title",
									spinner_indicators.getSelectedItem()
											+ " Comparison");
						}

						startActivity(intent);
					} else {
						Toast.makeText(this, "There was no data to use ",
								Toast.LENGTH_SHORT).show();
					}
				}

			} else {
				Toast.makeText(this, "Please check your network connection",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Please Select Appropriate years",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * This method is an actionlistener for the plus button it add the value
	 * selected in the country_Spinner to the list of countries to compare.
	 * 
	 * @param view
	 */
	public void onClickPlusButton(View view) {
		if (countries.size() == 0) {
			Toast.makeText(this, "There is a problem with your Network." + "",
					Toast.LENGTH_SHORT).show();
		} else {
			if (favouriteCountries.size() == 5) {
				Toast.makeText(this,
						"You have entered the maximmum number of countries",
						Toast.LENGTH_SHORT).show();
			} else {
				String country = spinner_countries.getSelectedItem().toString();
				if (favouriteCountries.contains(country)) {
					Toast.makeText(this, "This country has been selected",
							Toast.LENGTH_SHORT).show();

				} else {
					String key = keys.get(spinner_countries
							.getSelectedItemPosition());
					hashMap.put(country, key);

					favouriteCountries.add(country);
					lv_favourites.setAdapter(favAdapter);
				}
			}
		}

	}

	/**
	 * This method is an actionlistener for the minus button it removes the
	 * selected element from the listview.
	 * 
	 * @param view
	 */
	public void onCLickNegativeButton(View view) {
		if (favouriteCountries.size() == 0) {
			Toast.makeText(this, "The list does not contain any country",
					Toast.LENGTH_SHORT).show();

		} else {
			if (value.length() > 0) {
				favouriteCountries.remove(value);
				lv_favourites.setAdapter(favAdapter);
				value = "";
			} else {
				Toast.makeText(this, "No country has been selected",
						Toast.LENGTH_SHORT).show();
			}

		}

	}
	/**
	 * This method is the first method that runs in this class, it allocates the theme and sets everything for
	 * the GUI, it also has on click listeners for seekbar functionality. The country and indicator spinners are
	 * populated including the data structures the information is held in
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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

		setContentView(R.layout.activity_graph);

		// Spinners and textviews below them :D
		tv_Start = (TextView) findViewById(R.id.tv_StaryYear);
		tv_End = (TextView) findViewById(R.id.tv_endYear);
		spinner_countries = (Spinner) findViewById(R.id.spinner_Countries);
		spinner_indicators = (Spinner) findViewById(R.id.spinner_Indicator);
		indicators = new ArrayList<String>();
		indicatorKey = new ArrayList<String>();
		indicators.add("GDP");
		indicators.add("GDP Growth");
		indicators.add("Population Growth");
		indicators.add("Population");
		indicators.add("Agricultural land");
		indicators.add("Foreign direct investment");
		indicators.add("Alternative and nuclear energy");
		indicators.add("Energy Production");
		indicators.add("Energy Use");
		indicators.add("Inflation");
		indicators.add("Tax Rate");
		indicatorKey.add("NY.GDP.MKTP.CD");
		indicatorKey.add("NY.GDP.MKTP.KD.ZG");
		indicatorKey.add("SP.POP.GROW");
		indicatorKey.add("SP.POP.TOTL");
		indicatorKey.add("AG.LND.AGRI.ZS");
		indicatorKey.add("BX.KLT.DINV.CD.WD");
		indicatorKey.add("EG.USE.COMM.CL.ZS");
		indicatorKey.add("EG.EGY.PROD.KT.OE");
		indicatorKey.add("EG.USE.PCAP.KG.OE");
		indicatorKey.add("FP.CPI.TOTL.ZG");
		indicatorKey.add("IC.TAX.TOTL.CP.ZS");
		indicatorAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, indicators);
		spinner_indicators.setAdapter(indicatorAdapter);
		seekbar_sYear = (SeekBar) findViewById(R.id.seekbar_sYear);
		seekbar_sYear.setMax(48);

		seekbar_sYear.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				int current = 1960 + progress;
				tv_Start.setText(current + "");

			}
		});

		seekbar_eYear = (SeekBar) findViewById(R.id.seekbar_eYear);
		seekbar_eYear.setMax(48);
		seekbar_eYear.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int current = 1961 + progress;
				tv_End.setText(current + "");
			}
		});

		countries = new ArrayList<String>();
		keys = new ArrayList<String>();
		ParseCountry parseCountries = new ParseCountry(
				"http://api.worldbank.org/countries//all/?format=json&per_page=256");
		parseCountries.start();
		try {
			parseCountries.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parseCountries.orderList();
		countries = parseCountries.getCountries();
		keys = parseCountries.getKeys();

		ArrayAdapter<String> countriesArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item, countries);
		spinner_countries.setAdapter(countriesArrayAdapter);

		// listview favourites
		lv_favourites = (ListView) findViewById(R.id.lv_favourites);
		B_negative = (Button) findViewById(R.id.B_Negative);
		B_plus = (Button) findViewById(R.id.B_plus);

		// list of favourites
		hashMap = new HashMap<String, String>();
		favouriteCountries = new ArrayList<String>();
		favAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, favouriteCountries);
		lv_favourites.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				value = lv_favourites.getItemAtPosition(arg2).toString();

			}
		});
		Scatter = (RadioButton) findViewById(R.id.RB_Scatter);
		LineChart = (RadioButton) findViewById(R.id.RB_lineChart);
		barChart = (RadioButton) findViewById(R.id.RB_barChart);
		getCountryName();

	}

	/**
	 * This is an Inner class to parse JSON for the Graph.class activity, it
	 * parses the data from the start year to the end year.
	 * 
	 * @author Group F
	 * 
	 */
	public class ParseData extends Thread {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		String url;
		String countryName = new String();
		String data;

		public ParseData(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			data = readData(url);

			try {
				// Full data Array
				JSONArray arrayOne = new JSONArray(data);

				// Full Data Needed
				JSONArray MainArray = arrayOne.getJSONArray(1);
				for (int i = MainArray.length() - 1; i >= 0; i--) {
					JSONObject object = MainArray.getJSONObject(i);
					JSONObject country = object.getJSONObject("country");
					countryName = (String) country.get("value");

					String date = object.get("date").toString();
					dates.add(date);
					String value = object.get("value").toString();
					list.add(value);

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public ArrayList<String> getList() {
			return list;
		}

		public String getCountryName() {
			return countryName;
		}

		public ArrayList<String> getDatesList() {
			return dates;
		}
	}
	/**
	 * method given to us for reading the url
	 * @param url
	 * @return
	 */
	public static String readData(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		StringBuilder content = new StringBuilder();
		try {

			HttpResponse response = client.execute(get);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				// Build string from input stream
				String readLine = reader.readLine();
				while (readLine != null) {
					content.append(readLine);
					readLine = reader.readLine();
				}
			} else {
				Log.w("DATA RETRIEVAL",
						"Unable to read data.HTTP response code = "
								+ responseCode);
				content = null;
			}
		} catch (ClientProtocolException e) {
			Log.e("readData", "ClientProtocolException:\n" + e.getMessage());
		} catch (IOException e) {
			Log.e("readData", "IOException:\n+e.getMessage()");
		}

		if (content == null) {
			return (null);
		} else {
			return (content.toString());
		}
	}

}