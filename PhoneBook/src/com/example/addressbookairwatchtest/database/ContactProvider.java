package com.example.addressbookairwatchtest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactProvider extends SQLiteOpenHelper{
	
	public static final String TABLE_NAME = "tab_searchparam";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USER = "username";
	public static final String COLUMN_USER_NUMBER = "usernumber";
	public static final String COLUMN_USER_GROUP = "usergroup";

	private static final String DATABASE_NAME = "mycontacts.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME + "(" 
			+ COLUMN_ID	+ " integer primary key autoincrement, " 
			+ COLUMN_USER + " text not null, " 
			+ COLUMN_USER_NUMBER + " text not null, " 
			+ COLUMN_USER_GROUP	+ " text"
			+ ");";

	public ContactProvider(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ContactProvider.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}

}
