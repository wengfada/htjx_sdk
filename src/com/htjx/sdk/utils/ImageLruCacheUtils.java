package com.htjx.sdk.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.htjx.sdk.net.ThreadPoolManager;
/**
 * 利用Android原生的Lruchace的三级缓存
 * @author fada
 *
 */
public class ImageLruCacheUtils {
	private static final int HARD_CACHE_CAPACITY = 30;//方案1.以数量为基准
	/**
	 * maxMemory()获取当前应用可以从手机获得的最大内存.,totalMemory()表示当前应用已经获得的总内存.freeMemory():表示当前应用已获取的总内存中没有被消耗的部分
	 */
	private static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);//方案二.以容量为基准
	/**
	 * TODO 一级缓存 LruCache Android4.0及以上
	 */
	public final static LruCache<String, Bitmap> lruCache=new LruCache<String, Bitmap>(MAX_MEMORY/8){
		/**
		 * 这个方法计算新增一张图片的容量或数量,默认是数量1.
		 */
		protected int sizeOf(String key, Bitmap value) {
			  /**
			   * getRowBytes：Since API Level 1，用于计算位图每一行所占用的内存字节数。
			   * getByteCount：Since API Level 12，用于计算位图所占用的内存字节数。
			   * getByteCount() = getRowBytes() * getHeight().
			   * 也就是说位图所占用的内存空间数等于位图的每一行所占用的空间数乘以位图的行数。 重写此方法来衡量每张图片的大小，默认返回图片大小。 
			   */
			 //return bitmap.getByteCount() / 1024;   
			return (value.getRowBytes() * value.getHeight())/1024;
		};
		
		/**
		 * 集合调用get方法时,如果集合中没有找到对象会调用这个方法
		 */
		protected Bitmap create(String key) {
			 SoftReference<Bitmap> softReference = mSoftBitmapCache.get(key);
			 //看二级缓存是否存在
			 if(softReference!=null){
				 Bitmap bitmap = softReference.get();
				 //看软引用里面的bitmap对象有没有被回收
				 if(bitmap!=null){
					 return bitmap;
				 }
			 }
			return null;
		};
		/**
		 * 此方法在put时会调用.如果超出集合最大值则会调用这个方法
		 */
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			if(evicted){
				if(oldValue!=null)
				mSoftBitmapCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
	};

	/**
	 * TODO 第二级内存缓存,当mHardBitmapCache的key大于30的时候，会根据LRU算法把最近没有被使用的key放入到这个缓存中。
	 * Bitmap使用了SoftReference，当内存空间不足时，此cache中的bitmap会被垃圾回收掉
	 * ConcurrentHashMap是支持 高并发、高吞吐量的线程安全HashMap实现.
	 */
	public static Map<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);
	/**
	 * TODO 从内存缓存中获取图片
	 */
	public static Bitmap getBitmapFromCache(String imagePath) {
		return lruCache.get(imagePath);
	}
	/**
	 * 
	 * TODO @author 图片加载回调接口
	 * 
	 */
	public interface ImageCallback {
		public void loadImage(Bitmap bitmap, String imagePath);
	}
	/**
	 * 默认获取本地或者服务端加载图片
	 * @param context
	 * @param dir 本地缓存的目录
	 * @param imgUrl 图片Url
	 * @param callback 回调函数
	 * @param isCompress 图片是否要压缩
	 * @return Bitmap对象
	 */
	public static Bitmap loadImageDefault(Context context,File dir, String imagePath,
			ImageCallback callback,boolean isCompress) {
		try {
			if(dir == null ){
				dir=context.getCacheDir();
			}
			if (imagePath != null) {
				if (imagePath.startsWith("http://")) {//表示这个是网络
					String imageLocalPath = new File(dir, URLEncoder.encode(imagePath,"utf-8")).getAbsolutePath();
					return loadImage(context, imageLocalPath, imagePath, callback,isCompress);
				} else {//这个是本地图片
					String imageLocalPath =imagePath;
					return loadImage(context, imageLocalPath, null, callback,isCompress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方法描述：新:从本地或者服务端加载图片
	 * @param context 上下文
	 * @param imageLocalPath 本地路径
	 * @param imagePath 网络图片url
	 * @param callback  图片回调
	 * @param isCompress 是否要压缩
	 * @return Bitmap对象
	 */
	private static Bitmap loadImage(final Context context,final String imageLocalPath,final String imagePath, final ImageCallback callback,final boolean isCompress) {
		Bitmap bitmap = null;
		if (imageLocalPath != null)//从内存中高速加载
			bitmap = getBitmapFromCache(imageLocalPath);
		if (bitmap != null) {
			return bitmap;
		} else {// 从网上或本地缓存中加载
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						if (callback != null) {
							callback.loadImage(bitmap, imageLocalPath);
						}
					}
				}
			};
			Runnable runnable = new Runnable() {
			
				@Override
				public void run() {
					try {
						Message msg = handler.obtainMessage();
						//检查本地缓存是否存在
						File imageFile = new File(imageLocalPath);
						if (imageFile.exists()) {
							LogUtils.d("图片本地缓存加载url=:"+imageLocalPath);
							//从本地缓存中获取图片并存入常用集合中
							putImageToMap(imageLocalPath,isCompress);
							msg.obj = getBitmapFromCache(imageLocalPath);
							if(msg.obj!=null){
								handler.sendMessage(msg);
							}
						} else {//不存在则从网络获取
							if(imagePath!=null){
								Bitmap bitmap = null;
								SharedPreferences sp= context.getSharedPreferences("config",Context.MODE_PRIVATE);
								// 仅在wifi下加载
								if (sp.getBoolean("isWifiLoad", false)) {
									if (!NetUtil.isWifi(context)) {
										return;
									}
								}
								URL url = new URL(imagePath);
								LogUtils.d("图片从服务器加载url=:"+imagePath);
								URLConnection conn = null;
								conn = url.openConnection();
								conn.setConnectTimeout(5000);
								conn.setReadTimeout(5000);
								conn.connect();
								BufferedInputStream bis = new BufferedInputStream(
										conn.getInputStream(), 8192);
								if (bis != null) {
									bitmap = BitmapFactory.decodeStream(bis);
								}
								msg.what = 0;
								putBitmapToMap(imageLocalPath,bitmap);
								msg.obj = getBitmapFromCache(imageLocalPath);
								if(msg.obj!=null){
									handler.sendMessage(msg);
								}
								try {
									// 保存文件到sd卡
									saveToSD(imageLocalPath, bitmap);
								} catch (Exception e) {
									e.printStackTrace();
									Log.e(ImageUtil.class.getName(), "保存图片至SD卡出错！");
								}
							}

						}
					} catch (Exception e) {
						Log.e(ImageUtil.class.getName(), "网络请求图片出错！");
						e.printStackTrace();
					}
				}
			};
			//执行线程
			ThreadPoolManager.getInstance().addTask(runnable);
		}
		return null;
	}

	/**
	 * TODO 保存图片至sd卡
	 * @param fileName 文件名
	 * @param bitmap 
	 */
	public static synchronized void saveToSD(final String fileName,
			final Bitmap bitmap) {
		if (bitmap == null || fileName == null) {
			return;
		}
		Runnable runnable = new Runnable() {
			
			public void run() {
				FileOutputStream b = null;
				try {
					b = new FileOutputStream(fileName);

					bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		};
		ThreadPoolManager.getInstance().addTask(runnable);

	}

	
	
	/**
	 * 添加本地缓存进map集合,并与路径对应
	 * @param imagePath 图片路径
	 * @param isCompress 是否要压缩
	 */
	public static void putImageToMap(String imagePath,boolean isCompress) {
		Bitmap bitmap=null;
		if(isCompress){
			bitmap= getZoomBitmap(imagePath, 150f);
		}else{
			bitmap = getZoomBitmap(imagePath, 0f);
		}
		if (bitmap != null) {
			lruCache.put(imagePath, bitmap);
		}
	}
	/**
	 * 保存网络图片到map中
	 * @param imagePath 图片路径
	 * @param bitmap
	 */
	public static void putBitmapToMap(String imagePath,Bitmap bitmap) {
			if (bitmap != null) {
				lruCache.put(imagePath,bitmap);
			}
	}
	/**
	 * TODO 从本地加载图片,并缩放
	 * 
	 * @param imagePath
	 * @param scale 压缩比率 可以为空,那么是默认是200,这个值越大则 压缩比越小  如果压缩率是0,则表示不压缩
	 * @return 一个缩放好的bitmap
	 */
	public static Bitmap getZoomBitmap(String imagePath, Float scale) {
		if (!new File(imagePath).exists())
			return null;
		if (scale == null){
			scale = 200f;
		}
		Bitmap bm=null;
		if(scale>0){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bm = BitmapFactory.decodeFile(imagePath, options);
			int be = (int) (options.outHeight / (float) scale);
			if (be <= 1) {//如果没有超过我们的临界值大小
				be = 1;//按原大小加载,不压缩
			}
			options.inSampleSize = be;// be=2.表示压缩为原来的1/2,以此类推
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(imagePath, options);
		}else{
			bm = BitmapFactory.decodeFile(imagePath);
		}
		return bm;
	}
}
