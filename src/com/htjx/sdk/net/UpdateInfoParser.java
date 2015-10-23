package com.htjx.sdk.net;

import java.util.List;

import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.htjx.sdk.domain.Response;
import com.htjx.sdk.domain.UpdateInfo;
import com.htjx.sdk.utils.LogUtils;
/**
 * 自动更新 解析类
 * @author fada
 *
 */
public class UpdateInfoParser extends BaseParser<SparseArray<UpdateInfo>>{

	@Override
	public SparseArray<UpdateInfo> parseJSON(String str) {
		SparseArray<UpdateInfo> sa=new SparseArray<UpdateInfo>();
		try {
			if (str == null) {
				LogUtils.d("网络请求失败");
				sa.put(-1, null);
				return sa;
			}
			TypeReference<Response<UpdateInfo>> typeRef = new TypeReference<Response<UpdateInfo>>() {};
			Response<UpdateInfo> response = JSON.parseObject(str, typeRef);
			LogUtils.d("respose="+response);
			if (response == null) {
				sa.put(-1, null);
				return sa;
			}
			switch (response.getCode()) {
			case 0:// 请求成功
				if (response != null) {
					TypeReference<List<UpdateInfo>> typeJokeList = new TypeReference<List<UpdateInfo>>() {};
					List<UpdateInfo> softlist =JSON.parseObject(response.getResult().toString(), typeJokeList);
					sa.put(0, softlist.get(0));
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
