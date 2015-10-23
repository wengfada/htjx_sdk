package com.htjx.sdk.domain;

import java.io.Serializable;
/**
 * 升级版本对象
 * @author fada
 *
 */
public class UpdateInfo implements Serializable{

	private static final long serialVersionUID = 4326573484228942300L;
	private String name;//版本名
	private int code;
	private String size;
	private String content;
	private String apkurl;
	/**
	 *
	 * @return  版本名
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @return 版本下载地址
	 */
	public String getApkurl() {
		return apkurl;
	}

	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return 版本号
	 */
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * 
	 * @return 版本大小
	 */
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * 
	 * @return 更新内容
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 
	 * @param name 版本名
	 * @param code 版本号
	 * @param size 版本大小
	 * @param content 更新内容
	 */
	public UpdateInfo(String name, int code, String size, String content) {
		super();
		this.name = name;
		this.code = code;
		this.size = size;
		this.content = content;
	}
	/**
	 * 
	* @param name 版本名
	 * @param size 版本大小
	 * @param content 更新内容
	 */
	public UpdateInfo(String name, String size, String content) {
		super();
		this.name = name;
		this.size = size;
		this.content = content;
	}
	public UpdateInfo() {
		super();
	}
	/**
	 * 
	 * @param name 版本名
	 * @param code 版本号
	 * @param size 版本大小
	 * @param content 更新内容
	 * @param apkurl APK下载地址
	 */
	public UpdateInfo(String name, int code, String size, String content,
			String apkurl) {
		super();
		this.name = name;
		this.code = code;
		this.size = size;
		this.content = content;
		this.apkurl = apkurl;
	}

	@Override
	public String toString() {
		return "UpdateInfo [name=" + name + ", code=" + code + ", Size=" + size
				+ ", content=" + content + ", apkurl=" + apkurl + "]";
	}

	

}
