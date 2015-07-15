package com.charlie.notesyllabus.syllabus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.util.HttpUtil;

public class Login extends Activity implements OnClickListener {
	private EditText etUsername;
	private EditText etPassword;
	private EditText etCheckcode;
	private ImageView ivCheckCode;
	private Button btnLogin;

	String username = "";
	String password = "";
	String checkCode = "";

	// 登陆进度对话框
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_aty);

		// 检查是否已经登陆过，是则直接跳转至导入界面
//		if (Preferences.hasUser(this)) {
//			Intent i = new Intent(Login.this, ImportCourses.class);
//			startActivity(i);
//			finish();
//		}

		etUsername = (EditText) findViewById(R.id.username);
		etPassword = (EditText) findViewById(R.id.password);
		etCheckcode = (EditText) findViewById(R.id.checkcode);
		ivCheckCode = (ImageView) findViewById(R.id.iv_code);
		btnLogin = (Button) findViewById(R.id.login);

		btnLogin.setOnClickListener(this);
		
		// 加载验证码
		refreshCheckCode();
	}

	@Override
	public void onClick(View arg0) {
		// 获取输入的学号密码
		username = etUsername.getText().toString();
		password = etPassword.getText().toString();
		checkCode = etCheckcode.getText().toString();
		
		// 检查是否为空
		if (username.length() == 0 || password.length() == 0) {
			// 弹出提示框
			new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("学号 & 密码不能为空！").setPositiveButton("确定", null)
					.show();
			
			return ;
		}

		// 异步操作，联网查看登陆合法性
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialog.show(Login.this,
						"正在登陆", "请稍后...", true, false);
			}

			@Override
			protected Boolean doInBackground(String... arg0) {
				// 传入sid和password，成功则返回true
				return HttpUtil.checkLogin(arg0[0], arg0[1], arg0[2]);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				progressDialog.dismiss();
				
				// 检查登陆是否成功
				if (result == false) {
					new AlertDialog.Builder(Login.this)
							.setTitle("提示")
							.setMessage("帐号密码错误或验证码错误")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
//									refresh();
									refreshCheckCode();
								}
							})
							.show();
				}
				else {
					// 存储用户学号与密码
					Preferences.storeUserMsg(Login.this, username, password);
					
					Intent i = new Intent(Login.this, ImportCourses.class);
					startActivity(i);
					finish();
				}
			}
		}.execute(new String[] { username, password, checkCode });

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
				ivCheckCode.setImageBitmap(result);
			}

		}.execute();
	}
}