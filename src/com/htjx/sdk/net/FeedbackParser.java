package com.htjx.sdk.net;

import android.util.SparseArray;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.htjx.sdk.domain.Response;
import com.htjx.sdk.utils.LogUtils;
/**
 * 普通回执行解析类
 * @author fada
 *
 */
public class FeedbackParser extends BaseParser<SparseArray<Response<String>>> {

	@Override
	public SparseArray<Response<String>> parseJSON(String str) {
		LogUtils.d("FeedbackParser=str="+str);
		SparseArray<Response<String>> sa=new SparseArray<Response<String>>();
		try {
			if (str == null) {
				LogUtils.d("网络请求失败");
				sa.put(-1, null);
				return sa;
			}
			TypeReference<Response<String>> typeRef = new TypeReference<Response<String>>() {};
			Response<String> response = JSON.parseObject(str, typeRef);
			LogUtils.d("FeedBack="+response);
			if (response == null) {
				sa.put(-1, null);
				return sa;
			}
			switch (response.getCode()) {
			case 0:// 请求成功
				sa.put(0, response);
				break;
			case -1:// 无数据
				sa.put(-1, null);
				break;
			case 304:// 请求失败
				sa.put(304,null);
				break;
			}
			return sa;
		} catch (Exception e) {
			e.printStackTrace();
			sa.put(-1, null);
			return sa;
		}
	}

}
