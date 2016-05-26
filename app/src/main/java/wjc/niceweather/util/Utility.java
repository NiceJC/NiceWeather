package wjc.niceweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wjc.niceweather.model.City;
import wjc.niceweather.model.County;
import wjc.niceweather.model.NiceWeatherDB;
import wjc.niceweather.model.Province;

/**
 * Created by wjc on 16/5/23.
 */
public class Utility {
    /*＊
     * @param niceWeatherDB
     * @param response
     * @return
     *
     * 解析和处理服务器返回的Province信息
     * 将服务器返回信息按分隔符分开处理，并储存到province实体类中，再调用NiceWeatherDB的saveProvince方法存入数据库。
     */
    public synchronized static boolean handleProvincesResponse(NiceWeatherDB niceWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allprovinces = response.split(",");
            if (allprovinces != null && allprovinces.length > 0) {
                for (String p : allprovinces
                        ) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);


                    //将解析出来的province的信息存储到 province表中
                    niceWeatherDB.saveProvince(province);

                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析处理服务器返回的City信息
     *
     * @param niceWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean handleCitiesResponse(NiceWeatherDB niceWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities
                        ) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    niceWeatherDB.saveCity(city);


                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 解析处理服务器返回的County信息
     * @param niceWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(NiceWeatherDB niceWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties
                        ) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);

                    niceWeatherDB.saveCounty(county);

                }
                return true;
            }
        }
        return false;


    }

    /**
     * 解析服务器返回的JSON类型的天气信息，并将解析出的数据储存到本地
     * @param context
     * @param response
     */

    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);



            


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 将从服务器返回中解析出的信息存储到SharedPreferences文件中
     *
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param pTime
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String pTime){

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();

        //设置一个city_selected标志位，用于判断当前是否已有城市被选中
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);

        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",pTime);
        //随天气数据一起存储一个获取天气时的日期
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();



    }


}







