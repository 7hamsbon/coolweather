package com.coolweather.app.util;

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
