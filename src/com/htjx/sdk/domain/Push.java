package com.htjx.sdk.domain;

import java.io.Serializable;
/**
 * 推送软件对象
 * @author fada
 *
 */
public class Push implements Serializable {

	private static final long serialVersionUID = -3031429330954540204L;
	private String id;//软件id
	private String name;//软件名
	private String info;//软件描述
	private String downloadUrl;//软件下载url
	private String pkName;//要打开的应用包名
	private String className;//要打开的应用页面class
	private int isNotice;//是否要弹出通知 0表示不弹出 后台下载好了提示安装 1表示弹出 点击进入一个页面
	private int isSilent;//是否尝试静默安装 0表示不请求 1表示请求root权限静默安装
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	/**
	 * 
	 * @return 是否要弹出通知 0表示不弹出 后台下载好了提示安装 1表示弹出 点击进入一个页面
	 */
	public int getIsNotice() {
		return isNotice;
	}
	public void setIsNotice(int isNotice) {
		this.isNotice = isNotice;
	}
	/**
	 * 
	 * @return 是否尝试静默安装 0表示不请求 1表示请求root权限静默安装
	 */
	public int getIsSilent() {
		return isSilent;
	}
	public void setIsSilent(int isSilent) {
		this.isSilent = isSilent;
	}
	/**
	 * 
	 * @return 要打开的应用包名
	 */
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}
	/**
	 * 
	 * @return 要打开的应用页面class
	 */
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * 构造方法
	 * @param id 软件id
	 * @param name 软件名
	 * @param info 软件描述
	 * @param downloadUrl 软件下载url
	 * @param isNotice 是否要弹出通知 0表示不弹出 后台下载好了提示安装 1表示弹出 点击进入一个页面
	 * @param isSilent 是否尝试静默安装 0表示不请求 1表示请求root权限静默安装
	 */
	public Push(String id, String name, String info, String downloadUrl,
			int isNotice, int isSilent) {
		super();
		this.id = id;
		this.name = name;
		this.info = info;
		this.downloadUrl = downloadUrl;
		this.isNotice = isNotice;
		this.isSilent = isSilent;
	}
	/**
	 * 构造方法
	 * @param id 软件id
	 * @param name 软件名
	 * @param info 软件描述
	 * @param downloadUrl 软件下载url
	 */
	public Push(String id, String name, String info, String downloadUrl) {
		super();
		this.id = id;
		this.name = name;
		this.info = info;
		this.downloadUrl = downloadUrl;
	}
	/**
	 * 构造方法
	 * @param id 软件id
	 * @param name 软件名
	 * @param info 软件描述
	 * @param downloadUrl 软件下载url
	 * @param pkName 要打开的应用包名
	 * @param className 要打开的应用页面class
	 * @param isNotice 是否要弹出通知 0表示不弹出 后台下载好了提示安装 1表示弹出 点击进入一个页面
	 * @param isSilent 是否尝试静默安装 0表示不请求 1表示请求root权限静默安装
	 */
	public Push(String id, String name, String info, String downloadUrl,
			String pkName, String className, int isNotice, int isSilent) {
		super();
		this.id = id;
		this.name = name;
		this.info = info;
		this.downloadUrl = downloadUrl;
		this.pkName = pkName;
		this.className = className;
		this.isNotice = isNotice;
		this.isSilent = isSilent;
	}
	public Push() {
		super();
	}
	@Override
	public String toString() {
		return "Push [id=" + id + ", name=" + name + ", info=" + info
				+ ", downloadUrl=" + downloadUrl + ", pkName=" + pkName
				+ ", className=" + className + ", isNotice=" + isNotice
				+ ", isSilent=" + isSilent + "]";
	}
	
	

}
