package wjc.niceweather.util;

import android.text.TextUtils;

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
}







