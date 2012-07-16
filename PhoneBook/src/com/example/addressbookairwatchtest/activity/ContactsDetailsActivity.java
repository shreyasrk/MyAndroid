package com.example.addressbookairwatchtest.activity;
/**
 * 2nd Activity: Transferred to gather information in one single activity window.
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.addressbookairwatchtest.R;

public class ContactsDetailsActivity extends Activity {
	String groupItem;
	String oldName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdetails);
		
		final EditText username = (EditText) findViewById(R.id.nametext);
		final EditText usernumber = (EditText) findViewById(R.id.numbertext);

		Bundle valuesPassed = getIntent().getExtras();
		if (valuesPassed != null) {
			String[] userdetails = valuesPassed.getStringArray("UserDetails");
			username.setText(userdetails[0]);
			usernumber.setText(userdetails[1]);
			
			oldName = userdetails[0];
		}
		
		// Grouping based on Types provided.
		Spinner spinner = (Spinner) findViewById(R.id.group);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.group_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				groupItem = (String) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});

		Button OKButton = (Button) findViewById(R.id.buttonOK);
		OKButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String[] userArray = new String[4];
				if (!username.equals("")) {
					userArray[0] = username.getText().toString();
					if (!usernumber.equals("")) {
						userArray[1] = usernumber.getText().toString();
						
						userArray[2] = groupItem;
						userArray[3] = oldName;
						
						// Pass the result array back to the calling activity.
						Intent resultIntent = new Intent();
						resultIntent.putExtra("UserDetailsResult", userArray);
						setResult(Activity.RESULT_OK, resultIntent);

					} else {
						Toast.makeText(ContactsDetailsActivity.this,
								"Enter a Phone Number!", Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(ContactsDetailsActivity.this,
							"Enter a Username!", Toast.LENGTH_LONG).show();
				}
				finish();
			}

		});
	}
}
