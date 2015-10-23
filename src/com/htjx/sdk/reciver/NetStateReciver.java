package com.htjx.sdk.reciver;

import com.htjx.sdk.utils.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
/**
 * 网络变化广播接收者
 * @author fada
 *
 */
public class NetStateReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent();
		service.setAction("android.intent.action.STARTSDK");
		context.startService(service);
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		LogUtils.d("手机网络wifiState="+wifiState+"mobileState="+mobileState);
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			LogUtils.toast(context, "手机网络连接成功");
			LogUtils.d("手机网络连接成功");
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
			LogUtils.toast(context, "手机没有任何的网络");
			LogUtils.d("手机没有任何的网络");
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			LogUtils.toast(context, "无线网络连接成功");
			LogUtils.d("无线网络连接成功");
		}

	}

}
