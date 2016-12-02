package com.groupF.androidminiprojectone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
 * This is a class that parses an indicator and inserts them into an arraylist
 * you can access the array using the getList {@link Method} :)
 * 
 * @author Group F
 * 
 */
public class ParseArray extends Thread {
	ArrayList<String> list = new ArrayList<String>();
	String url;
	String countryName = new String();
	String data = new String();

	public ParseArray(String url) {
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
			for (int i = 0; i < MainArray.length(); i++) {
				JSONObject object = MainArray.getJSONObject(i);
				JSONObject country = object.getJSONObject("country");
				countryName = (String) country.get("value");

				// String date = object.get("date").toString();
				String value = object.get("value").toString();
				list.add(value);

			}

		} catch (JSONException e) {
//			CountrySelector tmp= new CountrySelector();
//			Toast.makeText(tmp.getApplicationContext(), "No Information for this country.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	public ArrayList<String> getList() {
		return list;
	}

	public String getCountryName() {
		return countryName;
	}

	/**
	 * 
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
		// return data
		if (content == null) {
			return (null);
		} else {
			return (content.toString());
		}
	}
}
