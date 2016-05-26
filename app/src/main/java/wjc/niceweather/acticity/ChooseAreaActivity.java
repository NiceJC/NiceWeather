package wjc.niceweather.acticity;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wjc.niceweather.R;
import wjc.niceweather.model.City;
import wjc.niceweather.model.County;
import wjc.niceweather.model.NiceWeatherDB;
import wjc.niceweather.model.Province;
import wjc.niceweather.util.HttpCallbackListener;
import wjc.niceweather.util.HttpUtil;
import wjc.niceweather.util.Utility;

/**
 * Created by wjc on 16/5/23.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private NiceWeatherDB niceWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 从数据库抓取数据处理并返回的是一个带范型的List，用一下List来表示
     */

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省
    private Province selectedProvince;
    //选中的市
    private City selectedCity;
    //选中的县
    private County selectedCounty;

    //当前选中的级别
    private int currentLevel;

    //是否从WeatherActivity跳转过来
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从Intent中获取from_weather_activity标志位，默认值为false
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);


        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * city_selected标志位默认返回值为false，即本地文件数据为空时会返回false
         * 对city_selected标志位和from_weather_activity标志位进行判断，
         * 只有在已经有城市被选中过并且不是从WeatherActivity跳转回来的情况行下，才跳转到WeatherActivity
         *
         * 跳转到WeatherActivity后，从Intent中得不到county_Code，就会直接调用showWeather将本地文件中的天气信息显示出来
         */
        if(prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);

        //初始化适配器，其中的dataList参数由之后的各种query方法进行更新并刷新适配器，使ListView的内容发生变化
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        niceWeatherDB = NiceWeatherDB.getInstance(this);

        //注册ListView的Item点击事件，随着点击Item，进行实时的query以及显示内容的更新
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }


            }
        });
        queryProvinces();

    }

    /**
     * 查询所有的省份信息，并加载到ListView的显示内容当中
     * 优先在数据库查询，如果没有 再去服务器查询
     */
    private void queryProvinces() {
        //在数据库中查询
        provinceList = niceWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList
                    ) {
                dataList.add(p.getProvinceName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("省份列表");
            currentLevel = LEVEL_PROVINCE;
        } else {
            //在服务器端查询
            queryFromServer(null, "province");
        }


    }

    /**
     * 查询所选中的省份的所有市的信息，并加载至ListView显示列表
     * 优先从数据库查询，如果没有，在服务器上查询
     *
     */
    private void queryCities() {
//        cityList.clear();
        cityList=niceWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for (City c:cityList
                 ) {
                dataList.add(c.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }



    }

    /**
     * 查询所选市的所有县的信息，并更新到ListView的显示列表上
     *优先数据库查询，如果没有，去服务器查询
     */

    private void queryCounties() {
//        countyList.clear();
        countyList=niceWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for (County c:countyList
                 ) {dataList.add(c.getCountyName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }


    }

    /**
     * 根据传入的带好和类型，从服务器上查询省市县数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        //显示进度条
        showProgressDialog();
        /**
         * 调用sendHttpRequest方法，开启一个新的线程
         * 在新线程上，向服务器发送请求，
         * 得到服务器的返回数据后，HttpCallbackListener的方法被回调，解析返回的数据，将数据存入数据库
         */

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(niceWeatherDB,response);

                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(niceWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(niceWeatherDB,response,selectedCity.getId());
                }
                if(result){
                    //通过runOnUiThread方法回到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }

                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);


        }
        progressDialog.show();

    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){

            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();

        }else {
            if(isFromWeatherActivity){
                //如果时从WeatherActivity跳转过来的，back可回到WeatherActivity（并显示本地存储的天气信息）
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
