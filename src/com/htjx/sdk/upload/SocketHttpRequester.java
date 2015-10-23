package com.htjx.sdk.upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.htjx.sdk.domain.FormFile;
import com.htjx.sdk.domain.UploadFile;

/**
 * 上传文件到服务器
 * 
 * @author fada
 *
 */
public class SocketHttpRequester {
    /**
     * 批量上传 
     * 直接通过HTTP协议提交数据到服务器, 实现如下面表单提交功能:
     *   <FORM METHOD=POST ACTION="http://192.168.1.101:8083/upload/servlet/UploadServlet" enctype="multipart/form-data">
            <INPUT TYPE="text" NAME="name">
            <INPUT TYPE="text" NAME="id">
            <input type="file" name="imagefile"/>
            <input type="file" name="zip"/>
         </FORM>
     * @param uploadFiles 上传对象
     */
    public static boolean post(UploadFile uploadFiles) throws Exception{     
        final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
        final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
        int fileDataLength = 0;
        String path=uploadFiles.getRequestUrl();
        Map<String, String> params=uploadFiles.getParamsMap();
        FormFile[] files=uploadFiles.getFiles();
        Handler handler=uploadFiles.getHandler();
      if(files!=null){
    	  for(FormFile uploadFile : files){//得到文件类型数据的总长度
              StringBuilder fileExplain = new StringBuilder();
               fileExplain.append("--");
               fileExplain.append(BOUNDARY);
               fileExplain.append("\r\n");
               fileExplain.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilename() + "\"\r\n");
               fileExplain.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
               fileExplain.append("\r\n");
               fileDataLength += fileExplain.length();
              if(uploadFile.getInStream()!=null){
                  fileDataLength += uploadFile.getFile().length();
               }else{
              	 byte[] data = uploadFile.getData();
              	 if(data!=null){
              		 fileDataLength += uploadFile.getData().length;
              	 }
               }
          } 
      }
        
        StringBuilder textEntity = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
            textEntity.append("--");
            textEntity.append(BOUNDARY);
            textEntity.append("\r\n");
            textEntity.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
            textEntity.append(entry.getValue());
            textEntity.append("\r\n");
        }
        //计算传输给服务器的实体数据总长度
        int dataLength = textEntity.toString().getBytes().length + fileDataLength +  endline.getBytes().length;
        if(path==null||path.equals("")){
        	return false;
        }
        URL url = new URL(path);
        int port = url.getPort()==-1 ? 80 : url.getPort();
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);    
        //socket.getSendBufferSize()
        OutputStream outStream = socket.getOutputStream();
       
        //下面完成HTTP请求头的发送
        String requestmethod = "POST "+ url.getPath()+" HTTP/1.1\r\n";
        outStream.write(requestmethod.getBytes());
        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
        outStream.write(accept.getBytes());
        String language = "Accept-Language: zh-CN\r\n";
        outStream.write(language.getBytes());
        String contenttype = "Content-Type: multipart/form-data; boundary="+ BOUNDARY+ "\r\n";
        outStream.write(contenttype.getBytes());
        String contentlength = "Content-Length: "+ dataLength + "\r\n";
        outStream.write(contentlength.getBytes());
        String alive = "Connection: Keep-Alive\r\n";
        outStream.write(alive.getBytes());
        String host = "Host: "+ url.getHost() +":"+ port +"\r\n";
        outStream.write(host.getBytes());
        //写完HTTP请求头后根据HTTP协议再写一个回车换行
        outStream.write("\r\n".getBytes());
        //把所有文本类型的实体数据发送出来
        outStream.write(textEntity.toString().getBytes());           
        //把所有文件类型的实体数据发送出来
        if(files!=null){
        	Message message=Message.obtain();
        	Bundle data = message.getData();
        	for(FormFile uploadFile : files){
        		StringBuilder fileEntity = new StringBuilder();
        		fileEntity.append("--");
        		fileEntity.append(BOUNDARY);
        		fileEntity.append("\r\n");
        		fileEntity.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilename() + "\"\r\n");
        		fileEntity.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
        		outStream.write(fileEntity.toString().getBytes());
        		if(uploadFile.getInStream()!=null){
        			byte[] buffer = new byte[1024];
        			int len = 0;
        			long uploadLeng=0L;
        			while((len = uploadFile.getInStream().read(buffer, 0, 1024))!=-1){
        				outStream.write(buffer, 0, len);
        				uploadLeng+=len;
        				if(handler!=null){
        					data.putLong(uploadFile.getFilename(), uploadLeng);
        					handler.sendMessage(message);
        				}
        			}
        			uploadFile.getInStream().close();
        		}else{
        			outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
        		}
        		outStream.write("\r\n".getBytes());
        	}
        }
        //下面发送数据结束标志，表示数据已经结束
        outStream.write(endline.getBytes());
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        if(reader.readLine().indexOf("200")==-1){//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
            return false;
        }
        outStream.flush();
        outStream.close();
        reader.close();
        socket.close();
        return true;
    }
}
