package com.htjx.sdk.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
/**
 * apk工具类
 * @author fada
 *
 */
public class ApkUtils {
/**
 * 安装apk
 * @param context 上下文
 * @param path apk路径
 */
	public static void InstallApk(Context context, String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
/**
 * 卸载apk
 * @param context 上下文
 * @param pkName 包名
 */
	public static void UnInstallApk(Context context, String pkName) {
		Uri packageURI = Uri.parse(pkName);   
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
		context.startActivity(uninstallIntent);
	}
/**
 * 通过包名启动app
 * @param context 上下文
 * @param pkName 包名
 */
	public static void OpenApp(Context context, String pkName) {
	     Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkName);
         context.startActivity(intent); 
	}
	/** 
	 * 静默安装 
	 * @param file 
	 * @return 是否成功
	 */  
	public static boolean slientInstall(File file) {  
	    boolean result = false;  
	    Process process = null;  
	    OutputStream out = null;  
	    try {  
	        process = Runtime.getRuntime().exec("su");  
	        out = process.getOutputStream();  
	        DataOutputStream dataOutputStream = new DataOutputStream(out);  
	        dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");  
	        dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " +  
	                file.getPath());  
	        // 提交命令  
	        dataOutputStream.flush();  
	        // 关闭流操作  
	        dataOutputStream.close();  
	        out.close();  
	        int value = process.waitFor();  
	          
	        // 代表成功  
	        if (value == 0) {  
	            result = true;  
	        } else if (value == 1) { // 失败  
	            result = false;  
	        } else { // 未知情况  
	            result = false;  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } catch (InterruptedException e) {  
	        e.printStackTrace();  
	    }  
	    return result;  
	} 

	/**
	 * 通过apk包获取apk信息
	 * @param context 上下文
	 * @param appPath apk路径
	 * @return 包名
	 */
	public static String getPageNameByApk(Context context, String appPath) {
		if (appPath != null) {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(appPath,PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = info.applicationInfo;
			String packageName = appInfo.packageName; // 得到安装包名称
			//String version=info.versionName;       //得到版本信息   
			//Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
			return packageName;
		} else {
			return null;
		}
	}
	/**
	 * 安装到系统目录下(system/app)
	 * @param context 上下文
	 * @param name 应用名
	 */
	public static void toSystemApp(Context context,String name) {
		String packageName = context.getPackageName();
		String[] commands = {"busybox mount -o remount,rw /system",
                             "busybox cp /data/data/" + packageName + "/files/"+name+" /system/app/"+name,
                             "busybox rm /data/data/" + packageName + "/files/"+name};
		Process process = null;
		DataOutputStream dataOutputStream = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			int length = commands.length;
			for (int i = 0; i < length; i++) {
				LogUtils.d( "commands[" + i + "]:" + commands[i]);
				dataOutputStream.writeBytes(commands[i] + "\n");
			}
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
			LogUtils.d( "copy fail"+ e);
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}
	/**
	 * 安装到手机(data/app)目录下
	 * @param context 上下文
	 * @param name 应用名
	 */
	public static void toDataApp(Context context,String name) {
		String packageName = context.getPackageName();
		String[] commands = {"busybox mount -o remount,rw /system",
                             "busybox cp /data/data/" + packageName + "/files/"+name+" /data/app/"+name,
                             "busybox rm /data/data/" + packageName + "/files/"+name};
		Process process = null;
		DataOutputStream dataOutputStream = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			int length = commands.length;
			for (int i = 0; i < length; i++) {
				LogUtils.d( "commands[" + i + "]:" + commands[i]);
				dataOutputStream.writeBytes(commands[i] + "\n");
			}
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
			LogUtils.d( "copy fail"+ e);
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}
}
