package com.example.addressbookairwatchtest.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.addressbookairwatchtest.R;
import com.example.addressbookairwatchtest.database.ContactDataSource;
import com.example.addressbookairwatchtest.database.ContactsTable;

public class MainActivity extends ListActivity {
	private ContactDataSource contactList;
	ContactsTable userDetails = new ContactsTable();
	int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Open the Database.
		contactList = new ContactDataSource(this);
		contactList.open();
		
		// Fetch all available values from DB.
		final List<ContactsTable> getContacts = contactList.getAllValues();
		setListAdapter(new ArrayAdapter<ContactsTable>(
				this, android.R.layout.simple_list_item_1, getContacts));
		
		// Auto-search names based on input parameters.
		final EditText textSearch = (EditText) findViewById(R.id.autosearch);
		textSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int textlength = textSearch.getText().length();
				List<ContactsTable> searchList = new ArrayList<ContactsTable>();
				searchList.clear();
				for (int i = 0; i < getContacts.size(); i++) {
					if (textlength <= getContacts.get(i).getUser().length()) {
						if (textSearch.getText().toString()
								.equalsIgnoreCase(
										(String) getContacts.get(i).getUser().subSequence(0, textlength))) {
							searchList.add(getContacts.get(i));
						}
					}
				}
				setListAdapter(new ArrayAdapter<ContactsTable>(
						MainActivity.this, android.R.layout.simple_list_item_1, searchList));
			}

		});
		
		// Add User
		Button addButton = (Button) findViewById(R.id.addbutton);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent newUserIntent = new Intent(MainActivity.this,
						ContactsDetailsActivity.class);
				startActivityForResult(newUserIntent, 1);
			}

		});
		
		// Edit the User contents
		Button editButton = (Button) findViewById(R.id.editbutton);
		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getListAdapter().getCount() > 0) {
					Intent newUserIntent = new Intent(MainActivity.this, ContactsDetailsActivity.class);
					String[] passParam = new String[3];
					ContactsTable user = (ContactsTable) getListAdapter().getItem(position);
					
					passParam[0] = user.getUser();
					passParam[1] = user.getNumber();
					passParam[2] = user.getGroup();
					newUserIntent.putExtra("UserDetails", passParam);
					
					startActivityForResult(newUserIntent, 2);
					
				}
				
			}
			
		});
		
		// Delete User from List.
		Button deleteButton = (Button) findViewById(R.id.deletebutton);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getListAdapter().getCount() > 0) {
					ContactsTable user = (ContactsTable) getListAdapter().getItem(position);
					contactList.deleteUser(user);
					setListAdapter(new ArrayAdapter<ContactsTable>(
							MainActivity.this, android.R.layout.simple_list_item_1, contactList.getAllValues()));
				}
			}

		});
	}

	// Fetch result from the 2nd Activity to get information and then call database methods for the same.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String[] result = data.getExtras().getStringArray("UserDetailsResult");
			userDetails.setUser(result[0]);
			userDetails.setNumber(result[1]);
			userDetails.setGroup(result[2]);
			contactList.open();

			// @SuppressWarnings("unchecked")
			// ArrayAdapter<ContactsTable> adapter = (ArrayAdapter<ContactsTable>) getListAdapter();

			switch (requestCode) {
			case (1): {
				contactList.createUser(userDetails); // Insert
			}
				break;
			case (2): {
				contactList.updateUser(userDetails, result[3]); // Update
			}
				break;
			}
			// Refresh the list.
			setListAdapter(new ArrayAdapter<ContactsTable>(
					MainActivity.this, android.R.layout.simple_list_item_1, contactList.getAllValues()));
		}
	}
	
	@Override
	protected void onResume() {
		contactList.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		contactList.close();
		super.onPause();
	}


}
