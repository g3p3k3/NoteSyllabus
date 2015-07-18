package com.charlie.notesyllabus.syllabus;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.CourseDBOp;
import com.charlie.notesyllabus.data.NotesDBOp;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.entity.Course;
import com.charlie.notesyllabus.util.DataUtil;
import com.charlie.notesyllabus.util.HttpUtil;

public class ImportCourses extends Activity implements OnValueChangeListener,
		OnClickListener {

	private NumberPicker np_year1, np_year2;
	private NumberPicker np_term;
	private EditText etCode;
	private ImageView ivCode;

	private Button btn_confirm;

	private int year1 = 2010, year2 = 2010;
	private int term = 1;
	
	private String code = "";

	// 联网读取课程表进度对话框
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_courses_aty);
		
		init();
		
		// 获取验证码图片
		refreshCheckCode();
	}

	public void init() {
		np_year1 = (NumberPicker) findViewById(R.id.numberPicker1);
		np_year2 = (NumberPicker) findViewById(R.id.numberPicker2);
		np_term = (NumberPicker) findViewById(R.id.numberPicker3);

		np_year1.setOnValueChangedListener(this);
		np_year2.setOnValueChangedListener(this);
		np_term.setOnValueChangedListener(this);

		np_year1.setMinValue(2010);
		np_year1.setMaxValue(2050);
		np_year2.setMinValue(2010);
		np_year2.setMaxValue(2050);
		np_term.setMinValue(1);
		np_term.setMaxValue(3);

		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		etCode = (EditText) findViewById(R.id.checkcode2);
		ivCode = (ImageView) findViewById(R.id.iv_code2);
	}

	@Override
	public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
		int id = arg0.getId();
		switch (id) {
		case R.id.numberPicker1:
			year1 = newVal;
			break;
		case R.id.numberPicker2:
			year2 = newVal;
			break;
		case R.id.numberPicker3:
			term = newVal;
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		// 检查学年度是否合法
		if (year2 - year1 != 1) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("学年度选择不规范")
					.setPositiveButton("确认", null).show();
		}
		else {
			code = etCode.getText().toString();
			// 检查验证码是否为空
			if (code.length() == 0) {
				// 弹出提示框
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("验证码不能为空！").setPositiveButton("确定", null)
						.show();
				
				return ;
			}
			
			importAndStore(year1, year2, term, code);
		}
	}

	/**
	 * 导入课程表，储存在数据库
	 * @param year1
	 * @param year2
	 * @param term
	 * @param code
	 */
	public void importAndStore(final int year1, final int year2, final int term, final String code) {

		// 进行网络请求
		new AsyncTask<Void, Void, List<Course>>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialog.show(ImportCourses.this,
						"正在读取课表信息", "请稍后...", true, false);
			}

			@Override
			protected List<Course> doInBackground(Void... a) {
				// 获取储存的账号密码
				String[] msg = Preferences.getUserMsg(ImportCourses.this);

				HttpUtil.getCookies(msg[0], msg[1], code);
				List<StringBuffer> list = HttpUtil.getTable(year1, year2, term);
				List<Course> courses = DataUtil.get(list);

				return courses;
			}

			protected void onPostExecute(List<Course> courses) {
				progressDialog.dismiss();
				
				// 导入成功
				if (courses.size() != 0) {
					// 存储在数据库
					CourseDBOp.storeCourses(ImportCourses.this, courses);
					// 为各个课程创建笔记Table
					NotesDBOp.createNoteTable(ImportCourses.this);
					
					// 结束当前活动，返回主界面
					finish();
				}
				// 导入失败
				else {
					new AlertDialog.Builder(ImportCourses.this)
							.setTitle("错误")
							.setMessage("导入失败")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									refreshCheckCode();
								}
							})
							.show();
				}

			};

		}.execute();
	}
	
	/**
	 * 重新加载验证码
	 */
	private void refreshCheckCode() {
		new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... arg0) {
				HttpUtil.getFirst();
				return HttpUtil.getBitmapFromServer();
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				ivCode.setImageBitmap(result);
			}

		}.execute();
	}
}
