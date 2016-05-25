package wjc.niceweather.model;

/**
 * Created by wjc on 16/5/21.
 *
 * Province的实体类
 */
public class Province {
    private int _id;
    private String provinceName;
    private String provinceCode;


    public void setId(int id) {
        this._id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {

        return _id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }
}
