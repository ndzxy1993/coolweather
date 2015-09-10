package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		List<String[]> list=splitResponse(response);
		if(list.size()==0){
			return false;
		}
		for(String [] array:list){
			Province province=new Province();
			province.setProvinceCode(array[0]);
			province.setProvinceName(array[1]);
			//将解析出来的数据存储到Province表
			coolWeatherDB.saveProvince(province);
		}
		return true;
	}
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		List<String[]> list=splitResponse(response);
		if(list.size()==0){
			return false;
		}
		for(String [] array:list){
			City city=new City();
			city.setCityCode(array[0]);
			city.setCityName(array[1]);
			city.setProvinceId(provinceId);
			//将解析出来的数据存储到City表
			coolWeatherDB.saveCity(city);
		}
		return true;
	}
	/**
	 *解析和处理服务器返回的县级数据 
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		List<String[]> list=splitResponse(response);
		if(list.size()==0){
			return false;
		}
		for(String [] array:list){
			County county=new County();
			county.setCountyCode(array[0]);
			county.setCountyName(array[1]);
			county.setCityId(cityId);
			//将解析出来的数据存储到County表
			coolWeatherDB.saveCounty(county);
		}
		return true;
	}
	
	public static List<String []> splitResponse(String response){
		List<String []> list=new ArrayList<String []>();
		if(!TextUtils.isEmpty(response)){
			String [] all=response.split(",");
			if(all!=null && all.length>0){
				for(String p:all){
					String [] array=p.split("\\|");
					list.add(array);
				}
			}
		}
		return list;
	}
	
	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context ,String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 */
	private static void saveWeatherInfo(Context context, String cityName, String weatherCode, 
			String temp1, String temp2, String weatherDesp, String publishTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name",cityName );
		editor.putString("weather_code",weatherCode );
		editor.putString("temp1",temp1 );
		editor.putString("temp2",temp2 );
		editor.putString("weather_desp",weatherDesp );
		editor.putString("publish_time",publishTime );
		editor.putString("current_date",sdf.format(new Date()));
		editor.commit();
	}
	

}
