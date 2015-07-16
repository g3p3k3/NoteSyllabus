package com.charlie.notesyllabus.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDB extends SQLiteOpenHelper {

	public static final String CONTENT = "content";
	public static final String IMAGE = "image";
	public static final String VIDEO = "video";
	public static final String ID = "_id";
	public static final String TIME = "time";
	
	// 课程名数组
	public ArrayList<String> list;
	
	public NotesDB(Context context, ArrayList<String> list) {
		// name: notes; version: 1;
		super(context, "notes", null, 1);
		this.list = list;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (int i = 0; i < list.size(); i++) {
			db.execSQL("CREATE TABLE " + list.get(i) + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CONTENT + " TEXT NOT NULL," 
					+ IMAGE + " TEXT NOT NULL,"
					+ VIDEO + " TEXT NOT NULL,"
					+ TIME + " TEXT NOT NULL)");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
