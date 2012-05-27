package com.foodsniff.shreyas.main;

/**
 * The base class from where the App loads.
 * It consists of the various values to be retrieved: Cuisine, Place and Mode of Travel.
 * It also consists of a numeric indicator for the user to type in the distance(in meters)
 * from his current location(Phone) for the radius determination.
 * */

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RestaurantMapSearchActivity extends Activity {
	String[] getInputList = new String[4];
	private static final String DEFAULT_DISTANCE = "50";

	HashMap<String, String> map = new HashMap<String, String>();

	/**
	 * Called when the activity is first created. Base Method called on Default.
	 * The main window appears in this screen
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window_layout);

		Spinner foodtypeSpinner = (Spinner) findViewById(R.id.FoodTypeSpinner);
		ArrayAdapter<CharSequence> foodAdapter = ArrayAdapter
				.createFromResource(this, R.array.foodType_array,
						android.R.layout.simple_spinner_item);
		foodAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		foodtypeSpinner.setAdapter(foodAdapter);

		Spinner resttypeSpinner = (Spinner) findViewById(R.id.RestTypeSpinner);
		ArrayAdapter<CharSequence> restAdapter = ArrayAdapter
				.createFromResource(this, R.array.restType_array,
						android.R.layout.simple_spinner_item);
		restAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		resttypeSpinner.setAdapter(restAdapter);

		Spinner transtypeSpinner = (Spinner) findViewById(R.id.TransTypeSpinner);
		ArrayAdapter<CharSequence> transAdapter = ArrayAdapter
				.createFromResource(this, R.array.transType_array,
						android.R.layout.simple_spinner_item);
		transAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		transtypeSpinner.setAdapter(transAdapter);

		foodtypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				getInputList[0] = parent.getItemAtPosition(pos).toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(parent.getContext(),
						"You have to select one type of food place!",
						Toast.LENGTH_LONG).show();
			}

		});

		// Add the required values to the restaurant type.
		map.put("Bakery", "bakery");
		map.put("Bar", "bar");
		map.put("Cafe", "cafe");
		map.put("Food", "food");
		map.put("Meal Delivery", "meal_delivery");
		map.put("Meal Takeaway", "meal_takeaway");
		map.put("Restaurant", "restaurant");

		resttypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				getInputList[1] = map.get(parent.getItemAtPosition(pos)
						.toString()); // get the type for the correct reference
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(parent.getContext(),
						"You have to select atleast one Restaurant style!",
						Toast.LENGTH_LONG).show();
			}

		});

		map.put("By Walk", "&mode=walking");
		map.put("By Biking/Cycling", "&mode=biking");
		map.put("Via Drive/Car", "&mode=driving");

		transtypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						getInputList[2] = map.get(parent.getItemAtPosition(pos)
								.toString());
					}

					public void onNothingSelected(AdapterView<?> parent) {
						Toast.makeText(
								parent.getContext(),
								"You have to select atleast one Transport type!",
								Toast.LENGTH_LONG).show();
					}
				});

		final EditText editDistance = (EditText) findViewById(R.id.DistanceSelect);

		Button searchButton = (Button) findViewById(R.id.SearchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Convert the Distance into meters for Google API criteria
				int getActualDistance = (int) (Double.parseDouble(editDistance
						.getText().toString()) * 1000);
				String getDistance = String.valueOf(getActualDistance);
				if (getDistance.equals("")) {
					getDistance = DEFAULT_DISTANCE;
				}
				getInputList[3] = getDistance;

				// Call the activity after collecting the list of inputs.
				Intent intMapCall = new Intent(
						RestaurantMapSearchActivity.this, MapCallActivity.class);
				intMapCall.putExtra("MapSearchListIntentKey", getInputList);

				RestaurantMapSearchActivity.this.startActivity(intMapCall);

			}
		});

		Button clearButton = (Button) findViewById(R.id.ClearButton);
		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Clear the contents of the String Array and set the value of
				// the distance as empty
				editDistance.setText("");
			}
		});

	}

	// Calling the options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	// On calling the menu, relevant task is performed.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.credits:
			Toast.makeText(
					this,
					"Developer: Shreyas Kulkarni\nEmail:shrykulk.rk85@gmail.com\nMain Credits:Google Maps, Google APIs",
					Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}

	/*
	 * void gotoSettings() { // App Settings Code goes here.
	 * Toast.makeText(this, "You pressed the Settings button. Design pending.",
	 * Toast.LENGTH_LONG).show();
	 * 
	 * }
	 * 
	 * void callMainScreen() { // Go back to main screen and clear the contents
	 * Toast.makeText(this,
	 * "You pressed the Button to call the main screen! Design pending.",
	 * Toast.LENGTH_LONG).show(); }
	 */

}