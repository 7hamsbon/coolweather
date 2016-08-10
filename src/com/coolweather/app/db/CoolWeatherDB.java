package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

public class CoolWeatherDB
{
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造函数私有化
	 */
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db = helper.getWritableDatabase();
	}
	
	/**
	 * 获取 CoolWeatherDB 的实例
	 */
	public synchronized static CoolWeatherDB getCoolWeatherDB(Context context)
	{
		if(coolWeatherDB == null)
		{
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/**
	 * 将Province 实例存储到数据库
	 */
	public void saveProvince(Province province)
	{
		if(province != null)
		{
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("PROVINCE", null, values);
		}
	}
	
	/**
	 * 从数据库中读取全国所有的省份信息
	 */
	public List<Province> loadProvince()
	{
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("PROVINCE", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do
			{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 将City 实例存储到数据库
	 */
	public void saveCity(City city)
	{
		if(city != null)
		{
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("CITY", null, values);
		}
	}
	
	private void loadCity(List<City> list,Cursor cursor)
	{
		if(cursor.moveToFirst())
		{
			do
			{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
	}
	/**
	 * 获取一个省内的所有城市
	 * @param privinceId 省的 id
	 */
	public List<City> loadCityOfProvince(int provinceId)
	{
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("CITY", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		loadCity(list,cursor);
//		if(cursor.moveToFirst())
//		{
//			do
//			{
//				City city = new City();
//				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
//				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
//				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
//				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
//				list.add(city);
//			}while(cursor.moveToNext());
//		}
//		if(cursor!=null)
//		{
//			cursor.close();
//		}
		return list;
	}
	
	/**
	 * 从数据库中读取全国所有的城市信息
	 */
	public List<City> loadAllCity()
	{
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("CITY", null, null, null, null, null, null);
		loadCity(list,cursor);
//		if(cursor.moveToFirst())
//		{
//			do
//			{
//				City city = new City();
//				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
//				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
//				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
//				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
//				list.add(city);
//			}while(cursor.moveToNext());
//		}
//		if(cursor!=null)
//		{
//			cursor.close();
//		}
		return list;
	}
	
	/**
	 * 将Country 实例存储到数据库
	 */
	public void saveCountry(Country country)
	{
		if(country != null)
		{
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("COUNTRY", null, values);
		}
	}
	
	private void loadCountry(List<Country> list,Cursor cursor)
	{
		if(cursor.moveToFirst())
		{
			do
			{
				Country country = new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(country);
			}while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
	}
	
	/**
	 * 从数据库中读出一个城市里的所有县信息
	 */
	public List<Country> loadCountryOfCity(int cityId)
	{
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("COUNTRY", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		loadCountry(list,cursor);
		return list;
	}
	/**
	 * 从数据库中读取全国所有的县信息
	 */
	public List<Country> loadAllCountry()
	{
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("COUNTRY", null, null, null, null, null, null);
		loadCountry(list,cursor);
		return list;
	}
	
}
