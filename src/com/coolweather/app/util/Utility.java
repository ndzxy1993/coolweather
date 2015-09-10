package com.coolweather.app.util;

import java.util.ArrayList;
import java.util.List;

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
	

}
