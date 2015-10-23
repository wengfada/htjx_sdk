package com.htjx.sdk.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
/**
 * 定位工具类
 * @author fada
 *
 */
public class LocationUtils {
/**
 * 定位,精确到市级
 * @param context 上下文
 * @param handler 传送位置信息  获取方法  msg.getData().getString("location");
 */
	public static  void getLocation(final Context context,final Handler handler) {
		LocationClientOption option = new LocationClientOption();
		// Hight_Accuracy高精度、Battery_Saving低功耗、Device_Sensors仅设备(GPS)
		option.setLocationMode(LocationMode.Battery_Saving);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(200);// 设置发起定位请求的间隔时间为5000ms
		option.setOpenGps(false);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		final LocationClient mLocationClient = new LocationClient(context); // 声明LocationClient类
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				Message msg=Message.obtain();
				Bundle data = msg.getData();
				data.putString("location", ""+location.getProvince()+location.getCity());
				handler.sendMessage(msg);
				mLocationClient.stop();
				
			}
		}); // 注册监听函数
		if (mLocationClient != null) {
			mLocationClient.start();
			if (mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
			} else {
				LogUtils.d("LocSDK4", "locClient is null or not started");
			}
		}

	}


}
