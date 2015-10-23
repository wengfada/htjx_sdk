package com.htjx.sdk.net;

import java.util.List;

import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.htjx.sdk.domain.Push;
import com.htjx.sdk.domain.Response;
import com.htjx.sdk.utils.LogUtils;
/**
 * 推送解析类  解析类
 * @author fada
 *
 */
public class PushParser extends BaseParser<SparseArray<Push>>{

	@Override
	public SparseArray<Push> parseJSON(String str) {
		SparseArray<Push> sa=new SparseArray<Push>();
		try {
			if (str == null) {
				LogUtils.d("网络请求失败");
				sa.put(-1, null);
				return sa;
			}
			TypeReference<Response<Push>> typeRef = new TypeReference<Response<Push>>() {};
			Response<Push> response = JSON.parseObject(str, typeRef);
			LogUtils.d("respose="+response);
			if (response == null) {
				sa.put(-1, null);
				return sa;
			}
			switch (response.getCode()) {
			case 0:// 请求成功
				if (response != null) {
					if(response.getResult()!=null){
						TypeReference<List<Push>> typeJokeList = new TypeReference<List<Push>>() {};
						List<Push> softlist =JSON.parseObject(response.getResult().toString(), typeJokeList);
						sa.put(0, softlist.get(0));
					}else{
						sa.put(-1, null);
					}
				}
				break;
			case -1:// 无数据
				sa.put(-1, null);
				break;
			case 304:// 请求失败
				sa.put(304, null);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			sa.put(-1, null);
			return sa;
		}
		return sa;
	}

}
