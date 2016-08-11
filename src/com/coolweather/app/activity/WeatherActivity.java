package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener
{
	/**
	 *	切换城市视图 
	 */
	private Button switchCity;
	/**
	 * 更新天气视图
	 */
	private Button refreshWeather;
	/**
	 * 显示天气的布局，用来设置其可见性
	 */
	private LinearLayout weatherInfoLayout;
	/**
	 * 城市名
	 */
	private TextView cityNameText;
	/**
	 * 发布时间
	 */
	private TextView publishText;
	/**
	 * 当前日期
	 */
	private TextView currentDateText;
	/**
	 * 天气信息
	 */
	private TextView weatherDespText;
	/**
	 * 最低温
	 */
	private TextView temp1Text;
	/**
	 * 最高温
	 */
	private TextView temp2Text;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 获得部件实例
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh_weather);
		
		//为按钮添加监听器
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

		// 获得城市代码
		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode))
		{
			publishText.setText("同步中....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else
		{
			// 没有省级代号的时候显示当地的，即 SharePreferences 文件中已经有本地的天气信息了
			showWeather();
		}
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

	/**
	 * 根据城市信息查询天气代号
	 */
	private void queryWeatherCode(String countryCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * 根据天气代号查询天气信息
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * 从服务器请求 address，并根据 type 做出其他操作
	 */
	private void queryFromServer(String address, final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
		{
			@Override
			public void onFinished(String response)
			{
				//如果查询服务器的类型是查询天气代码
				if ("countryCode".equals(type))
				{
					if(!TextUtils.isEmpty(response))
					{
						String[] array = response.split("\\|");
						if(array!=null&&array.length==2)
						{
							SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
							SharedPreferences.Editor editor = pref.edit();
							String weatherCode = array[1];
							editor.putString("weather_code", weatherCode);
							editor.commit();
							queryWeatherInfo(weatherCode);
						}
					}
				}
				//如果查询服务器的类型是查询天气详情
				else if("weatherCode".equals(type))
				{
					if(!TextUtils.isEmpty(response))
					{
						//将响应传给工具类去处理并存储
						Utility.handleWeatherResponse(WeatherActivity.this, response);
						//显示天气详情
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								showWeather();
							}
						});
					}
				}
			}

			@Override
			public void onError(Exception e)
			{
				runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 显示天气信息
	 */
	private void showWeather()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		cityNameText.setText(pref.getString("city_name", ""));
		publishText.setText("今天"+pref.getString("publish_time", "")+"发布");
		currentDateText.setText(pref.getString("current_date", ""));
		weatherDespText.setText(pref.getString("weather_desp", ""));
		temp1Text.setText(pref.getString("temp1", ""));
		temp2Text.setText(pref.getString("temp2", ""));
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.switch_city:
				Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
				break;
			case R.id.refresh_weather:
				publishText.setText("同步中.....");
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				String weatherCode = pref.getString("weather_code", "");
				if(!TextUtils.isEmpty(weatherCode))
				{
					queryWeatherInfo(weatherCode);
				}
				break;
			default:
		}
	}

}
