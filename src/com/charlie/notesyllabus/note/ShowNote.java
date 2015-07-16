package com.charlie.notesyllabus.note;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.NotesDB;

public class ShowNote extends Activity implements OnClickListener {
	private Button s_delete, s_detail;
	private ImageView s_img;
	private VideoView s_video;
	private TextView s_tv;
	
	private NotesDB notesDB;
	private SQLiteDatabase dbWriter;
	
	String path_img = "";
	String path_video = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_note_aty);
		
		s_delete = (Button) findViewById(R.id.s_delete);
		s_detail = (Button) findViewById(R.id.s_detail);
		s_img = (ImageView) findViewById(R.id.s_img);
		s_video = (VideoView) findViewById(R.id.s_video);
		s_tv = (TextView) findViewById(R.id.s_tv);
		
		s_delete.setOnClickListener(this);
		s_detail.setOnClickListener(this);
		
		notesDB = new NotesDB(this, null);
		dbWriter = notesDB.getWritableDatabase();
		
		
		if (getIntent().getStringExtra(NotesDB.IMAGE).equals("null")) {
			s_img.setVisibility(View.GONE);
		} else {
			s_img.setVisibility(View.VISIBLE);
			// show image
			path_img = getIntent().getStringExtra(NotesDB.IMAGE);
			Bitmap bitmap = BitmapFactory.decodeFile(path_img);
			s_img.setImageBitmap(bitmap);
		}
		
		if (getIntent().getStringExtra(NotesDB.VIDEO).equals("null")) {
			s_video.setVisibility(View.GONE);
		} else {
			s_video.setVisibility(View.VISIBLE);
			// show video
			path_video = getIntent().getStringExtra(NotesDB.VIDEO);
			s_video.setVideoURI(Uri.parse(path_video));
			s_video.start();
		}
		
		s_tv.setText(getIntent().getStringExtra(NotesDB.CONTENT));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.s_delete:
			deleteData();
			finish();
			break;
		case R.id.s_detail:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if (s_video.getVisibility() == View.VISIBLE) {
				Uri uri = Uri.fromFile(new File(path_video));
				intent.setDataAndType(uri, "video/*");
			}
			else if (s_img.getVisibility() == View.VISIBLE) {
				Uri uri = Uri.fromFile(new File(path_img));
				intent.setDataAndType(uri, "image/*");
			}
			startActivity(intent);

			break;
		}
	}
	
	public void deleteData() {
		String table_name = getIntent().getStringExtra("COURSE_NAME");
		dbWriter.delete(table_name, "_id="+getIntent().getIntExtra(NotesDB.ID, 0), null);
	}
}
