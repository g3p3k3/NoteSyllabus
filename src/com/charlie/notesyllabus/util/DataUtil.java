package com.charlie.notesyllabus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;

import android.content.Context;

import com.charlie.notesyllabus.data.CourseDB;
import com.charlie.notesyllabus.data.CourseDBOp;
import com.charlie.notesyllabus.entity.Course;

public class DataUtil {

	/**
	 * 解析html实体，返回有关课程的部分
	 * 
	 * @param entity
	 * @return
	 */
	public static List<StringBuffer> getCourse(HttpEntity entity) {
		List<StringBuffer> courseList = new ArrayList<StringBuffer>();
		boolean isIn_tbody = false; // 仅在<tbody>内才截取相关内容
		boolean isIn_tr_class = false;

		try {
			InputStream is;
			is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));

			String data = "";
			StringBuffer sb = new StringBuffer();
			while ((data = br.readLine()) != null) {
				// System.out.println(data);

				if (isIn_tbody) {
					if (data.contains("odd") || data.contains("even")) {
						isIn_tr_class = true;
					}

					// 在class odd或even里，添加信息
					if (isIn_tr_class) {
						if (data.length() > 0) {
							sb.append(data + "\n");
							// System.out.println(data+ "   长度为"+data.length());
						}
					}

					// 结束，完成一个课程的添加
					if (data.contains("</tr>")) {
						isIn_tr_class = false;
						courseList.add(sb);
						// 清空
						sb = new StringBuffer();
					}
				} else if (data.contains("<tbody>")) {
					System.out.println("呵呵");
					isIn_tbody = true;
				} else if (data.contains("</tbody>")) {
					break;
				}
			}
			br.close();
			is.close();

