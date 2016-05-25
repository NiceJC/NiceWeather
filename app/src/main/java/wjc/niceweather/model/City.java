package wjc.niceweather.model;

/**
 * Created by wjc on 16/5/21.
 *
 * City的实体类
 *
 */
public class City {
    private int _id;
    private int provinceId;
    private String cityName;
    private String cityCode;

    public void setId(int id) {
        this._id = id;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getId() {

        return _id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }





}
