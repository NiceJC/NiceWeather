package wjc.niceweather.util;

/**
 * Created by wjc on 16/5/22.
 * 接口
 * 用于在HttpUtil类中回调服务返回的结果
 *
 *
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
