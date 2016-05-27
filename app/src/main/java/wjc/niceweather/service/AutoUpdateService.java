package wjc.niceweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import wjc.niceweather.receiver.AutoUpdateReceiver;
import wjc.niceweather.util.HttpCallbackListener;
import wjc.niceweather.util.HttpUtil;
import wjc.niceweather.util.Utility;

/**
 * 为应用加入后台自动更新天气的功能，每隔八小时自动从服务器获取最新的天气信息并更新到本地的SharedPreferences文件
 * 保证用户打开应用时看见的是最新的天气信息
 *
 *
 *
 * Created by wjc on 16/5/26.
 */

/**
 * 服务一旦被启动就会处于后台运行状态，而每调用一次startService，onStartCommand就会被执行一次
 * 重写服务的onStartCommand方法，在这里开启一个新线程进行后台天气数据的更新
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

/**
 * 设置一个定时任务，onStartCommand执行后， 八小时后会激活一个广播接收器
 *
 */
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int eightHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+eightHour;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);


    }



    private void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=preferences.getString("weather_code","");
        String address="http://www.weather.com.cn/adat/cityinfo/"+weatherCode+".html";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });


    }
}
