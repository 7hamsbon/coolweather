package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AutoUpdateService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//开启线程更新天气
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				updateWeather();
			}
		}).start();
		//设置定时器，8小时后发送到 AutoUpdateReceiver 去
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int eightHour = 8*60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + eightHour;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, operation);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气并存储在 SharedPreferences 中
	 */
	private void updateWeather()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = pref.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode))
		{
			String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
			{
				@Override
				public void onFinished(String response)
				{
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				}
				@Override
				public void onError(Exception e)
				{
					e.printStackTrace();
				}
			});
		}
	}
	
}
