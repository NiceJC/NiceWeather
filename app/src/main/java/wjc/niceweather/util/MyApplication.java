package wjc.niceweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by wjc on 16/5/21.
 * 工具类，对外提供一个  MyApplication.getContext() 的静态方法  用于获取一个全局的Context，
 *
 *
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
