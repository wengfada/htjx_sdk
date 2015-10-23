package com.htjx.sdk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
/**
 * 文件工具类
 * @author fada
 *
 */
public class FileUtils {
	/**
	 * 复制文件
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
 /**
  *  复制文件通过流
  * @param inBuff
  * @param targetFile
  * @throws IOException
  */
    public static void copyFileByIO(InputStream inBuff, File targetFile) throws IOException {
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    /**
     * 资产目录复制入项目filesDir目录
     * @param context 上下文
     * @param name 文件名
     */
    public static void copyAssetsToFilesDir(Context context,String name) {
		InputStream myInput = null;  
		try {
			myInput = context.getAssets().open(name);  
			File dataFolder = context.getFilesDir();
			File jar = new File(dataFolder.getAbsolutePath() + "/"+name);
			FileUtils.copyFileByIO(myInput, jar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     *  复制文件夹
     * @param sourceDir
     * @param targetDir
     * @throws IOException
     */
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
    /**
     * 删除一个目录下的所有文件
     * @param filepath
     * @throws IOException
     */
    public static void del(String filepath) throws IOException {
        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                f.delete();
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
            }
        }
    }
    /**
     * 文件转成字节数组
     * 
     * @param path
     * @return byte[]
     * @throws IOException
     */
    public static byte[] readFileToBytes(String path) throws IOException {
        byte[] b = null;
        InputStream is = null;
        File f = new File(path);
        try {
            is = new FileInputStream(f);
            b = new byte[(int) f.length()];
            is.read(b);
        } finally {
            if (is != null)
                is.close();
        }
        return b;
    }

    /**
     * 将byte写入文件中
     * 
     * @param fileByte
     * @param filePath
     * @throws IOException
     */
    public static void byteToFile(byte[] fileByte, String filePath) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath));
            os.write(fileByte);
            os.flush();
        } finally {
            if (os != null)
                os.close();
        }
    }

    
    /**
     * 判空字串
     * 
     * @param str
     * @return 为空true
     */
    public static boolean strIsNull(String str) {
        return str == null || str.equals("");
    }

    /**
     * 折分数组
     * 
     * @param ary
     * @param subSize
     * @return 数组
     */
    public static List<List<Object>> splitAry(Object[] ary, int subSize) {
        int count = ary.length % subSize == 0 ? ary.length / subSize : ary.length / subSize + 1;

        List<List<Object>> subAryList = new ArrayList<List<Object>>();

        for (int i = 0; i < count; i++) {
            int index = i * subSize;

            List<Object> list = new ArrayList<Object>();
            int j = 0;
            while (j < subSize && index < ary.length) {
                list.add(ary[index++]);
                j++;
            }

            subAryList.add(list);
        }

        return subAryList;
    }

    /**
     * 数据转字符串
     * @param mobile 数据
     * @return 转换好的字符串
     */
    public static String ArrayToString(Object[] mobile) {
        String destId = "";
        for (Object phone : mobile) {
            destId += " " + (String) phone;
        }
        return destId.trim();
    }

}
