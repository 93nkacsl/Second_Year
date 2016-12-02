package com.groupF.androidminiprojectone;

//package com.truiton.googlegraphs;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
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
public class BarChart extends Activity {
	WebView webview;

	public SharedPreferences preferences;

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

		setContentView(R.layout.activity_bar_chart);
		webview = (WebView) findViewById(R.id.webView1);

		String values = getIntent().getExtras().getString("value");
		System.out.println(values);

		String title = getIntent().getExtras().getString("title");
		String content = "<html>"
				+ "  <head>"
				+ "    <script type=\"text/javascript\" src=\"jsapi.js\"></script>"
				+ "    <script type=\"text/javascript\">"
				+ "      google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"
				+ "      google.setOnLoadCallback(drawChart);"
				+ "      function drawChart() {"
				+ "        var data = google.visualization.arrayToDataTable(["
				+ values
				+ "        ]);"
				+ "        var options = {"
				+ "          title: '"
				+ title
				+ "',"
				+ "          hAxis: {title: 'Year', titleTextStyle: {color: 'green'}}"
				+ "        };"
				+ "        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));"
				+ "        chart.draw(data, options);"
				+ "      }"
				+ "    </script>"
				+ "  </head>"
				+ "  <body>"
				+ "    <div id=\"chart_div\" style=\"width: 650px; height: 500px;\"></div>"

				+ "  </body>" + "</html>";

		WebSettings webSettings = webview.getSettings();

		webSettings.setJavaScriptEnabled(true);
		webview.requestFocusFromTouch();
		webview.loadDataWithBaseURL("file:///android_asset/", content,
				"text/html", "utf-8", null);
		webview.getSettings().setBuiltInZoomControls(true);
		// webview.loadUrl("file:///android_asset/Code.html"); // Can be used in
		// this way too.
	}

}