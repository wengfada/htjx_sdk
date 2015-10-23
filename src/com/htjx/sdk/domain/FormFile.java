package com.htjx.sdk.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 上传文件对象
 */
public class FormFile implements Serializable{

	private static final long serialVersionUID = 8759849844428133113L;
	/* 上传文件的数据 */
	private byte[] data;//字节数据
	private InputStream inStream;//文件流
	private File file;//文件
	private String filename;//文件名称
	private String parameterName = "file";//请求参数名称 默认为"file"
	private String contentType = "application/octet-stream";//内容类型  (默认为二进制流"application/octet-stream")
	/**
	 * 文件上传构造方法
	 * @param filename 文件名
	 * @param data 字节数组
	 * @param parameterName  请求参数名称 默认为"file"
	 * @param contentType 内容类型  (默认为二进制流"application/octet-stream")
	 */
	public FormFile(String filename, byte[] data, String parameterName,
			String contentType) {
		this.data = data;
		this.filename = filename;
		this.parameterName = parameterName;
		if (contentType != null)
			this.contentType = contentType;
	}
	/**
	 * 构造方法
	 * @param filename 文件名
	 * @param data 字节数组
	 * 
	 */
	public FormFile(String filename,byte[] data) {
		super();
		this.filename = filename;
		if(data!=null){
			this.data = data;
		}
	}

	/**
	 * 文件上传类构造方法
	 * @param filename 文件名
	 * @param file 文件
	 * @param parameterName 请求参数名(要与服务器一致)
	 * @param contentType 文件类型
	 */
	public FormFile(String filename, File file, String parameterName,
			String contentType) {
		this.filename = filename;
		this.parameterName = parameterName;
		try {
			if (file != null) {
				this.file = file;
				this.inStream = new FileInputStream(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (contentType != null)
			this.contentType = contentType;
	}
	/**
	 * 构造方法 
	 * @param file 要上传的文件
	 */
	public FormFile(File file) {
		super();
		try {
			if (file != null) {
				this.file = file;
				this.filename=file.getName();
				this.inStream = new FileInputStream(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public File getFile() {
		return file;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * 
	 * @return 请求参数名称 默认为"file"
	 */
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	/**
	 * 
	 * @return 内容类型  (默认为二进制流"application/octet-stream")
	 */
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
	public void setFile(File file) {
		this.file = file;
	}
	@Override
	public String toString() {
		return "FormFile [data=" + Arrays.toString(data) + ", inStream="
				+ inStream + ", file=" + file + ", filename=" + filename
				+ ", parameterName=" + parameterName + ", contentType="
				+ contentType + "]";
	}
	

}
