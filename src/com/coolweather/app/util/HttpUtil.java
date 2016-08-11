package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil
{

	/**
	 *	根据地址发送 Http 请求，并调用回调方法 
	 */
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				HttpURLConnection conn = null;
				try
				{
					
					//获得 URL
					URL url = new URL(address);
					//获得连接
					conn = (HttpURLConnection)url.openConnection();
					
					//设置请求方法
					conn.setRequestMethod("GET");
					//设置读取超时
					conn.setReadTimeout(8000);
					//设置连接超时
					conn.setConnectTimeout(8000);
					//获得输入流
					InputStream in = conn.getInputStream();
					Log.d("HttpUtil", "1");
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					Log.d("HttpUtil", "2");
					String line ="";
					StringBuffer sb = new StringBuffer();
					Log.d("HttpUtil", "3");
					//获得请求内容存储在 StringBuffer 里面
					while((line = br.readLine()) != null)
					{
						sb.append(line);
					}
					Log.d("HttpUtil", "4");
					//回调方法
					if(listener !=null)
					{
						listener.onFinished(sb.toString());
					}
					Log.d("HttpUtil", "5");
				} catch (Exception e)
				{
					if(listener !=null)
					{
						listener.onError(e);
					}
				} finally
				{
					if(conn != null)
					{
						conn.disconnect();
					}
				}
			}
		}).start();
		
	}
}
