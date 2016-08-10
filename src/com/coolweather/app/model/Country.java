package com.coolweather.app.model;

public class Country
{
	/**
	 * 县 id
	 */
	private int id;
	/**
	 * 县名
	 */
	private String countryName;
	/**
	 * 县代码
	 */
	private String countryCode;
	/**
	 * 城市 id
	 */
	private int cityId;
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getCountryName()
	{
		return countryName;
	}
	public void setCountryName(String countryName)
	{
		this.countryName = countryName;
	}
	public String getCountryCode()
	{
		return countryCode;
	}
	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}
	public int getCityId()
	{
		return cityId;
	}
	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}
}