			return courseList;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Course> get(List<StringBuffer> htmStr) {
		List<Course> result = new ArrayList<Course>();
		if (htmStr == null) {
			return result;
		}
		
		for (int j = 0; j < htmStr.size(); j++) {
			String tmp = htmStr.get(j).toString();
			String strArr[] = tmp.split("\n");
			String name = null, classroom = null, week = null, day = null, teacher = null;
			int startLesson = 0, totalLesson = 0;

			boolean lack_msg = false; // 无上课信息
			// All:
			for (int i = 0; i < strArr.length; i++) {

				if (i == 5) {
					int first = strArr[i].indexOf(">", 30);
					int last = strArr[i].indexOf("<", 30);
					name = strArr[i].substring(first + 1, last);

				}
				if (i == 10) {
					// 软件综合实验这类没有上课信息的课程
					if (strArr[i].length() < 49) {
						lack_msg = true;
						break;
					}
					day = strArr[i].substring(20, 23);
					int mark = strArr[i].indexOf("-");
					int end;
					if (strArr[i].charAt(mark - 2) != ' ') {
						startLesson = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark - 2)))
								* 10
								+ Integer.parseInt(String.valueOf(strArr[i]
										.charAt(mark - 1)));
					} else {
						startLesson = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark - 1)));
					}
					// System.out.println("文冰的函数："+startLesson);
					if (strArr[i].charAt(mark + 2) != ' ') {
						end = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark + 1)))
								* 10
								+ Integer.parseInt(String.valueOf(strArr[i]
										.charAt(mark + 2)));
					} else {
						end = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark + 1)));
					}
					totalLesson = end - startLesson + 1;
					int placeFirst = strArr[i].indexOf("/");
					int placeLast = strArr[i].indexOf("（");
					classroom = strArr[i].substring(placeFirst + 1, placeLast);
					int weekLast = strArr[i].indexOf("）");
					week = strArr[i].substring(placeLast + 1, weekLast);
				}
				if (i == 14) {
					int firstTeacher = strArr[i].indexOf(">");
					int lastTeacher = strArr[i].indexOf("<", 19);
					teacher = strArr[i]
							.substring(firstTeacher + 1, lastTeacher);
				}

			}
			if (lack_msg) {
				continue;
			}

			Course tmpCourse = new Course(name, day, classroom, startLesson,
					totalLesson, week, teacher);
			result.add(tmpCourse);
		}

		return result;
	}

	/**
	 * 获取所有课程名称，并且在末尾添加一个缺省字符串变量 用于打开数据库操作
	 * 
	 * @param context
	 *            上下文变量
	 * @return 所有课程名称，加一个缺省字符串""
	 */
	public static ArrayList<String> getCoursesName(Context context) {
		// 载入课表信息
		List<Course> courses = (ArrayList<Course>) CourseDBOp
				.loadCourses(context);

		if (courses.size() > 0) {
			ArrayList<String> courseList = new ArrayList<String>();

			for (int i = 0; i < courses.size(); i++) {
				courseList.add(courses.get(i).getName());
			}
			// 添加缺省字符串
			courseList.add(Config.DEFAULT);

			return courseList;
		}

		return null;
	}

	/**
	 * 获取当前的课程名称，没有则返回缺省字符串"默认"
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentCourse(Context context) {
		String weekday = "";
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		Date d = new Date();
		cal.setTime(d);
		int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (day < 0)
			day = 0;
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);
		int current_total_minutes = currentHour * 60 + currentMinute;

		weekday = weekDays[day];

		ArrayList<Course> courses = CourseDBOp.queryCourses(context,
				CourseDB.DAY + "=?", new String[] { weekday });

		// 计算当前时间是否处于某节课中
		for (int i = 0; i < courses.size(); i++) {
			// 某节课开始的时间，以总分钟计算
			int min_m = 8 * 60 + (courses.get(i).getStartLesson() - 1) * 55;
			// 某节课结束的时间，以总分钟计算
			int max_m = min_m + courses.get(i).getTotalLesson() * 55 - 10;

			if (min_m <= current_total_minutes
					&& max_m >= current_total_minutes) {
				return courses.get(i).getName();
			}
		}

		return Config.DEFAULT;
	}
	
	/**
	 * 检查添加的单个课程是否与现有课程产生冲突
	 * @param context
	 * @param course
	 * @return
	 */
	public static boolean checkConflict(Context context, Course course) {
		boolean conflict = false;
		
		int start_1 = course.getStartLesson();
		int end_1 = start_1 + course.getTotalLesson() - 1;
		
		// 获取当天的所有课程
		String weekday = course.getDay();
		ArrayList<Course> courses = CourseDBOp.queryCourses(context,
				CourseDB.DAY + "=?", new String[] { weekday });
		
		// 遍历检查冲突
		for (int i = 0; i < courses.size(); i++) {
			// 检查是否发生交叉
			int start_2 = courses.get(i).getStartLesson();
			int end_2 = start_2 + courses.get(i).getTotalLesson() - 1;
			// a覆盖b
			if (start_1 <= start_2 && end_1 >= end_2) {
				conflict = true;
				break;
			}
			
			// b包含a
			if (start_1 >= start_2 && end_1 <= end_2) {
				conflict = true;
				break;
			}
			
			// 左交叉
			if (end_1 > start_2 && end_1 < end_2) {
				conflict = true;
				break;
			}
			
			// 右交叉
			if (start_1 > start_2 && start_1 < end_2) {
				conflict = true;
				break;
			}
		}
		
		return conflict;
	}

	/**
	 * 将 "星期一"等字符串处理，返回int 1
	 * 
	 * @param day
	 * @return
	 */
	public static int getWeekday(String day) {
		int result = 0;
		day = day.substring(2);

		if (day.equals("一")) {
			result = 1;
		} else if (day.equals("二")) {
			result = 2;
		} else if (day.equals("三")) {
			result = 3;
		} else if (day.equals("四")) {
			result = 4;
		} else if (day.equals("五")) {
			result = 5;
		} else if (day.equals("六")) {
			result = 6;
		} else if (day.equals("日")) {
			result = 7;
		}

		return result;
	}

	/**
	 * 将数字1-7转化为星期一到星期日
	 * 
	 * @param day
	 * @return
	 */
	public static String getWeekday(int day) {
		String weekday = null;
		switch (day) {
		case 1:
			weekday = "星期一";
			break;
		case 2:
			weekday = "星期二";
			break;
		case 3:
			weekday = "星期三";
			break;
		case 4:
			weekday = "星期四";
			break;
		case 5:
			weekday = "星期五";
			break;
		case 6:
			weekday = "星期六";
			break;
		case 7:
			weekday = "星期日";
			break;
		}
		return weekday;
	}

	/**
	 * 删除指定文件
	 * 
	 * @param uri
	 *            文件URI
	 */
	public static void deleteFile(String uri) {

		if (uri.length() != 0) {
			File f = new File(uri);
			f.delete();
		}
	}
}
