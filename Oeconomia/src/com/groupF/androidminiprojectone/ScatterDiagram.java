package com.groupF.androidminiprojectone;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * This Class is one of the three type of graphs that the application contains.
 * Reference:
 * http://www.truiton.com/2013/04/android-tutorial-using-google-chart-tools
 * -with-svg-and-image-api/
 * 
 * @author Group F
 */
public class ScatterDiagram extends Activity {
	WebView wv_Scatter;

	public SharedPreferences preferences;

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

		setContentView(R.layout.activity_scatter_diagram);
		String values = getIntent().getExtras().getString("value");
		wv_Scatter = (WebView) findViewById(R.id.WB_Scatter);
		String title = getIntent().getExtras().getString("title");
		String html = "<html>"
				+ "<head>"
				+ "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>"
				+ "<script type=\"text/javascript\">"
				+ " google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"
				+ "google.setOnLoadCallback(drawChart);"
				+ "function drawChart() {"
				+ "var data = google.visualization.arrayToDataTable(["
				+ values
				+ "]);"
				+

				"var options = {"
				+ "title: '"
				+ title
				+ "',"
				+ "hAxis: {title: 'Year',  titleTextStyle: {color: '#333'}},"
				+ "vAxis: {minValue: 0}"
				+ "};"
				+ "var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));"
				+ "chart.draw(data, options);"
				+ "}"
				+ "</script>"
				+ "</head>"
				+ "<body>"
				+ "<div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div>"
				+ "</body>" + "</html>";

		WebSettings webSettings = wv_Scatter.getSettings();

		webSettings.setJavaScriptEnabled(true);
		wv_Scatter.requestFocusFromTouch();
		wv_Scatter.loadDataWithBaseURL("file:///android_asset/", html,
				"text/html", "utf-8", null);
		wv_Scatter.getSettings().setBuiltInZoomControls(true);
	}

}