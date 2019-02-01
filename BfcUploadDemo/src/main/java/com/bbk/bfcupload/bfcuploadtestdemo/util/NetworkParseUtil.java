package com.bbk.bfcupload.bfcuploadtestdemo.util;

import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;

/**
 * Desc: 网络类型解析、获取、权限判断、添加、移除帮助类
 * Author: llp
 * Create Time: 2016年5月6日 下午10:32:53
 * Email: jacklulu29@gmail.com
 */
public class NetworkParseUtil {
	
	/**
	 * 是否可使用Wifi网络类型，默认支持
	 * 
	 * @param networkTypes 网络类型
	 * @return true支持，false不支持
	 */
	public static boolean containsWifi(int networkTypes){
		return (networkTypes & NetworkType.NETWORK_WIFI) != 0;
	}
	
	/**
	 * 是否可使用移动数据网络类型
	 * 
	 * @param networkTypes 网络类型
	 * @return true支持，false不支持
	 */
	public static boolean containsMobile(int networkTypes){
		return (networkTypes & NetworkType.NETWORK_MOBILE) != 0;
	}
	
	/**
	 * 是否可使用蓝牙网络类型
	 * 
	 * @param networkTypes 网络类型
	 * @return true支持，false不支持
	 */
	public static boolean containsBluetooth(int networkTypes){
		return (networkTypes & NetworkType.NETWORK_BLUETOOTH) != 0;
	}

}
