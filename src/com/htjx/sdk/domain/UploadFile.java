package com.htjx.sdk.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import android.os.Handler;
/**
 * 文件上传请求类
 * @author fada
 *
 */
public class UploadFile implements Serializable {
	private static final long serialVersionUID = 6019114016408029093L;
	private String requestUrl;//请求上传的的服务器地址
	private Map<String, String> paramsMap;//上传参数Map
	private FormFile[] files;//要上传的文件数组
	private Handler handler;//用于反馈已下载数据长度 "获取方法  msg.getData().getString("以文件名当key");"(不需要就为null)
	/**
	 * 
	 * @return 要上传的文件数组
	 */
	public FormFile[] getFiles() {
		return files;
	}
	public void setFiles(FormFile[] files) {
		this.files = files;
	}
	/**
	 * 上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://www.iteye.cn或http://192.168.1.101:8083这样的路径测试)
	 * @return 请求上传的的服务器地址
	 */
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	/**
	 * 
	 * @return 用于反馈已下载数据长度 "获取方法  msg.getData().getString("以文件名当key");"(不需要就为null)
	 */
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	/**
	 * 
	 * @return 上传参数Map
	 */
	public Map<String, String> getParamsMap() {
		return paramsMap;
	}
	
	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
	/**
	 * 上传对象构造方法 含有文件
	 * @param requestUrl 上传地址
	 * @param paramsMap 上传参数
	 * @param files 上传文件数组
	 * @param handler 用于反馈已下载数据长度 "获取方法  msg.getData().getString("以文件名当key");"(不需要就为null)
	 */
	public UploadFile(String requestUrl, Map<String, String> paramsMap,
			FormFile[] files,Handler handler) {
		super();
		this.requestUrl = requestUrl;
		this.paramsMap = paramsMap;
		this.files = files;
		this.handler=handler;
	}
	/**
	 * 上传对象构造方法 含有文件
	 * @param requestUrl 上传地址
	 * @param paramsMap 上传参数
	 * @param files 上传文件数组
	 */
	public UploadFile(String requestUrl, Map<String, String> paramsMap,
			FormFile[] files) {
		super();
		this.requestUrl = requestUrl;
		this.paramsMap = paramsMap;
		this.files = files;
		
	}
	public UploadFile() {
		super();
	}
	/**
	 * 上传对象构造方法 只传文字
	 * @param requestUrl 上传地址
	 * @param paramsMap  上传参数
	 */
	public UploadFile(String requestUrl, Map<String, String> paramsMap) {
		super();
		this.requestUrl = requestUrl;
		this.paramsMap = paramsMap;
	}
	@Override
	public String toString() {
		return "UploadFile [requestUrl=" + requestUrl + ", paramsMap="
				+ paramsMap + ", files=" + Arrays.toString(files) + "]";
	}
	
}
