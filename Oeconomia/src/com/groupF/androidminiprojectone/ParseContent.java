package com.groupF.androidminiprojectone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * This class parses the last value in a indicator, it is used in the
 * DataPage.class and CompareDataPage.class
 * 
 * 
 * @author Group F
 * 
 */
public class ParseContent extends Thread {
	String value;
	String url;
	String data = new String();

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
		// return data
		if (content == null) {
			return (null);
		} else {
			return (content.toString());
		}
	}

	public ParseContent(String url) {
		this.url = url;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void run() {

		data = readData(url);

		try {

			JSONArray arrayOne = new JSONArray(data);

			JSONArray MainArray = arrayOne.getJSONArray(1);

			JSONObject object = MainArray.getJSONObject(0);

			value = object.get("value").toString();

		} catch (JSONException e) {

			e.printStackTrace();
		}

	}
}
