package com.groupF.androidminiprojectone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class parses the list of the valid countries and it orders the in
 * alphabetical order. It is used in the Graph.class, DataPage.class and
 * CompareDataPage.class.
 * 
 * @author Group F
 * 
 */
public class ParseCountry extends Thread {
	String value;
	String url;
	ArrayList<String> keys, countries, capital, invalidCountries;
	String data = new String();

	public ParseCountry(String url) {
		this.url = url;
		countries = new ArrayList<String>();
		keys = new ArrayList<String>();
		capital = new ArrayList<String>();
		invalidCountries = new ArrayList<String>();
		fillInvalidCountries();

	}

	public ArrayList<String> getCountries() {
		return countries;
	}

	public ArrayList<String> getCapitals() {
		return capital;
	}

	public ArrayList<String> getKeys() {
		return keys;
	}
	@Override
	public void run() {

		data = readData(url);
		try {
			JSONArray arrayOne = new JSONArray(data); //

			JSONArray array = arrayOne.getJSONArray(1);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i); // String name =
				object.getString("name");
				object.getString("id");
				object.getString("capitalCity");
				if (getInvalid(object.getString("name"), invalidCountries)) {
					countries.add(object.getString("name"));
					keys.add(object.getString("id"));
					capital.add(object.getString("capitalCity"));
				}

			}

		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		// orderList();
	}

	/**
	 * method given to us for reading the url
	 * @param url
	 * @return
	 */
	public static String readData(String url) {
		// Create download objects
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		StringBuilder content = new StringBuilder();
		try {
			// Execute response and create input stream
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
	/**
	 * This method gets all the invalid countries so we can take it out of the data structure afterwards
	 */
	public void fillInvalidCountries() {
		invalidCountries.add("Arab World");
		invalidCountries.add("Sub-Saharan Africa (IFC classification)");
		invalidCountries.add("East Asia and the Pacific (IFC classification)");
		invalidCountries.add("Europe and Central Asia (IFC classification)");
		invalidCountries
				.add("Latin America and the Caribbean (IFC classification)");
		invalidCountries
				.add("Middle East and North Africa (IFC classification)");
		invalidCountries.add("South Asia (IFC classification)");
		invalidCountries.add("East Asia & Pacific (developing only)");
		invalidCountries.add("East Asia & Pacific (all income levels)");
		invalidCountries.add("Europe & Central Asia (developing only)");
		invalidCountries.add("Europe & Central Asia (all income levels)");
		invalidCountries.add("Heavily indebted poor countries (HIPC)");
		invalidCountries.add("Not classified");
		invalidCountries.add("Latin America & Caribbean (developing only)");
		invalidCountries.add("Latin America & Caribbean (all income levels)");
		invalidCountries.add("Lower middle income");
		invalidCountries.add("Low & middle income");
		invalidCountries.add("Middle East & North Africa (all income levels)");
		invalidCountries.add("Middle East & North Africa (developing only)");
		invalidCountries.add("High income: nonOECD");
		invalidCountries.add("Other small states");
		invalidCountries.add("Sub-Saharan Africa (developing only)");
		invalidCountries.add("North America");
		invalidCountries.add("North Africa");
		invalidCountries.add("World");
		invalidCountries.add("Euro area");
		invalidCountries.add("High income");
		invalidCountries.add("Least developed countries: UN classification");
		invalidCountries.add("OECD members");
		invalidCountries.add("Low income");
		invalidCountries.add("High income: OECD");
		invalidCountries.add("Sub-Saharan Africa (all income levels)");
		invalidCountries.add("Sub-Saharan Africa excluding South Africa");
		invalidCountries.add("Upper middle income");
		invalidCountries
				.add("Sub-Saharan Africa excluding South Africa and Nigeria");
		invalidCountries.add("Middle income");
		invalidCountries.add("Least developed countries: UN classification");
		invalidCountries.add("Africa");
	}
	/**
	 * It filters through all countries, checking if the value which is the country, is in the invalidCountry
	 * data structure, if it is, it wont be added when the method returns, if the country isnt in invalidCountry
	 * then it will return true so it can be added in the returning method
	 * @param value
	 * @param invalidCountry
	 * @return
	 */
	public boolean getInvalid(String value, ArrayList<String> invalidCountry) {
		boolean a = true;
		for (int i = 0; i < invalidCountry.size(); i++) {
			if (value.equals(invalidCountry.get(i))) {
				a = false;
			}
		}
		return a;
	}
	/**
	 * We are putting it in a tree map so the map orders the list and so we clear both country and keys list,
	 * then we add the countries and keys back to the lists in alphabetical order
	 */
	public void orderList() {

		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		for (int i = 0; i < countries.size(); i++) {
			treeMap.put(countries.get(i), keys.get(i));
		}
		countries.clear();
		keys.clear();
		for (Entry<String, String> entry : treeMap.entrySet()) {

			countries.add(entry.getKey());
			keys.add(entry.getValue());

		}

		treeMap = null;

	}
}