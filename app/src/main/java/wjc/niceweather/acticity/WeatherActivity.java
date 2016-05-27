package wjc.niceweather.acticity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import wjc.niceweather.R;
import wjc.niceweather.receiver.AutoUpdateReceiver;
import wjc.niceweather.service.AutoUpdateService;
import wjc.niceweather.util.HttpCallbackListener;
import wjc.niceweather.util.HttpUtil;
import wjc.niceweather.util.Utility;

/**
 * Created by wjc on 16/5/26.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    //显示城市名
    private TextView cityNameText;
    //显示发布时间
    private TextView publishText;
    //显示天气描述信息
    private TextView weatherDespText;
    //用于显示气温1
    private TextView temp1Text;
    //用于显示气温2
    private TextView temp2Text;
    //用于显示当前日期
    private TextView currentDateText;

    //切换城市按钮
    private Button switchCity;
    //更新天气按钮
    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        /**
         * 初始化各控件
         */
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        switchCity= (Button) findViewById(R.id.switch_city);
        refreshWeather= (Button) findViewById(R.id.regresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);


        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            //接受到Intent传递过来的countyCode，进入查询天气的步骤
            publishText.setText("更新天气信息...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);

        } else {
            //没有县级代号时，直接显示本地存储的天气信息
            showWeather();

        }
    }

    //首先,查询县级代号对应的天气代号
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    //根据查询到的对应天气代号进行天气查询
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");

    }

    /**
     * 根据传入的地址和类型去服务器查询天气代号或者天气信息
     * @param address
     * @param type
     */
    private void queryFromServer(final String address, final String type) {
        /**
         * 调用sendHttpRequest方法，开启一条新线程进行服务器数据查询以及返回结果处理
         * onFinish和onError也都是在新线程上进行的
         */
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //解析从服务器返回的天气代号信息
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            //调用queryWeatherInfo，传入查询到的天气代号，进一步进行天气查询
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    //调用handleWeatherResponse，将返回的天气信息解析并存入SharedPreferences文件
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    //回主线程处理UI变更
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("更新失败");
                    }
                });

            }
        });

    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);

        cityNameText.setText(preferences.getString("city_name",""));
        temp1Text.setText(preferences.getString("temp1",""));
        temp2Text.setText(preferences.getString("temp2",""));
        weatherDespText.setText(preferences.getString("weather_desp",""));
        publishText.setText("今天"+preferences.getString("publish_time","")+"发布");
        currentDateText.setText(preferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        //在这里create  AutoUpdateService， 让他在后台运行起来
        //保证客户一旦选择了城市并且更新了天气之后，AutoUpdateService就在后台运行并定时更新天气

        Intent intent=new Intent(this , AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                /**
                 * 对于切换城市的按钮点击事件，直接让他回到选择城市的活动去，
                 * 但是由于已经选中过一个城市，SharedPreferences中的city_selected为true，又会返回到天气活动来，
                 * 所以设置一个from_weather_activity标志位进行后续处理
                 *
                 */
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;

            case R.id.regresh_weather:
                /**
                 * 对于刷新按钮，直接从本地SharedPreferences文件中读取weatherCode
                 * 并调用queryWeatherInfo从服务器获取最新天气信息进行更新
                 */
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=preferences.getString("weatherCode","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                Toast.makeText(WeatherActivity.this,"更新完成",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }


    }
}

