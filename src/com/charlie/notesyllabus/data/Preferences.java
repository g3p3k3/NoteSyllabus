package com.charlie.notesyllabus.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;

import com.charlie.notesyllabus.util.Config;

public class Preferences {

	/**
	 * 储存用户登录的用户名（学号sid）与密码
	 * 
	 * @param context
	 * @param username
	 * @param password
	 */
	public static void storeUserMsg(Context context, String username,
			String password) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Config.USERNAME, username);
		editor.putString(Config.PASSWORD, password);
		editor.commit();
	}

	/**
	 * 获取用户名与密码
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getUserMsg(Context context) {
		// msg[0]为学号，msg[1]为密码
		String[] msg = new String[2];
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);

		msg[0] = sharedPreferences.getString(Config.USERNAME, "");
		msg[1] = sharedPreferences.getString(Config.PASSWORD, "");

		return msg;
	}

	/**
	 * 检查是否已经登录过用户
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasUser(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);

		String name = sharedPreferences.getString(Config.USERNAME, "");
		// 存在用户
		if (name.length() > 0) {
			return true;
		}
		// 不存在用户
		else {
			return false;
		}
	}

	/**
	 * 存储当前音量设置
	 * 
	 * @param context
	 * @param systemVol
	 *            系统音量
	 * @param ring_vol
	 *            铃音音量
	 * @param alarm_vol
	 *            提示音量
	 * @param ringMode
	 *            铃音模式
	 */
	public static void storeVolumeSettings(Context context, int systemVol,
			int ringVol, int alarmVol, int ringMode) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putInt(Config.VOLUME_SYSTEM, systemVol);
		editor.putInt(Config.VOLUME_RING, ringVol);
		editor.putInt(Config.VOLUME_ALARM, alarmVol);
		editor.putInt(Config.RING_MODE, ringMode);
		editor.commit();
	}

	/**
	 * 获取用户的音量设置与铃音模式
	 * 
	 * @param context
	 * @return
	 */
	public static int[] getVolumeSettings(Context context) {
		int[] volumes = new int[4];

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);

		volumes[0] = sharedPreferences.getInt(Config.VOLUME_SYSTEM, 0);
		volumes[1] = sharedPreferences.getInt(Config.VOLUME_RING, 0);
		volumes[2] = sharedPreferences.getInt(Config.VOLUME_ALARM, 0);
		volumes[3] = sharedPreferences.getInt(Config.RING_MODE, AudioManager.RINGER_MODE_SILENT);

		return volumes;
	}
	
	/**
	 * 删除用户信息，包括学号、密码及各种设置
	 * @param context
	 */
	public static void delUserMsg(Context context) {
		// 清除用户信息
		SharedPreferences prf = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);
		Editor editor = prf.edit();
		editor.clear().commit();
	}
	
	/**
	 * 获取模式设置
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean getModeSetting(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);
		
		return sharedPreferences.getBoolean(key, false);
	}
	
	/**
	 * 存储模式设置
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void storeModeSetting(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Config.PRF_USER_MSG, Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putBoolean(key, value);
		editor.commit();
	}
}
