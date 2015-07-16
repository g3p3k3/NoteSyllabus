package com.charlie.notesyllabus.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;

public class NotesDBOp {
	
	/**
	 * 创建笔记Table
	 * @param context
	 */
	public static void createNoteTable(Context context) {
		
		ArrayList<String> list = DataUtil.getCoursesName(context);
		
		if (list == null) {
			return ;
		}
		
		NotesDB notesDB = new NotesDB(context, list);
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();
		
		for (int i = 0; i < list.size(); i++) {
			
			dbWriter.execSQL("CREATE TABLE IF NOT EXISTS " + list.get(i) + " (" 
					+ Config.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Config.CONTENT + " TEXT NOT NULL," 
					+ Config.IMAGE + " TEXT NOT NULL,"
					+ Config.VIDEO + " TEXT NOT NULL,"
					+ Config.TIME + " TEXT NOT NULL)");
		}
		
		dbWriter.close();
		notesDB.close();
	}

	/**
	 * 删除所有笔记
	 * @param context
	 */
	public static void deleteAllNotes(Context context) {
		ArrayList<String> list = DataUtil.getCoursesName(context);
		NotesDB notesDB = new NotesDB(context, list);
		
		// 当前不存在课程，直接退出
		if (list == null) {
			notesDB.close();
			return ;
		}

		SQLiteDatabase dbReader = notesDB.getReadableDatabase();
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();

		// 每一个课程是一个表
		// 先将表中的图片与录像删除，再删除该表
		for (int i = 0; i < list.size(); i++) {
			String courseName = list.get(i);
			Cursor cursor = dbReader.query(courseName, new String[] {
					NotesDB.IMAGE, NotesDB.VIDEO }, null, null, null, null,
					null);
			
			if (cursor.getCount() == 0) {
				continue;
			}
			
			cursor.moveToFirst();  
			while (!cursor.isAfterLast()) {
				
				// 获取图片与录像路径
				String imageUri = cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE));
				String videoUri = cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO));

				// 删除文件
				DataUtil.deleteFile(imageUri);
				DataUtil.deleteFile(videoUri);
			    
			    cursor.moveToNext();  
			}
			
			cursor.close();
			String sql_delTable = "DROP TABLE "+courseName;
			dbWriter.execSQL(sql_delTable);
		}
		
		// 关闭数据库
		dbReader.close();
		dbWriter.close();
		notesDB.close();
	}
	
	public static void delSingleCourseNote(Context context, String courseName) {
		ArrayList<String> list = DataUtil.getCoursesName(context);
		NotesDB notesDB = new NotesDB(context, list);

		SQLiteDatabase dbReader = notesDB.getReadableDatabase();
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();

		// 每一个课程是一个表
		// 先将表中的图片与录像删除，再删除该表
		Cursor cursor = dbReader.query(courseName, new String[] {
				NotesDB.IMAGE, NotesDB.VIDEO }, null, null, null, null,
				null);
	
		if (cursor.getCount() != 0) {

			cursor.moveToFirst();  
			while (!cursor.isAfterLast()) {
				
				// 获取图片与录像路径
				String imageUri = cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE));
				String videoUri = cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO));

				// 删除文件
				DataUtil.deleteFile(imageUri);
				DataUtil.deleteFile(videoUri);
			    
			    cursor.moveToNext();  
			}
		}

		cursor.close();
		// 删除表
		String sql_delTable = "DROP TABLE IF EXISTS "+courseName;
		dbWriter.execSQL(sql_delTable);
		
		// 关闭数据库
		dbReader.close();
		dbWriter.close();
		notesDB.close();
	}
}
