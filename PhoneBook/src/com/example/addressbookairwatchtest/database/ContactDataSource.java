package com.example.addressbookairwatchtest.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactDataSource {
	private SQLiteDatabase database;
	private ContactProvider dbHelper;
	private String[] allColumns = { ContactProvider.COLUMN_ID,
			ContactProvider.COLUMN_USER,
			ContactProvider.COLUMN_USER_NUMBER,
			ContactProvider.COLUMN_USER_GROUP};
	
	public ContactDataSource(Context context) {
		dbHelper = new ContactProvider(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	// Creates the User in the DB.
	public void createUser(ContactsTable contact) {
		ContentValues values = new ContentValues();
		values.put(ContactProvider.COLUMN_USER, contact.getUser());
		values.put(ContactProvider.COLUMN_USER_NUMBER, contact.getNumber());
		values.put(ContactProvider.COLUMN_USER_GROUP, contact.getGroup());
		
		long insertId = database.insert(ContactProvider.TABLE_NAME, null, values);
		
		Cursor cursor = database.query(ContactProvider.TABLE_NAME,
				allColumns, ContactProvider.COLUMN_ID + " = " + insertId, null,
				null, null, ContactProvider.COLUMN_USER);
		cursor.moveToFirst();
		// ContactsTable newContactsTable = cursorToContacts(cursor);
		cursor.close();
		
	}
	
	// Updates the User contents in the DB.
	public void updateUser(ContactsTable newContact, String oldName) {
		ContentValues values = new ContentValues();
		values.put(ContactProvider.COLUMN_USER, newContact.getUser());
		values.put(ContactProvider.COLUMN_USER_NUMBER, newContact.getNumber());
		values.put(ContactProvider.COLUMN_USER_GROUP, newContact.getGroup());
		
		long updateId = database.update(ContactProvider.TABLE_NAME, values,
				ContactProvider.COLUMN_USER + " = '" + oldName + "'", null);
		
		Cursor cursor = database.query(ContactProvider.TABLE_NAME, allColumns,
				ContactProvider.COLUMN_ID + " = " + updateId, null, null, null,
				ContactProvider.COLUMN_USER);
		cursor.moveToFirst();
		// ContactsTable updateUser = cursorToContacts(cursor);
		cursor.close();
		
	}
	
	// Removes the User in the DB.
	public void deleteUser(ContactsTable contact) {
		long id = contact.getId();
		database.delete(ContactProvider.TABLE_NAME, ContactProvider.COLUMN_ID
				+ " = " + id, null);
	}
	
	public List<ContactsTable> getAllValues() {
		List<ContactsTable> ContactsTableList = new ArrayList<ContactsTable>();

		Cursor cursor = database.query(ContactProvider.TABLE_NAME,
				allColumns, null, null, null, null, ContactProvider.COLUMN_USER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContactsTable ContactsTable = cursorToContacts(cursor);
			ContactsTableList.add(ContactsTable);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return ContactsTableList;
	}

	// Fetches User information.
	private ContactsTable cursorToContacts(Cursor cursor) {
		ContactsTable contactsTable = new ContactsTable();
		
		contactsTable.setId(cursor.getLong(0));
		contactsTable.setUser(cursor.getString(1));
		contactsTable.setNumber(cursor.getString(2));
		contactsTable.setGroup(cursor.getString(3));
		
		return contactsTable;
	}

}
