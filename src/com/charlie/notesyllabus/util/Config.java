package com.charlie.notesyllabus.util;

import com.charlie.notesyllabus.R;


public class Config {

	// -----------------------------------------------------------------------//
	/*
	 * MainActivity.java 背景颜色
	 */
	public static int[] BACKGROUND_COLOR = { R.drawable.course_info_blue,
			R.drawable.course_info_green, R.drawable.course_info_red,
			R.drawable.course_info_orange, R.drawable.course_info_yellow,
			R.drawable.course_info_blue2, R.drawable.course_info_green2 };

	// 左拉菜单选项
	public static final String[] MENU_LIST = new String[] { "导入课表",
			"添加课程 / 事务", "选课", "课堂笔记", "上课模式", "删除信息" };

	// 左拉菜单"笔记"选项的子菜单
	public static final String[] NOTE_SUB_MENU = new String[] { "查看笔记", "新建笔记" };
	public static final String NOTE_TYPE = "note_type";
	public static final int NOTE_TYPE_TEXT = 1;
	public static final int NOTE_TYPE_IMAGE = 2;
	public static final int NOTE_TYPE_VIDEO = 3;
	public static final String[] NOTE_TYPE_SELECT = new String[] { "文本", "拍照",
			"视频" };

	// AtionBar的名字
	public static final String ACTIONBAR_CLOSE_TITLE = "Note课程表";
	public static final String ACTIONBAR_OPEN_TITLE = "请选择";

	// -----------------------------------------------------------------------//
	/*
	 * Preferences.java 储存用户账号密码的文件名与键值对名称 储存用户音量设置信息
	 */
	public static final String PRF_USER_MSG = "user_msg_file";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	public static final String VOLUME_SYSTEM = "volume_system";
	public static final String VOLUME_RING = "volume_ring";
	public static final String VOLUME_ALARM = "volume_alarm";
	public static final String RING_MODE = "ring_mode";

	public static final String AI_MODE = "ai_mode";

	// -----------------------------------------------------------------------//
	// 建表所需属性
	public static final String CONTENT = "content";
	public static final String IMAGE = "image";
	public static final String VIDEO = "video";
	public static final String ID = "_id";
	public static final String TIME = "time";

	// -----------------------------------------------------------------------//
	public static final String COURSE_NAME = "COURSE_NAME";

	public static final String DEFAULT = "默认";
}
