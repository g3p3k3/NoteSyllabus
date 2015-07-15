package com.charlie.notesyllabus.aimode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.main.MainActivity;
import com.charlie.notesyllabus.util.Config;
import com.charlie.notesyllabus.util.DataUtil;

public class PollingService extends Service {

	public static final String ACTION = "com.charlie.notesyllabus.aimode.PollingService";

	private Notification mNotification;
	private NotificationManager mManager;

	private boolean isSetRing;
	private boolean isSetCilent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		new PollingThread().start();
	}

	// 初始化NotificationManager
	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "课表模式提醒";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	// 弹出通知
	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), "现在没课!",
				pendingIntent);
		mManager.notify(0, mNotification);
	}

	/**
	 * 新建线程执行查询当前所处课程，进行铃音设置
	 */
	class PollingThread extends Thread {
		@Override
		public void run() {
			// 查看当前所在课程
			String curCourse = DataUtil.getCurrentCourse(PollingService.this);

			// 当前无课，则铃音正常
			if (curCourse.equals(Config.DEFAULT)) {
				// 开启铃声
				isSetCilent = false;
				if (!isSetRing) {
					setVolume(true);
					isSetRing = true;
				}
				showNotification();
			}

			// 当前有课，静音开启震动
			else {
				isSetRing = false;
				if (!isSetCilent) {
					setVolume(false);
					isSetCilent = true;
				}

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service:onDestroy");
	}

	/**
	 * 设置手机的音量
	 * 
	 * @param on
	 *            静音模式是否开启
	 */
	public void setVolume(boolean on) {
		int[] volumes = new int[4];
		// 缺省铃音模式
		volumes[3] = AudioManager.RINGER_MODE_VIBRATE;

		if (on) {
			volumes = Preferences.getVolumeSettings(this);
		}

		AudioManager AM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		AM.setStreamVolume(AudioManager.STREAM_SYSTEM, volumes[0], 0);
		AM.setStreamVolume(AudioManager.STREAM_RING, volumes[1], 0);
		AM.setStreamVolume(AudioManager.STREAM_ALARM, volumes[2], 0);
		AM.setRingerMode(volumes[3]);
	}

}
