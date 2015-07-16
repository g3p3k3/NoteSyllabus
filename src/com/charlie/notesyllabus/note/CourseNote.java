package com.charlie.notesyllabus.note;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.NotesDB;
import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;

public class CourseNote extends Activity {
	private ListView lv;

	private MyAdapter adapter;
	private NotesDB notesDB;
	private SQLiteDatabase dbReader;
	private Cursor cursor;

	// 要显示笔记的课程名称
	private String courseName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_note_aty);
		
		// 获取要显示的课程名称
		courseName = getIntent().getStringExtra(Config.COURSE_NAME);
		initView();
	}

	public void initView() {
		lv = (ListView) findViewById(R.id.list);
		
		// 获取课表名称
		ArrayList<String> courseList = DataUtil.getCoursesName(this);
		
		if (courseList.size() > 0) {

			notesDB = new NotesDB(this, courseList);
			dbReader = notesDB.getReadableDatabase();
		}

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				cursor.moveToPosition(position);
				Intent i = new Intent(CourseNote.this, ShowNote.class);
				i.putExtra(Config.COURSE_NAME, courseName);
				i.putExtra(NotesDB.ID,
						cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
				i.putExtra(NotesDB.CONTENT, cursor.getString(cursor
						.getColumnIndex(NotesDB.CONTENT)));
				i.putExtra(NotesDB.TIME,
						cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
				i.putExtra(NotesDB.IMAGE,
						cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE)));
				i.putExtra(NotesDB.VIDEO,
						cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
				startActivity(i);
			}
		});
	}

	public void selectDB() {
		cursor = dbReader.query(courseName, null, null, null, null, null, null);
		adapter = new MyAdapter(this, cursor);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		selectDB();
	}
}
