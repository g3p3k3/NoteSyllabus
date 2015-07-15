package com.charlie.notesyllabus.elect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.charlie.notesyllabus.R;
import com.charlie.notesyllabus.data.Preferences;
import com.charlie.notesyllabus.util.HttpUtil;

public class Elect extends Activity implements OnClickListener {

	private Button btn_elect;
	private EditText et_courseID;
	private EditText et_code;
	private ImageView iv_code;
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.elect_aty);

		et_courseID = (EditText) findViewById(R.id.et1);
		et_code = (EditText) findViewById(R.id.et_elect_code);
		iv_code = (ImageView) findViewById(R.id.iv_elect_code);

		btn_elect = (Button) findViewById(R.id.btn_elect);
		btn_elect.setOnClickListener(this);
		
		refreshCheckCode();
	}

	@Override
	public void onClick(View arg0) {

		// 检查课程号输入是否为空
		String input = et_courseID.getText().toString();
		if (input.length() == 0) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("课程号不可为空！")
					.setPositiveButton("确定", null)
					.show();
			return ;
		}
		
		// 检查课程号是否合法(均为数字)(方法：正则表达式)
		Pattern pattern = Pattern.compile("[0-9]*"); 
	    Matcher isNum = pattern.matcher(input);
	    if (!isNum.matches()){
	    	new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("课程号格式不正确！")
					.setPositiveButton("确定", null)
					.show();
	    	return ;
	    }
	    
	    // 获取输入的验证码
	    String code = et_code.getText().toString();
	    if (code.length() == 0) {
	    	new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("验证码不可为空！")
				.setPositiveButton("确定", null)
				.show();
	    	return ;
		}
	    
	    // 合法的输入，进行选课操作
		elect(input, code);
	}
	
	/**
	 * 选课
	 * @param courseID
	 * @param code
	 */
	public void elect(String courseID, final String code) {
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialog.show(Elect.this,
						"正在选课", "请稍后...", true, false);
			}

			@Override
			protected Boolean doInBackground(String... s) {
				// 获取储存的账号密码
				String[] msg = Preferences.getUserMsg(Elect.this);
				HttpUtil.getCookies(msg[0], msg[1], code);

				return HttpUtil.electCourse(s[0]);
			}

			@Override
			protected void onPostExecute(final Boolean result) {
				progressDialog.dismiss();
				
				String msg = "";
				if (result) {
					msg = "选课成功";
				} else {
					msg = "选课失败";
				}
				
				new AlertDialog.Builder(Elect.this)
						.setTitle("提示")
						.setMessage(msg)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (!result) {
									refreshCheckCode();
								}
							}
						})
						.show();
			};

		}.execute(courseID);
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
				iv_code.setImageBitmap(result);
			}

		}.execute();
	}
}
