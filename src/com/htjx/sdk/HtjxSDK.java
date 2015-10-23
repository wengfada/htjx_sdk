package com.htjx.sdk;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.htjx.sdk.domain.Response;
import com.htjx.sdk.domain.UpdateInfo;
import com.htjx.sdk.download.DownloadInfo;
import com.htjx.sdk.download.Downloader;
import com.htjx.sdk.net.FeedbackParser;
import com.htjx.sdk.net.RequestVo;
import com.htjx.sdk.net.UpdateInfoParser;
import com.htjx.sdk.utils.AppInfoUtil;
import com.htjx.sdk.utils.BaseParamsMapUtil;
import com.htjx.sdk.utils.LogUtils;
import com.htjx.sdk.utils.MyAsyncTask;
import com.htjx.sdk.utils.NetUtil;
import com.umeng.analytics.MobclickAgent;

public class HtjxSDK {
	/**
	 * 应用启动提交
	 * @param context
	 */
	public static void onCreate(final Context context){
		CrashHandler.getInstance().init(context);
		Intent service = new Intent();
		service.setAction("android.intent.action.STARTSDK");
		context.startService(service);
		submitData(context,BaseParamsMapUtil.getOnCreate(context,""));
		
	}
	/**
	 * 应用推出提交
	 * @param context
	 */
	public static void onDestroy(final Context context){
		submitData(context,BaseParamsMapUtil.getOnDestroy(context,""));
		
	}
	public static void onResume(Context context){
		MobclickAgent.onResume(context);
		
	}

	public static void onPause(Context context){
		MobclickAgent.onPause(context);
	}
	/**
	 * 提交错误
	 * @param context 上下文
	 * @param error 错误
	 */
	public static void onError(Context context,String error){
		submitData(context,BaseParamsMapUtil.getOnError(context, error, ""));
	}

	/**
	 * 服务启动提交
	 * @param context 上下文
	 * @param location 地理位置信息
	 */
	public static void onStartService(Context context,String location){
		submitData(context,BaseParamsMapUtil.getOnStartService(context, location,""));
	}
	/**
	 * 服务推出提交
	 * @param context 上下文
	 */
	public static void onExitService(Context context){
		submitData(context,BaseParamsMapUtil.getExitService(context, ""));
	}
	
	
	private static UpdateInfo updateInfo;
	/**
	 * 检查版本升级
	 * @param activity 
	 */
	public static void checkVersionUpdate(final Activity activity,final View view,final TextView tv_update_info,final TextView tv_update_size,final TextView tv_update_version,final Button btn_sure,final Button btn_cancel,final Handler handler,final boolean isShowTost) {
		if(NetUtil.hasConnectedNetwork(activity)){
			new MyAsyncTask() {
				@Override
				public void onPreExecute() {
				}
				@Override
				public void onPostExecute() {
					showVersionDialog(activity, view, tv_update_info, tv_update_size, tv_update_version, btn_sure, btn_cancel,handler, isShowTost);
				}
				@Override
				public void doInBackground() {
					RequestVo requestVo = new RequestVo(activity, BaseParamsMapUtil.getVersionUpdate(activity,""), new UpdateInfoParser());
					try {
						SparseArray<UpdateInfo> sa = (SparseArray<UpdateInfo>) NetUtil.get(requestVo);
						int keyAt = sa.keyAt(0);
						if(keyAt==0){
							updateInfo = sa.valueAt(0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}.execute();
		}
		
	}
	/**
	 * 弹出升级窗口
	 * @param activity activity
	 * @param view
	 * @param tv_update_info
	 * @param tv_update_size
	 * @param tv_update_version
	 * @param btn_sure
	 * @param btn_cancel
	 * @param isShowTost 是否弹出提示
	 */
	public static void showVersionDialog(final Activity activity,View view,TextView tv_update_info,TextView tv_update_size,TextView tv_update_version,Button btn_sure,Button btn_cancel,final Handler handler, boolean isShowTost) {
		if(updateInfo!=null&&AppInfoUtil.getVersionCode(activity)<updateInfo.getCode()){
			//View view = View.inflate(activity,R.layout.dialog_update_apk, null);
		//	TextView tv_update_info = (TextView) view.findViewById(R.id.tv_update_info);
			//TextView tv_update_size = (TextView) view.findViewById(R.id.tv_update_size);
			//TextView tv_update_version = (TextView) view.findViewById(R.id.tv_update_vesrion);
			tv_update_info.setText(updateInfo.getContent());
			tv_update_version.setText("软件版本 :V"+updateInfo.getName());
			tv_update_size.setText("软件大小 :"+ updateInfo.getSize());
			AlertDialog.Builder builder = new Builder(activity);
			final AlertDialog verDialog = builder.create();
			// 设置dialog的布局,并显示
			verDialog.setView(view,0,0,0,0);
			verDialog.show();
			verDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
				}
			});
			//Button btn_sure = (Button) view.findViewById(R.id.nav_s);
			//Button btn_cancel = (Button) view.findViewById(R.id.pov_s);
			btn_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					verDialog.dismiss();
				}
			});
			btn_sure.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences sp = activity.getSharedPreferences("config",
							Context.MODE_PRIVATE);
					String img_url = sp.getString("img_url", "");
					DownloadInfo downloadInfo=new DownloadInfo("me", img_url+updateInfo.getApkurl(), updateInfo.getName());
					Downloader downloader = new Downloader(activity, downloadInfo,2);
					try {
						downloader.setHandler(handler);
						downloader.download(null);
						
						LogUtils.toast(activity, "开始在后台更新");
					} catch (Exception e) {
						e.printStackTrace();
					}
					verDialog.dismiss();
				}
			});
			handler.sendEmptyMessage(4);
		}else{
			if(isShowTost){
				LogUtils.toast(activity, "当前已是最新版本");
			}
		}
	}
	/**
	 * 提交服务接口
	 * @param context 上下文
	 * @param submitDataMap 提交键值对Map
	 */
	private static void submitData(final Context context,final Map<String, String> submitDataMap ) {
		if (NetUtil.hasConnectedNetwork(context)) {
			new Thread() {
				public void run() {
					try {
						RequestVo requestVo = new RequestVo(context, submitDataMap,new FeedbackParser());
						SparseArray<Response<String>> array = (SparseArray<Response<String>>) NetUtil.get(requestVo);
						int code = array.keyAt(0);
						if (code == 0) {
							Response<String> response = array.valueAt(0);
							String result = response.getResultSingle();
							if (result != null && !result.equals("")) {
								LogUtils.d("提交成功");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
}
