package com.charlie.notesyllabus.note;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.NotesDB;
import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;


public class AddNote extends Activity implements OnClickListener {
	private int note_type;
	private Button saveBtn, deleteBtn;
	private EditText ettext;
	private ImageView c_img;
	private VideoView v_video;

	private NotesDB notesDB;
	private SQLiteDatabase dbWriter;

	private File photoFile;
	private File videoFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note_aty);
		note_type = getIntent().getIntExtra(Config.NOTE_TYPE, 0);

		saveBtn = (Button) findViewById(R.id.save);
		deleteBtn = (Button) findViewById(R.id.delete);
		ettext = (EditText) findViewById(R.id.ettext);
		c_img = (ImageView) findViewById(R.id.c_img);
		v_video = (VideoView) findViewById(R.id.c_video);

		saveBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);

		notesDB = new NotesDB(this, DataUtil.getCoursesName(this));
		dbWriter = notesDB.getWritableDatabase();

		initView();
	}

	public void initView() {
		if (note_type == 1) {
			c_img.setVisibility(View.GONE);
			v_video.setVisibility(View.GONE);
		} 
		else if (note_type == 2) {
			c_img.setVisibility(View.VISIBLE);
			v_video.setVisibility(View.GONE);
			// take a photo
			Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/" + getTime() + ".jpg");
			
			System.out.println("图片地址："+photoFile.toString());
			
			iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(iimg, 1);
		} 
		else if (note_type == 3) {
			c_img.setVisibility(View.GONE);
			v_video.setVisibility(View.VISIBLE);
			// take a video
			Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/" + getTime() + ".mp4");
			video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
			startActivityForResult(video, 2);
		}
		else
			return ;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			addDB();
			finish();
			break;
		case R.id.delete:
			finish();
			break;
		}
	}

	public void addDB() {
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.CONTENT, ettext.getText().toString());
		cv.put(NotesDB.TIME, getTime());
		cv.put(NotesDB.IMAGE, photoFile + "");
		cv.put(NotesDB.VIDEO, videoFile + "");
		
		// 计算出当前时间所属的课程，若不属于所有课程则属于"默认"
		String tableName = DataUtil.getCurrentCourse(this);
		dbWriter.insert(tableName, null, cv);
	}

	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date d = new Date();
		String str = format.format(d);
		return str;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			Bitmap bitmap = BitmapFactory.decodeFile(photoFile
					.getAbsolutePath());
			c_img.setImageBitmap(bitmap);
		}
		if (requestCode == 2) {
			v_video.setVideoURI(Uri.fromFile(videoFile));
			v_video.start();
		}
	}
}
