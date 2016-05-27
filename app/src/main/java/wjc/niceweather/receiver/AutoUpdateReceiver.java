package wjc.niceweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import wjc.niceweather.service.AutoUpdateService;

/**
 * AutoUpdateReceiver一被激活就会执行onReceive调用startService，这样更新服务的onStartCommand方法就会再次执行
 * 而自动更新天气的服务也会在后台运行八小时后再次激活这个接收器
 * 由此实现后台定时更新天气的功能
 *
 * Created by wjc on 16/5/27.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(i);

    }
}
