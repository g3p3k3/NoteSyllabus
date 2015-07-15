package com.charlie.notesyllabus.entity;

public class Course {
	// 课程名称
	private String name;
	// 星期几
	private String day;
	// 上课地点
	private String classroom;
	// 第几节开始上课
	private int startLesson;
	// 共几节
	private int totalLesson;
	// 持续的周数：如1-18周
	private String week;
	// 讲师姓名
	private String teacher;

	// 主构造方法
	public Course(String name, String day, String classroom, int startLesson,
			int totalLesson, String week, String teacher) {
		super();
		this.name = name;
		this.day = day;
		this.classroom = classroom;
		this.startLesson = startLesson;
		this.totalLesson = totalLesson;
		this.week = week;
		this.teacher = teacher;
	}

	// 空构造方法
	public Course() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public int getStartLesson() {
		return startLesson;
	}

	public void setStartLesson(int startLesson) {
		this.startLesson = startLesson;
	}

	public int getTotalLesson() {
		return totalLesson;
	}

	public void setTotalLesson(int totalLesson) {
		this.totalLesson = totalLesson;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
}
