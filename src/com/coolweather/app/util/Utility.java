package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

/**
 *	用来处理服务器返回的信息
 */
public class Utility
{
	/**
	 * 解析和处理服务器传过来的天气信息，并存储在本地
	 */
	public static void handleWeatherResponse(Context context,String response)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			//获得天气信息
			String cityName = weatherInfo.getString("city");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String publishTime = weatherInfo.getString("ptime");
			String weatherCode = weatherInfo.getString("cityid");
			String weatherDesp = weatherInfo.getString("weather");
			//存储进文件
			saveWeatherInfo(context,cityName,weatherCode,weatherDesp,temp1,temp2,publishTime);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将服务器返回的信息存储在 SharedPreferences 文件中
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
			String weatherDesp,String temp1,String temp2,String publishTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		//获得 SharedPreferences.Editor 
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("publish_time", publishTime);
		//保存已选择城市
		editor.putBoolean("selected_city", true);
		//保存当前日期
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
	/**
	 * 解析和处理服务器传过来的省信息,并将信息存储到数据库
	 */
	public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB ,String response)
	{
		boolean result = false;
		if(response.length()>0)
		{
			String[] provinces = response.split(",");
			if(provinces.length > 0)
			{
				for(String province:provinces)
				{
					Province provinceInstance = new Province();
					String[] array = province.split("\\|");
					provinceInstance.setProvinceName(array[1]);
					provinceInstance.setProvinceCode(array[0]);
					coolWeatherDB.saveProvince(provinceInstance);
				}
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 解析和处理服务器传过来的市信息,并将信息存储到数据库
	 */
	public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB ,String response,int provinceId)
	{
		boolean result = false;
		if(response.length()>0)
		{
			String[] cities = response.split(",");
			if(cities.length > 0)
			{
				for(String city:cities)
				{
					City cityInstance = new City();
					String[] array = city.split("\\|");
					cityInstance.setCityCode(array[0]);
					cityInstance.setCityName(array[1]);
					cityInstance.setProvinceId(provinceId);
					coolWeatherDB.saveCity(cityInstance);
				}
				result = true;
			}
		}
		return result;
	}

	/**
	 * 解析和处理服务器传过来的县信息,并将信息存储到数据库
	 */
	public static boolean handleCountryResponse(CoolWeatherDB coolWeatherDB ,String response,int cityId)
	{
		boolean result = false;
		if(response.length()>0)
		{
			String[] countries = response.split(",");
			if(countries.length > 0)
			{
				for(String country:countries)
				{
					Country countryInstance = new Country();
					String[] array = country.split("\\|");
					countryInstance.setCountryCode(array[0]);
					countryInstance.setCountryName(array[1]);
					countryInstance.setCityId(cityId);
					coolWeatherDB.saveCountry(countryInstance);
				}
				result = true;
			}
		}
		return result;
	}
	
}
