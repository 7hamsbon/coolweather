package com.coolweather.app.util;

public interface HttpCallbackListener
{
	/**
	 * 请求发送成功时回调
	 */
	void onFinished(String response);
	/**
	 * 请求发送失败时回调
	 */
	void onError(Exception e);
}
