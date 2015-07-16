package com.charlie.notesyllabus.note;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;

public class Note extends Activity implements OnItemClickListener {
	
	private ListView lv_courses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.note_aty);
		lv_courses = (ListView) findViewById(R.id.lv_courses);
		
		initListView();
	}
	
	public void initListView() {
		// 获取课表名称
		ArrayList<String> courseList = DataUtil.getCoursesName(this);
		
		if (courseList.size() > 0) {
			
			lv_courses.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, courseList));
			lv_courses.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		String courseName = lv_courses.getItemAtPosition(position).toString();
		Toast.makeText(this, courseName, Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(this, CourseNote.class);
		i.putExtra(Config.COURSE_NAME, courseName);
		startActivity(i);

	}
}
