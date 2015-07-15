package com.charlie.notesyllabus.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.affair.AddAffair;
import com.charlie.notesyllabus.aimode.AIMode;
import com.charlie.notesyllabus.data.CourseDB;
import com.charlie.notesyllabus.data.CourseDBOp;
import com.charlie.notesyllabus.data.NotesDBOp;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.elect.Elect;
import com.charlie.notesyllabus.entity.Course;
import com.charlie.notesyllabus.note.AddNote;
import com.charlie.notesyllabus.note.Note;
import com.charlie.notesyllabus.syllabus.Login;
import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {

	// 第一行第一个空白格
	protected TextView empty;
	// 第一行星期一到星期日的格子
	protected TextView monColum;
	protected TextView tueColum;
	protected TextView wedColum;
	protected TextView thrusColum;
	protected TextView friColum;
	protected TextView satColum;
	protected TextView sunColum;

	// 课程表主体布局
	protected RelativeLayout course_table_layout;
	// 屏幕宽度 
	protected int screenWidth;
	// 课程格子平均宽度 
	protected int aveWidth;

	protected int gridHeight;

	private ListView lv;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化课表视图，绘制网格
		initView();
		// 初始化左拉菜单列表
		initList();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void initView() {
		empty = (TextView) this.findViewById(R.id.test_empty);
		monColum = (TextView) this.findViewById(R.id.test_monday_course);
		tueColum = (TextView) this.findViewById(R.id.test_tuesday_course);
		wedColum = (TextView) this.findViewById(R.id.test_wednesday_course);
		thrusColum = (TextView) this.findViewById(R.id.test_thursday_course);
		friColum = (TextView) this.findViewById(R.id.test_friday_course);
		satColum = (TextView) this.findViewById(R.id.test_saturday_course);
		sunColum = (TextView) this.findViewById(R.id.test_sunday_course);

		course_table_layout = (RelativeLayout) this
				.findViewById(R.id.test_course_rl);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 屏幕宽度
		int width = dm.widthPixels;
		// 平均宽度
		int aveWidth = width / 8;
		
		empty.setWidth(aveWidth * 3 / 4);
		monColum.setWidth(aveWidth * 33 / 32 + 1);
		tueColum.setWidth(aveWidth * 33 / 32 + 1);
		wedColum.setWidth(aveWidth * 33 / 32 + 1);
		thrusColum.setWidth(aveWidth * 33 / 32 + 1);
		friColum.setWidth(aveWidth * 33 / 32 + 1);
		satColum.setWidth(aveWidth * 33 / 32 + 1);
		sunColum.setWidth(aveWidth * 33 / 32 + 1);
		this.screenWidth = width;
		this.aveWidth = aveWidth;
		int height = dm.heightPixels;
		gridHeight = height / 10;
		// 设置课表界面
		// 动态生成15 * maxCourseNum个textview
		for (int i = 1; i <= 15; i++) {
			for (int j = 1; j <= 8; j++) {
				TextView tx = new TextView(MainActivity.this);
				tx.setId((i - 1) * 8 + j);
				// 除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
				if (j < 8)
					tx.setBackgroundDrawable(MainActivity.this.getResources()
							.getDrawable(R.drawable.course_text_view_bg));
				else
					tx.setBackgroundDrawable(MainActivity.this.getResources()
							.getDrawable(R.drawable.course_table_last_colum));
				// 相对布局参数
				RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
						aveWidth * 33 / 32 + 1, gridHeight);
				// 文字对齐方式
				tx.setGravity(Gravity.CENTER);
				// 字体样式
				tx.setTextAppearance(this, R.style.courseTableText);
				// 如果是第一列，需要设置课的序号（1 到 12）
				if (j == 1) {
					tx.setText(String.valueOf(i));
					rp.width = aveWidth * 3 / 4;
					// 设置他们的相对位置
					if (i == 1)
						rp.addRule(RelativeLayout.BELOW, empty.getId());
					else
						rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
				} else {
					rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
					rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
					tx.setText("");
				}

				tx.setLayoutParams(rp);
				course_table_layout.addView(tx);
			}
		}

	}

	/**
	 * 初始化侧滑菜单
	 */
	public void initList() {
		// 得到DrawerLayout对象的引用
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// 得到ListView对象的引用
		lv = (ListView) findViewById(R.id.left_drawer);
		
		// 为ListView设置Adapter来绑定数据
		lv.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Config.MENU_LIST));

		// 设置监听事件
		lv.setOnItemClickListener(this);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(Config.ACTIONBAR_OPEN_TITLE);
				// Call OnPrepareOptionsMenu 
				invalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getActionBar().setTitle(Config.ACTIONBAR_CLOSE_TITLE);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	/**
	 * 检查当前课表信息
	 */
	public void checkCourses() {

		List<Course> courses = CourseDBOp.loadCourses(this);

		// 数据库中不含任何内容
		if (courses.size() == 0) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("通知")
					.setMessage("当前无课程，是否进行导入?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent i = new Intent(MainActivity.this, Login.class);
									startActivity(i);
								}
							}).setNegativeButton("取消", null).show();
		}
		// 数据库中已有课程信息
		else {
			// 将课程表绘制到UI
			addCoursesView(courses);
		}
	}

	public void addCoursesView(List<Course> courses) {
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		int day = 0;

		for (int i = 0; i < courses.size(); i++) {
			courseName = courses.get(i).getName();
			startLesson = courses.get(i).getStartLesson();
			totalLesson = courses.get(i).getTotalLesson();
			classroom = courses.get(i).getClassroom();
			day = DataUtil.getWeekday(courses.get(i).getDay());

			// 添加课程信息
			TextView courseInfo = new TextView(this);
			courseInfo.setText(courseName + "\n@" + classroom);
			// 该textview的高度根据其节数的跨度来设置
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					aveWidth, gridHeight * totalLesson);
			// textview的位置由课程开始节数和上课的时间（day of week）确定
			rlp.topMargin = (startLesson - 1) * gridHeight;
			
			// 偏移由这节课是星期几决定
			rlp.addRule(RelativeLayout.RIGHT_OF, day);
			// 字体居中
			courseInfo.setGravity(Gravity.CENTER);
			// 设置随机背景颜色
			courseInfo.setBackgroundResource(Config.BACKGROUND_COLOR[i % 7]);
			courseInfo.setTextSize(11);
			courseInfo.setLayoutParams(rlp);
			courseInfo.setTextColor(Color.WHITE);
			// 设置不透明度
			courseInfo.getBackground().setAlpha(255);
			course_table_layout.addView(courseInfo);
			
			// 设置点击事件
			courseInfo.setOnClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// 判断哪个选项被点
		switch (position) {
		
		case 0:
			Intent loginIntent = new Intent(MainActivity.this, Login.class);
			startActivity(loginIntent);
			break;

		// 启动添加课程/事务界面
		case 1:
			Intent i3 = new Intent(MainActivity.this, AddAffair.class);
			startActivity(i3);
			break;	
			
		// 启动选课界面
		case 2:
			Intent i = new Intent(MainActivity.this, Elect.class);
			startActivity(i);
			break;

		// 启动笔记界面
		case 3:
			new AlertDialog.Builder(this).setItems(Config.NOTE_SUB_MENU,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								// 查看笔记
								Intent i2 = new Intent(MainActivity.this, Note.class);
								startActivity(i2);
							} else {
								// 新建笔记，选择笔记类型：文本、图片、视频
								new AlertDialog.Builder(MainActivity.this)
										.setItems(Config.NOTE_TYPE_SELECT,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface arg0, int which) {
														Intent i3 = new Intent(MainActivity.this, AddNote.class);
														i3.putExtra(Config.NOTE_TYPE, which + 1);
														startActivity(i3);
													}
												}).show();
							}
						}
					}).show();
			break;
			
		// 启动模式设置
		case 4:
			Intent i4 = new Intent(MainActivity.this, AIMode.class);
			startActivity(i4);
			break;
			
		// 删除信息
		case 5:
			// 先清除笔记再清除课程，次序不可乱
			new AlertDialog.Builder(MainActivity.this).setMessage(
					"清除信息将删除已导入的课程表与笔记，是否继续").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							NotesDBOp.deleteAllNotes(MainActivity.this);
							CourseDBOp.deleteAllCourses(MainActivity.this);
							Preferences.delUserMsg(MainActivity.this);
							finish();
						}
					}).setNegativeButton("取消", null).show();
			
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkCourses();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(R.id.left_layout);
//		boolean isDrawerOpen = false;
		menu.findItem(R.id.open_drawer).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 将ActionBar上的图标与Drawer结合起来
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		switch (item.getItemId()) {
		case R.id.open_drawer:
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 需要将ActionDrawerToggle与DrawerLayout的状态同步
		// 将ActionDrawerToggle中的drawer图标，设置为ActionBar的Home-Button的Icon
		mDrawerToggle.syncState();
	}

	// 课程信息的点击事件
	@Override
	public void onClick(final View view) {
		// 获取课程信息，如“软件测试技术@C304”
		String tmp = ((TextView) view).getText().toString();
		// 切割字符串
		tmp = tmp.split("\n")[0];
		// 查询数据库，获取课程信息
		ArrayList<Course> courses = CourseDBOp.queryCourses(this,
				CourseDB.COURSE_NAME + "=?", new String[] { tmp });
		final Course course = courses.get(0);
		courses = null;
		
		
		// 要弹出显示的信息
					final String msg = "课程事务: " + course.getName() + "\n"
							+ "星期: " + course.getDay() + "\n"
							+ "节数: " + course.getStartLesson() + "-" + 
								(course.getStartLesson()  + course.getTotalLesson() - 1) + "\n"
							+ "课室: " + course.getClassroom() + "\n"
							+ "周跨度：" + course.getWeek() + "\n"
							+ "讲师: " + course.getTeacher() + "\n";
		
		new AlertDialog.Builder(this).setTitle("课程信息").setMessage(msg)
				.setNegativeButton("删除", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("警告")
								.setMessage("该操作将删除该课程与其相关笔记，是否继续?")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// 删除课程，包括笔记
								NotesDBOp.delSingleCourseNote(MainActivity.this, course.getName());
								CourseDBOp.deleteSingleCourse(MainActivity.this, course.getName());
								course_table_layout.removeView((TextView) view);
							}
						}).show();
					}
				}).show();

	}
	
	
}
