package com.charlie.notesyllabus.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.charlie.notesyllabus.util.DataUtil;

public class HttpUtil {
	public static List<Cookie> cookies; // 保存的cookie

	private static String temp_sid = "";

	public static void getCookies(String username, String password, String code) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;
		HttpContext httpContext = new BasicHttpContext();

		String uriAPI = "http://uems.sysu.edu.cn/elect/login";

		// 声明HttpPost方法，指定URI
		HttpPost httpRequest = new HttpPost(uriAPI);
		
		// 设置参数对
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("_eventId", "submit"));
		params.add(new BasicNameValuePair("gateway", "true"));
		params.add(new BasicNameValuePair("j_code", code));
		params.add(new BasicNameValuePair("lt", ""));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("username", username));

		try {
			httpRequest.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			httpRequest.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest.setHeader("Referer", "http://uems.sysu.edu.cn/elect/");
			httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");
			
			// 设置实体，添加参数对
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 执行HTTP请求，获取response
			httpResponse = client.execute(httpRequest, httpContext);

			HttpUriRequest realRequest = (HttpUriRequest) httpContext
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			System.out.println("URI地址:" + realRequest.getURI());

			// 重定向后的URI�?/elect/s/types?sid=94fab0dc-b777-4f47-b728-7c8d622809da
			String redirectUri = realRequest.getURI().toString();
			// 截取sid部分
			temp_sid = redirectUri.split("\\?")[1];

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<StringBuffer> getTable(int year1, int year2, int term) {

		// 获取选课结果的URI
		String uriAPI = "http://uems.sysu.edu.cn/elect/s/courseAll?xnd="+year1+"-"+year2+"&xq="+term+"&"
				+ temp_sid;

		HttpGet httpRequest3 = new HttpGet(uriAPI);
		System.out.println("请求的URI: " + uriAPI);

		try {
			// 使用HttpGet方法，设置Header相关信息
			httpRequest3.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			httpRequest3.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest3.setHeader("Referer",
					"http://uems.sysu.edu.cn/elect/s/types?" + temp_sid);

			// 执行Get请求
			HttpResponse httpResponse2 = new DefaultHttpClient()
					.execute(httpRequest3);


			// 返回码为200即成功?
			if (httpResponse2.getStatusLine().getStatusCode() == 200) {
				// 获取返回的实体
				HttpEntity entity = httpResponse2.getEntity();
				return DataUtil.getCourse(entity);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据用户输入的课程号进行选课
	 * @param courseID
	 * 		课程编号
	 * @return
	 * 		选课是否成功
	 */
	public static boolean electCourse(String courseID) {
		// 选课网址
		String electUrl = "http://uems.sysu.edu.cn/elect/s/elect";
		String sid = temp_sid.split("\\=")[1];
		
		// 声明HttpPost对象
		HttpPost httpRequest = new HttpPost(electUrl);

		try {
			// 设置参数对
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("jxbh",courseID));
			params.add(new BasicNameValuePair("sid", sid));
			
			// 使用HttpGet方法，设置Header相关信息
			httpRequest.setHeader("Cookie", "JSESSIONID=" + cookies.get(0).getValue());
			httpRequest.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");

			// 设置实体，添加参数对
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 执行Get请求
			HttpResponse httpResponse2 = new DefaultHttpClient()
					.execute(httpRequest);

			// 返回200即选课成功
			if (httpResponse2.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 检验能否成功登陆
	 * @param sid
	 * @param password
	 * @param code
	 * @return
	 */
	public static boolean checkLogin(final String sid, final String password, final String code) {
		// indicate the validity of msg that are inputed
		boolean validity = false;
		
		HttpClient client = new DefaultHttpClient();

		String uriAPI = "http://uems.sysu.edu.cn/elect/login";

		HttpPost httpRequest = new HttpPost(uriAPI);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("_eventId", "submit"));
		params.add(new BasicNameValuePair("gateway", "true"));
		params.add(new BasicNameValuePair("j_code", code));
		params.add(new BasicNameValuePair("lt", ""));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("username", sid));
		
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpRequest.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			
			HttpResponse httpResponse = client.execute(httpRequest);

			// 返回200，说明帐号密码正确
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 释放连接
				client.getConnectionManager().shutdown();
				return validity = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 释放连接
		client.getConnectionManager().shutdown();
		
		// 帐号与密码不对应，登陆失败
		return validity;
	}
	
	/**
	 * 获取Cookie，用于后面的处理
	 */
	public static void getFirst() {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;

		String uriAPI = "http://uems.sysu.edu.cn/elect/index.html";

		// 声明HttpPost方法，指定URI
		HttpPost httpRequest = new HttpPost(uriAPI);
		
		try {
			// 设置实体，添加参数对

			httpRequest.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");

			// 执行HTTP请求，获取response
			httpResponse = client.execute(httpRequest);


			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 获取cookie
				cookies = ((AbstractHttpClient) client).getCookieStore()
						.getCookies();

			} else {
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取验证码图片
	 * 
	 * @return 验证码Bitmap对象
	 */
	public static Bitmap getBitmapFromServer() { 
		String uri = "http://uems.sysu.edu.cn/elect/login/code";
		
	    HttpPost post = new HttpPost(uri); 
	    HttpClient client = new DefaultHttpClient(); 
	    Bitmap pic = null; 
	    try {
	    	post.setHeader("Cookie", "JSESSIONID=" + cookies.get(0).getValue());
	    	post.setHeader("Host", "uems.sysu.edu.cn");
	    	post.setHeader("Referer", "http://uems.sysu.edu.cn/elect/");
	    	post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");
			
	    	
	        HttpResponse response = client.execute(post);
	        
	        if (response.getStatusLine().getStatusCode() == 200) {
				Log.i("HttpUtil", "get code success");
			} 
	        HttpEntity entity = response.getEntity(); 
	        InputStream is = entity.getContent(); 

	        pic = BitmapFactory.decodeStream(is);   // 关键代码

	    } catch (ClientProtocolException e) { 
	        e.printStackTrace(); 
	    } catch (IOException e) { 
	        e.printStackTrace(); 
	    } 
	    return pic; 
	}

	public static class RedirectHandler extends DefaultRedirectHandler {

		@Override
		public boolean isRedirectRequested(HttpResponse response,
				HttpContext context) {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
