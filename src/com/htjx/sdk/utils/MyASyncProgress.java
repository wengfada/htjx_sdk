package com.htjx.sdk.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.htjx.sdk.domain.FormFile;
import com.htjx.sdk.domain.UploadFile;
/**
 * 包含进度的异步任务 (三个参数分别为(请求参数:FormFile,进度参数:Integer,返回结果:Boolean))
 * @author fada
 *
 */
public class MyASyncProgress extends AsyncTask<UploadFile, Integer, Boolean> {
	// 可变长的输入参数，与AsyncTask.exucute()对应
	ProgressDialog pdialog;

	public MyASyncProgress(Context context,ProgressDialog pdialog) {
		if(pdialog!=null){
			this.pdialog=pdialog;
		}else{
			pdialog = new ProgressDialog(context, 0);
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
			}
		});
		pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				
			}
		});
		pdialog.setCancelable(true);
		pdialog.setMax(100);
		pdialog.show();

	}
   //在子线程执行的方法
	@Override
	protected Boolean doInBackground(UploadFile... uploadFiles) {
		
		 try {
			final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
			final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
			UploadFile uploadFile = uploadFiles[0];
			int fileDataLength = 0;
			String path = uploadFile.getRequestUrl();
			Map<String, String> params = uploadFile.getParamsMap();
			FormFile[] files = uploadFile.getFiles();
			Handler handler = uploadFile.getHandler();
			if (files != null) {
				for (FormFile formFile : files) {//得到文件类型数据的总长度
					StringBuilder fileExplain = new StringBuilder();
					fileExplain.append("--");
					fileExplain.append(BOUNDARY);
					fileExplain.append("\r\n");
					fileExplain.append("Content-Disposition: form-data;name=\""
							+ formFile.getParameterName() + "\";filename=\""
							+ formFile.getFilename() + "\"\r\n");
					fileExplain.append("Content-Type: "
							+ formFile.getContentType() + "\r\n\r\n");
					fileExplain.append("\r\n");
					fileDataLength += fileExplain.length();
					if (formFile.getInStream() != null) {
						fileDataLength += formFile.getFile().length();
					} else {
						byte[] data = formFile.getData();
						if (data != null) {
							fileDataLength += formFile.getData().length;
						}
					}
				}
			}
			StringBuilder textEntity = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
				textEntity.append("--");
				textEntity.append(BOUNDARY);
				textEntity.append("\r\n");
				textEntity.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				textEntity.append(entry.getValue());
				textEntity.append("\r\n");
			}
			//计算传输给服务器的实体数据总长度
			int dataLength = textEntity.toString().getBytes().length
					+ fileDataLength + endline.getBytes().length;
			if (path == null || path.equals("")) {
				return false;
			}
			URL url = new URL(path);
			int port = url.getPort() == -1 ? 80 : url.getPort();
			Socket socket = new Socket(InetAddress.getByName(url.getHost()),
					port);
			//socket.getSendBufferSize()
			OutputStream outStream = socket.getOutputStream();
			//下面完成HTTP请求头的发送
			String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
			outStream.write(requestmethod.getBytes());
			String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
			outStream.write(accept.getBytes());
			String language = "Accept-Language: zh-CN\r\n";
			outStream.write(language.getBytes());
			String contenttype = "Content-Type: multipart/form-data; boundary="
					+ BOUNDARY + "\r\n";
			outStream.write(contenttype.getBytes());
			String contentlength = "Content-Length: " + dataLength + "\r\n";
			outStream.write(contentlength.getBytes());
			String alive = "Connection: Keep-Alive\r\n";
			outStream.write(alive.getBytes());
			String host = "Host: " + url.getHost() + ":" + port + "\r\n";
			outStream.write(host.getBytes());
			//写完HTTP请求头后根据HTTP协议再写一个回车换行
			outStream.write("\r\n".getBytes());
			//把所有文本类型的实体数据发送出来
			outStream.write(textEntity.toString().getBytes());
			//把所有文件类型的实体数据发送出来
			if (files != null) {
				Message message = Message.obtain();
				Bundle data = message.getData();
				for (FormFile formFile : files) {
					StringBuilder fileEntity = new StringBuilder();
					fileEntity.append("--");
					fileEntity.append(BOUNDARY);
					fileEntity.append("\r\n");
					fileEntity.append("Content-Disposition: form-data;name=\""
							+ formFile.getParameterName() + "\";filename=\""
							+ formFile.getFilename() + "\"\r\n");
					fileEntity.append("Content-Type: "
							+ formFile.getContentType() + "\r\n\r\n");
					outStream.write(fileEntity.toString().getBytes());
					if (formFile.getInStream() != null) {
						byte[] buffer = new byte[1024];
						int len = 0;
						long uploadLeng = 0L;
						while ((len = formFile.getInStream().read(buffer, 0,
								1024)) != -1) {
							outStream.write(buffer, 0, len);
							uploadLeng += len;
							publishProgress((int)(uploadLeng/formFile.getFile().length()));
							if (handler != null) {
								data.putLong(formFile.getFilename(), uploadLeng);
								handler.sendMessage(message);
							}
						}
						formFile.getInStream().close();
					} else {
						outStream.write(formFile.getData(), 0,
								formFile.getData().length);
					}
					outStream.write("\r\n".getBytes());
				}
			}
			//下面发送数据结束标志，表示数据已经结束
			outStream.write(endline.getBytes());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			if (reader.readLine().indexOf("200") == -1) {//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
				return false;
			}
			outStream.flush();
			outStream.close();
			reader.close();
			socket.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return false;

	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
//子线程执行完了
	@Override
	protected void onPostExecute(Boolean result) {
		// 返回HTML页面的内容
		pdialog.dismiss();
		
	}
//在请求前你做什么事件
	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度
		pdialog.setProgress(values[0]);
	}

}
