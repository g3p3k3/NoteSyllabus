package com.charlie.notesyllabus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CourseDB extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "courses";
	public static final String ID = "_id";
	public static final String COURSE_NAME = "name";
	public static final String DAY = "day";
	public static final String CLASSROOM = "classroom";
	public static final String START_LESSON = "start_lesson";
	public static final String TOTAL_LESSON = "total_lesson";
	public static final String WEEK = "week";
	public static final String TEACHER = "teacher";
	
	public CourseDB(Context context) {
		// name: courses; version: 1;
		super(context, "courses", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COURSE_NAME + " TEXT NOT NULL," 
				+ DAY + " TEXT NOT NULL,"
				+ CLASSROOM + " TEXT NOT NULL,"
				+ START_LESSON + " INTEGER NOT NULL,"
				+ TOTAL_LESSON + " INTEGER NOT NULL,"
				+ WEEK + " TEXT NOT NULL,"
				+ TEACHER + " TEXT NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
