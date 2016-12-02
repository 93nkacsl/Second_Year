package com.groupF.androidminiprojectone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class OptionsPage extends Activity {
	public SharedPreferences preferences;
	public ListView lvThemes;
	public ArrayList<String> alThemes = new ArrayList<String>();
	public ArrayList<String> alThemesKey = new ArrayList<String>();
	int themePosition = -1;
	Boolean bQ1Counter = false;
	Boolean bQ2Counter = false;
	Boolean bQ3Counter = false;
	
	//Declaring the GestureDetector
	private GestureDetectorCompat mDetector;
	/**
	 * This is the first method that runs for this class, it populates arrays referring to different themes,
	 * it sets the theme of this page and it creates the GUI
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		populateArrayList();
		populateArrayListKey();
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String themePreference = preferences.getString("ThemePreference","");

		//Choosing theme, based on what variable themePreference contains
		if (themePreference.contains((CharSequence)"Theme1")) {
			setTheme(R.style.Theme1);
		}
		else if (themePreference.contains((CharSequence) "Theme2")) {
			setTheme(R.style.Theme2);
		}
		else if (themePreference.contains((CharSequence) "Theme3")) {
			setTheme(R.style.Theme3);
		}
		
		//End of saved data stuff
		setContentView(R.layout.activity_options_page);
		
		//Instantiation of the GestureDetector with the application context
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		//populate ListView
		lvThemes = (ListView) findViewById(R.id.lvThemes);
		lvThemes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvThemes.setSelector(android.R.color.holo_blue_light);
		ArrayAdapter<String> aalvThemes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alThemes);
		lvThemes.setAdapter(aalvThemes);
		
		//onClickListener to remember position of selected item THEME LISTVIEW
		lvThemes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg2) {
				themePosition = position;
				Context context = getApplicationContext();
				CharSequence warning = "Please save theme when done";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, warning, duration);
				toast.show();
			}	
		});
	}
	/**
	 * Populates the ArrayList alThemes which fills the ListView
	 * the user uses on the GUI
	 */
	private void populateArrayList() {
		alThemes.add("Dark Theme");
		alThemes.add("Light Theme");
		alThemes.add("Colourful!");
	}
	/**
	 * Populates the ArrayList alThemesKey which holds the theme
	 * variable names (therefore, not what the user sees, but 
	 * what the code uses to identify each individual theme)
	 */
	private void populateArrayListKey() {
		alThemesKey.add("Theme1");
		alThemesKey.add("Theme2");
		alThemesKey.add("Theme3");
	}
	/**
	 * onClick method for when the save button is pressed.
	 * Returns a toast if no theme has been selected, and otherwise
	 * commits the change in theme (by chaging the SharedPreferences 
	 * theme value) and restarts the activity
	 * @param view
	 */
	public void saveMe(View view) {
		if(themePosition == -1)
		{
			Context context = getApplicationContext();
			CharSequence warning = "Please select a theme!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, warning, duration);
			toast.show();	
		}
		else 
		{
			String preferencesTheme = alThemesKey.get(themePosition);
			if (preferencesTheme != "") {
				//SharedPreferences
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("ThemePreference", preferencesTheme);
				editor.commit();
	
				//Restart activity
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		}
	}
	/**
	 * The help document for the country selector page
	 */
	public void onQ1ButtonClick(View view) {
		TextView tvQ1 = (TextView) findViewById(R.id.tvQ1);
		if (bQ1Counter == false) {
			tvQ1.setText("Once on the Country Selector page you will see a list of countries, you have two choices:" + "\n" + " - Select \"One Country\" " +
					 "\n" + " - Select \"Two Countries\"" + "\n" + "If you select One Country, click on the country and you will go to the information"
					+ "\n" + "\n" + "If you select Two Countries, click both countries, they will go at the bottom and press \"Compare\"" + "\n" + "\n" + "Click on the \"People\", " +
							"\"Finance\" and \"Energy & Infrastructure\" buttons to view more" + "\n" + "\n" + "If you selected One" + "Country, on the Data page, " +
									"you can choose to:" + "\n" + " - Add the country to favourites" + "\n" + " - Display information in graphs");
			bQ1Counter = true;
		}
		else {
			tvQ1.setText("");
			bQ1Counter = false;
		}	
	}
	/**
	 * The help document for the favourites page
	 */
	public void onQ2ButtonClick(View view) {
		TextView tvQ2 = (TextView) findViewById(R.id.tvQ2);
		if (bQ2Counter == false) {
			tvQ2.setText("Once on favourite's page, if you dont have any favourites, you will have to follow the information. If you do, click on the country you want" +
					", now you have two options:" + "\n" + " - Delete the country" + "\n" + " - View the country" + "\n" + "\n" + "If you choose to view it, it will take you to the page with the data"
					+ "\n" + "\n" + "Just remember, you have a maximum of 10 favourite slots ...So pick wizely!");
			bQ2Counter = true;
		}
		else {
			tvQ2.setText("");
			bQ2Counter = false;
		}	
	}
	/**
	 * The help document for the graphs page
	 */
	public void onQ3ButtonClick(View view) {
		TextView tvQ3 = (TextView) findViewById(R.id.tvQ3);
		if (bQ3Counter == false) {
			tvQ3.setText("Once on the Graph page, you will be asked to selected a country. After this is done, press the add button to add it to a list" + "\n" + "\n" + "You can do this process for up to 5 countries!" 
					+ "\n" + "\n" + "Go ahead and select the indicator and time frame, then pick the graph you want" + "\n" + "\n" + "Finally click the \"Generate Graph\" button and your graph will pop up!"
					+ "\n" + "\n" + "Another way to add a country is via the \"Get Graph\" button in the single country data page!" + "\n" + "\n" + "Please note, the graph won't show if:" + "\n" + "\n" + 
					" - You dont ADD the country" + "\n" +	" - There's no data for the country" + "\n" + " - Start year is higher than end" + "\n" + " - There is no network");
			bQ3Counter = true;
		}
		else {
			tvQ3.setText("");
			bQ3Counter = false;
		}	
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

				if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Intent intent = new Intent(OptionsPage.this, MenuPage.class);
					startActivity(intent);
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
	public boolean dispatchTouchEvent(MotionEvent event){
	    super.dispatchTouchEvent(event);    
	    return mDetector.onTouchEvent(event); 
	}
}
