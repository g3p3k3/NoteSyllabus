package com.charlie.notesyllabus.affair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.CourseDBOp;
import com.charlie.notesyllabus.data.NotesDBOp;
import com.charlie.notesyllabus.entity.Course;
import com.charlie.notesyllabus.util.DataUtil;

public class AddAffair extends Activity implements OnClickListener {

	private EditText et_name;
	private EditText et_start;
	private EditText et_total;
	private EditText et_day;
	private EditText et_room;
	private EditText et_week;
	private EditText et_teacher;

	private Button btn_save;
	private Button btn_cancel;

	//
	private String name = "";
	private int start = 0;
	private int total = 0;
	private String day = "";
	private String room = "";
	private String week = "";
	private String teacher = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_affair_aty);
		
		// 初始化相关控件
		init();
	}

	/**
	 * 初始化相关控件
	 */
	public void init() {
		et_name = (EditText) findViewById(R.id.addAffET_name);
		et_start = (EditText) findViewById(R.id.addAffET_start);
		et_total = (EditText) findViewById(R.id.addAffET_total);
		et_day = (EditText) findViewById(R.id.addAffET_day);
		et_room = (EditText) findViewById(R.id.addAffET_room);
		et_week = (EditText) findViewById(R.id.addAffET_week);
		et_teacher = (EditText) findViewById(R.id.addAffET_teacher);

		btn_save = (Button) findViewById(R.id.addAffBtn_save);
		btn_cancel = (Button) findViewById(R.id.addAffBtn_cancel);

		btn_save.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.addAffBtn_cancel:
			finish();
			break;

		case R.id.addAffBtn_save:
			// 检查输入是否合法
			if (!checkInput()) {
				return;
			}

			final Course course = new Course(name, day, room, start, total, week,
					teacher);

			// 检查是否产生冲突
			if (DataUtil.checkConflict(this, course)) {
				new AlertDialog.Builder(this).setTitle("警告")
						.setMessage("与已有课程产生冲突，请检查！").setPositiveButton("确定", null)
						.show();
			}
			
			// 要弹出显示的信息
			final String msg = "课程事务：" + name + "\n"
					+ "星期：" + day + "\n"
					+ "开始节数：" + start + "\n"
					+ "总共节数：" + total + "\n"
					+ "课室：" + room + "\n"
					+ "周跨度：" + week + "\n"
					+ "讲师：" + teacher + "\n";
			
			// 合格则先进行确定，再进行存储操作
			new AlertDialog.Builder(AddAffair.this)
					.setTitle("确认添加")
					.setMessage(msg)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									CourseDBOp.storeSingleCourse(AddAffair.this, course);
									NotesDBOp.createNoteTable(AddAffair.this);
									finish();
								}
							}).setNegativeButton("取消", null).show();

			
			//break;
		}
	}

	/**
	 * 检查输入是否合法
	 * 
	 * @return
	 */
	public boolean checkInput() {
		boolean legality = true;
		String tmp;

		// 检查课程名称，非空即可
		tmp = et_name.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("课程名称不可为空！").setPositiveButton("确定", null)
					.show();
			legality = false;
		}
		name = tmp;

		// 检查开始节号，必须为1-15的数字
		tmp = et_start.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || !isInteger(tmp)
				|| Integer.parseInt(tmp) > 15 || Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("开始节数应为1到15之间的数字！")
					.setPositiveButton("确定", null).show();
			legality = false;
		}
		start = Integer.parseInt(tmp);

		// 检查总共节数，必须为1-15的数字
		tmp = et_total.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || !isInteger(tmp)
				|| Integer.parseInt(tmp) > 15 || Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("总共节数应为1到15之间的数字！")
					.setPositiveButton("确定", null).show();
			legality = false;
		}
		total = Integer.parseInt(tmp);

		// 检查每一天的课，必须为1-7的数字
		tmp = et_day.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || !isInteger(tmp)
				|| Integer.parseInt(tmp) > 7 || Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("星期应为1到7之间的数字！").setPositiveButton("确定", null)
					.show();
			legality = false;
		}
		day = DataUtil.getWeekday(Integer.parseInt(tmp));

		// 检查课室，非空即可
		tmp = et_room.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("课室不可为空！")
					.setPositiveButton("确定", null).show();
			legality = false;
		}
		room = tmp;

		// 检查持续周数，非空即可
		tmp = et_week.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("持续周数不可为空！").setPositiveButton("确定", null)
					.show();
			legality = false;
		}
		week = tmp;

		// 检查讲师名称，非空即可
		tmp = et_teacher.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("讲师名称不可为空！").setPositiveButton("确定", null)
					.show();
			legality = false;
		}
		teacher = tmp;

		// 检查节数是否超出范围
		if (start + total - 1 > 15) {
			legality = false;
		}

		return legality;
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param value
	 * @return
	 */
	public boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
