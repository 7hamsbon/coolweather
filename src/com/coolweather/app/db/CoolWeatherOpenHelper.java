package com.coolweather.app.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper
{
	/**
	 * 省级建表语句
	 */
	public static final String CREATE_PROVINCE = "Create Table PROVINCE(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	/**
	 * CITY 建表语句
	 */
	public static final String CREATE_CITY = "Create Table CITY(" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";
	/**
	 * COUNTRY 建表语句
	 */
	public static final String CREATE_COUNTRY = "Create Table COUNTRY(" +
			"id integer primary key autoincrement," +
			"country_name text," +
			"country_code text," +
			"city_id integer)";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version)
	{
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_PROVINCE);  //创建 PROVINCE 表
		db.execSQL(CREATE_CITY);  //创建 CITY 表
		db.execSQL(CREATE_COUNTRY);  //创建 COUNTRY 表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

}
