package wjc.niceweather.model;

/**
 * Created by wjc on 16/5/21.
 *
 * County的实体类
 *
 */
public class County {
    private int id;
    private int cityId;
    private String countyName;
    private String countyCode;

    public int getId() {
        return id;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }
}
