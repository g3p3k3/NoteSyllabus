package com.charlie.notesyllabus.aimode;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.util.Config;


public class AIMode extends Activity implements OnCheckedChangeListener {
	
	private CheckBox mode_cb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ai_mode_aty);
		
		mode_cb = (CheckBox) findViewById(R.id.checkbox);
		mode_cb.setOnCheckedChangeListener(this);
		mode_cb.setChecked(Preferences.getModeSetting(this, Config.AI_MODE));
		
		// 获取当前各项音量
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 系统音量
		int system_volume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		// 铃音音量
		int ring_volume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		// 提示音量
		int alarm_volume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		// 铃音模式
		int ring_mode = mAudioManager.getRingerMode();
		// 存储当前音量设置
		Preferences.storeVolumeSettings(this, system_volume, ring_volume, alarm_volume, ring_mode);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// 存储当前状态
		Preferences.storeModeSetting(this, Config.AI_MODE, arg1);
		
		if (arg1) {
			PollingUtils.startPollingService(this, 60, PollingService.class, PollingService.ACTION);
		}
		else {
			PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
		}
	}
}
