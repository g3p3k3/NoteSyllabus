package com.charlie.notesyllabus.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.charlie.notesyllabus.entity.Course;

public class CourseDBOp {
	
	/**
	 * 存储课程表信息
	 * @param context
	 * @param courses
	 */
	public static void storeCourses(Context context, List<Course> courses) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		for (int i = 0; i < courses.size(); i++) {
			ContentValues values = new ContentValues();
			values.put(CourseDB.COURSE_NAME, courses.get(i).getName());
			values.put(CourseDB.DAY, courses.get(i).getDay());
			values.put(CourseDB.CLASSROOM, courses.get(i).getClassroom());
			values.put(CourseDB.START_LESSON, courses.get(i).getStartLesson());
			values.put(CourseDB.TOTAL_LESSON, courses.get(i).getTotalLesson());
			values.put(CourseDB.WEEK, courses.get(i).getWeek());
			values.put(CourseDB.TEACHER, courses.get(i).getTeacher());
			
			dbWriter.insert(CourseDB.TABLE_NAME, null, values);
			values.clear();
		}
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 载入课程表信息
	 * @param context
	 * @return
	 */
	public static List<Course> loadCourses(Context context) {
		List<Course> courses = new ArrayList<Course>();
		
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbReader = courseDB.getReadableDatabase();
		
		Cursor cursor = dbReader.query(CourseDB.TABLE_NAME, null, null, null, null, null, null);

		// 没有课程信息
		if (cursor.getCount() == 0) {
			cursor.close();
			dbReader.close();
			courseDB.close();
			return courses;
		}
		
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		String day = "";
		String week = "";
		String teacher = "";
		
		cursor.moveToFirst();  
		while (!cursor.isAfterLast()) {
			// 获取各字段名称
		    courseName = cursor.getString(cursor.getColumnIndex(CourseDB.COURSE_NAME));
		    startLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.START_LESSON));
		    totalLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.TOTAL_LESSON));
		    classroom = cursor.getString(cursor.getColumnIndex(CourseDB.CLASSROOM));
		    day = cursor.getString(cursor.getColumnIndex(CourseDB.DAY));
		    week = cursor.getString(cursor.getColumnIndex(CourseDB.WEEK));
		    teacher = cursor.getString(cursor.getColumnIndex(CourseDB.TEACHER));
		    
		    Course tmp = new Course(courseName, day, classroom, startLesson, totalLesson, week, teacher);
		    courses.add(tmp);
		    
		    cursor.moveToNext();  
		}
		// 关闭
		cursor.close();
		dbReader.close();
		courseDB.close();
		
		return courses;
	}
	
	/**
	 * 按照指定条件查询课表数据
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static ArrayList<Course> queryCourses(Context context,
			String selection, String[] selectionArgs) {
		
		ArrayList<Course> courses = new ArrayList<Course>();
		
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbReader = courseDB.getReadableDatabase();
		
		Cursor cursor = dbReader.query(CourseDB.TABLE_NAME, null, selection,
				selectionArgs, null, null, null);

		// 没有课程信息
		if (cursor.getCount() == 0) {
			cursor.close();
			dbReader.close();
			courseDB.close();
			return courses;
		}
		
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		String day = "";
		String week = "";
		String teacher = "";
		
		cursor.moveToFirst();  
		while (!cursor.isAfterLast()) {
			// 获取各字段名称
		    courseName = cursor.getString(cursor.getColumnIndex(CourseDB.COURSE_NAME));
		    startLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.START_LESSON));
		    totalLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.TOTAL_LESSON));
		    classroom = cursor.getString(cursor.getColumnIndex(CourseDB.CLASSROOM));
		    day = cursor.getString(cursor.getColumnIndex(CourseDB.DAY));
		    week = cursor.getString(cursor.getColumnIndex(CourseDB.WEEK));
		    teacher = cursor.getString(cursor.getColumnIndex(CourseDB.TEACHER));
		    
		    Course tmp = new Course(courseName, day, classroom, startLesson, totalLesson, week, teacher);
		    courses.add(tmp);
		    
		    cursor.moveToNext();  
		}
		// 关闭
		cursor.close();
		dbReader.close();
		courseDB.close();
		
		return courses;
	}
	
	/**
	 * 删除所有课程
	 * @param context
	 */
	public static void deleteAllCourses(Context context) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		String sql_delTable = "DELETE FROM "+ CourseDB.TABLE_NAME;
		dbWriter.execSQL(sql_delTable);
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 存储单个课程
	 * @param context
	 * @param course
	 */
	public static void storeSingleCourse(Context context, Course course) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CourseDB.COURSE_NAME, course.getName());
		values.put(CourseDB.DAY, course.getDay());
		values.put(CourseDB.CLASSROOM, course.getClassroom());
		values.put(CourseDB.START_LESSON, course.getStartLesson());
		values.put(CourseDB.TOTAL_LESSON, course.getTotalLesson());
		values.put(CourseDB.WEEK, course.getWeek());
		values.put(CourseDB.TEACHER, course.getTeacher());
		
		dbWriter.insert(CourseDB.TABLE_NAME, null, values);
		values.clear();
			
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 删除单个课程
	 * @param context
	 * @param name
	 */
	public static void deleteSingleCourse(Context context, String name) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();

		dbWriter.delete(CourseDB.TABLE_NAME, CourseDB.COURSE_NAME+"=?", new String[]{ name });
		
		dbWriter.close();
		courseDB.close();
	}
}
