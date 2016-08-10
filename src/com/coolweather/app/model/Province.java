package com.coolweather.app.model;

public class Province
{
	/**
	 * 表中主键
	 */
	private int id;
	/**
	 * 省名
	 */
	private String provinceName;
	/**
	 * 省代码
	 */
	private String provinceCode;
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getProvinceName()
	{
		return provinceName;
	}
	public void setProvinceName(String provinceName)
	{
		this.provinceName = provinceName;
	}
	public String getProvinceCode()
	{
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode)
	{
		this.provinceCode = provinceCode;
	}
}
