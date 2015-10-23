package com.htjx.sdk.upload;

import java.io.File;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.htjx.sdk.domain.FormFile;
import com.htjx.sdk.domain.UploadFile;
import com.htjx.sdk.utils.MyASyncProgress;

/**
 * 上传文件工具类
 * 
 * @author fada
 * 
 */
public class Uploader {
	/**
	 * 上传文件,并自已处理进度
	 * 
	 * @param requestUrl
	 *            上传路径
	 * @param file
	 *            上传文件
	 * @param paramsMap
	 *            上传参数
	 * @param handler
	 *            用于反馈已上传数据长度
	 *            "获取方法 msg.getData().getString("以文件名当key");"(不需要就为null)
	 */
	public static void uploadFile(String requestUrl, File file,
			final Map<String, String> paramsMap,  Handler handler) {
		FormFile formFile =new FormFile(file);
		FormFile[] formFiles=new FormFile[]{formFile};
		UploadFile uploadFile = new UploadFile(requestUrl, paramsMap, formFiles, handler);
		new AsyncTask<UploadFile, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(UploadFile... params) {
				try {
					return SocketHttpRequester.post(params[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}.execute(uploadFile);


	}
	/**
	 * 上传文字,没有文件
	 * 
	 * @param requestUrl
	 *            上传路径
	 * @param paramsMap
	 *            上传参数
	 */
	public static void uploadNoFile(String requestUrl,Map<String, String> paramsMap) {
		UploadFile uploadFile = new UploadFile(requestUrl, paramsMap);
		new AsyncTask<UploadFile, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(UploadFile... params) {
				try {
					return SocketHttpRequester.post(params[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}.execute(uploadFile);
		
		
	}
	/**
	 * 批量上传文件,并自已处理进度
	 * 
	 * @param requestUrl
	 *            上传路径
	 * @param files
	 *            上传文件数组
	 * @param paramsMap
	 *            上传参数
	 * @param handler
	 *            用于反馈已下载数据长度
	 *            "获取方法 msg.getData().getString("以文件名当key");"(不需要就为null)
	 */
	public static void uploadFiles(String requestUrl, File[] files,Map<String, String> paramsMap,Handler handler) {
		FormFile[] formFiles=new FormFile[files.length];
		for (int i = 0; i < files.length; i++) {
			FormFile formFile =new FormFile(files[i]);
			formFiles[i]=formFile;
		}
		UploadFile uploadFile=new UploadFile(requestUrl, paramsMap, formFiles,handler);
		new AsyncTask<UploadFile, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(UploadFile... params) {
				try {
					UploadFile uploadFile=params[0];
					return SocketHttpRequester.post(uploadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}.execute(uploadFile);

	}
	/**
	 * 批量上传文件,并自带进度
	 * @param context
	 *            上下文
	 * @param pdialog
	 *            进度条 可以为空 
	 * @param requestUrl
	 *            上传路径
	 * @param files
	 *            上传文件数组
	 * @param paramsMap
	 *            上传参数
	 */
	public static void uploadFilesProgress(Context context,ProgressDialog pdialog,String requestUrl, File[] files,Map<String, String> paramsMap) {
		FormFile[] formFiles=new FormFile[files.length];
		for (int i = 0; i < files.length; i++) {
			FormFile formFile =new FormFile(files[i]);
			formFiles[i]=formFile;
		}
		UploadFile uploadFile=new UploadFile(requestUrl, paramsMap, formFiles);
		new MyASyncProgress(context, pdialog).execute(uploadFile);
	}
	/**
	 * 批量上传文件,并自带进度
	 * 
	 * @param context
	 *            上下文
	 * @param pdialog
	 *            进度条 可以为空
	 * @param requestUrl
	 *            上传路径
	 * @param file
	 *            上传文件
	 * @param paramsMap
	 *            上传参数
	 */
	public static void uploadFilesProgress(Context context,ProgressDialog pdialog,String requestUrl, File file,Map<String, String> paramsMap) {
		FormFile[] formFiles=new FormFile[]{new FormFile(file)};
		UploadFile uploadFile=new UploadFile(requestUrl, paramsMap, formFiles);
		new MyASyncProgress(context, pdialog).execute(uploadFile);
	}
}
