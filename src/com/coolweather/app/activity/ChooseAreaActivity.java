package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.collweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity
{
	public final static int LEVEL_PROVINCE = 0;
	public final static int LEVEL_CITY = 1;
	public final static int LEVEL_COUNTRY = 2;
	
	/**
	 * 加载对话框
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * 记录当时的页面显示的是什么页面
	 */
	private int currentLevel;
	/**
	 *	db操作类 
	 */
	private CoolWeatherDB coolWeatherDB;
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县列表
	 */
	private List<Country> countryList;
	
	/**
	 * 标题
	 */
	private TextView textView;
	/**
	 * 列表视图
	 */
	private ListView listView;
	/**
	 * 存放当时放在 ListView 中的字符数组
	 */
	private List<String> dataList = new ArrayList<String>();
	/**
	 * ListView 适配器
	 */
	private ArrayAdapter<String> adapter; 
	/**
	 * 存放选中的 省
	 */
	private Province selectedProvince;
	/**
	 * 存放选中的 市
	 */
	private City selectedCity;
	/**
	 * 用来判断是否只有一个市
	 */
	private boolean isOnlyCity = false;
	/**
	 * 存放选中的 县
	 */
//	private Country selectedCountry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//判断是不是有默认城市，有的话直接跳转
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isSelectedCity = pref.getBoolean("selected_city", false);
		if(isSelectedCity)
		{
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		//获取部件实例
		textView = (TextView)findViewById(R.id.title_text);
		listView = (ListView)findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		//获取数据库操作类实例
		coolWeatherDB = CoolWeatherDB.getCoolWeatherDB(this);
		//为子项添加监听器
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if(currentLevel == LEVEL_PROVINCE)
				{
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY)
				{
					selectedCity = cityList.get(position);
					queryCountries();
				}else if(currentLevel == LEVEL_COUNTRY)
				{
					//获取县代号
					String countryCode = countryList.get(position).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					//将县代号作为一个参数传入
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
					return;
				}
			}
		});
		queryProvinces();
	}
	/**
	 * 查询县列表
	 */
	private void queryCountries()
	{
		countryList = coolWeatherDB.loadCountryOfCity(selectedCity.getId());
		if(countryList.size()>0)
		{
			dataList.clear();
			for(Country country:countryList)
			{
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else
		{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	/**
	 * 查询市列表
	 */
	private void queryCities()
	{
		cityList = coolWeatherDB.loadCityOfProvince(selectedProvince.getId());
		if(cityList.size()>0)
		{
			if(cityList.size()>1)
			{
				dataList.clear();
				for(City city:cityList)
				{
					dataList.add(city.getCityName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				textView.setText(selectedProvince.getProvinceName());
				currentLevel = LEVEL_CITY;
			}
			else
			{
				selectedCity = cityList.get(0);
				isOnlyCity = true;
				queryCountries();
			}
		}else
		{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	/**
	 * 查询省列表
	 */
	private void queryProvinces()
	{
		provinceList = coolWeatherDB.loadProvince();
		if(provinceList.size()>0)
		{
			dataList.clear();
			for(Province province:provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else
		{
			queryFromServer(null,"province");
		}
	}
	private void queryFromServer(final String code,final String type)
	{
		showProgressDialog();
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		else
		{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
		{
			@Override
			public void onFinished(String response)
			{
				boolean result = false;
				if("province".equals(type))
				{
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type))
				{
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("country".equals(type))
				{
					result = Utility.handleCountryResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result)
				{
					runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							if("province".equals(type))
							{
								queryProvinces();
							}else if("city".equals(type))
							{
								queryCities();
							}else if("country".equals(type))
							{
								queryCountries();
							}
							closeProgressDialog();
						}
					});
				}
					
			}
			@Override
			public void onError(Exception e)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog()
	{
		if(progressDialog == null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("加载中。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}
	/**
	 * 按了 Back 键之后，根据当前活动处于什么 LEVEL，做出不同的动作
	 */
	@Override
	public void onBackPressed()
	{
		if(currentLevel == LEVEL_PROVINCE)
		{
			finish();
		}else if(currentLevel == LEVEL_CITY)
		{
			queryProvinces();
		}else if(currentLevel == LEVEL_COUNTRY)
		{
			if(isOnlyCity)
			{
				isOnlyCity = false;
				queryProvinces();
			}
			else
			{
				queryCities();
			}
		}
	}
	
}
