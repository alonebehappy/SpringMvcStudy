package com.alibaba.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class LoginWithHttpclient {
	private static CookieStore cookieStore = null;

	/**
	 * 组装登录参数
	 * 
	 * @return
	 */
	public static List<NameValuePair> getLoginNameValuePairList() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		return params;
	}

	/**
	 * 组装操作参数
	 * 
	 * @return
	 */
	public static List<NameValuePair> getQueryNameValuePairList() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CurrentPageIndex", "0"));
		/*params.add(new BasicNameValuePair("FederationEnabled", "false"));
		params.add(new BasicNameValuePair("LimitationIds", ""));
		params.add(new BasicNameValuePair("OrderByClause", ""));*/
		params.add(new BasicNameValuePair("PageSize", "999"));
	/*	params.add(new BasicNameValuePair("RelatedNodeEntityUri", ""));
		params.add(new BasicNameValuePair("RelatedNodeId", "0"));
		params.add(new BasicNameValuePair("ShowAcknowledgedAlerts", "True"));
		params.add(new BasicNameValuePair("TriggeringObjectEntityNames", ""));
		params.add(new BasicNameValuePair("TriggeringObjectEntityUris", ""));*/
		return params;
	}

	public static String doGet(String getUrl, List<NameValuePair> parameterList) {
		String retStr = "";
		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = null;
		CookieStore cookieStoreTemp = new BasicCookieStore();
		closeableHttpClient = httpClientBuilder.setDefaultCookieStore(cookieStoreTemp).build();

		HttpGet httpGet = new HttpGet(getUrl);
		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpGet.setConfig(requestConfig);
		try {
			CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
			print(cookieStoreTemp);
			cookieStore = cookieStoreTemp;

			HttpEntity httpEntity = response.getEntity();
			retStr = EntityUtils.toString(httpEntity, "UTF-8");
			// 释放资源
			closeableHttpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR:" + e);
		}
		return retStr;
	}

	public static String doPost(String postUrl, List<NameValuePair> parameterList) {
		if (cookieStore == null) {

			String loginUrl = "http://10.116.218.79/Orion/Login.aspx?autologin=no&__EVENTTARGET=ctl00%24BodyContent%24ctl04&__EVENTARGUMENT=&__VIEWSTATE=rP5%2FeXeaxTUA3ZFr4bFc1vJM4aiB%2Bd9x2LSBBo1BbGuHS1PYpy0TOJBNrjoyS2R%2BsUx%2BiF9y14Jc%2BdGFq464AuVgyjdgC%2FYdFP81pXc2SrmFbJLwZfHSVEqDo03A37ieNQe3UdvlgPBNfUJXcgO0J627EnYNgX1T%2FVkridngfa4bCppsLoxENeMT3DvHjr3o0kJmevdMXKB3MLZ5sRJC5wqpdm2%2Fp6cW0oRO7zbJAcrHu3%2FeZL4B4tdthLyzEGZnkOLh3ffRi8n3M2jVyJpoDL7XHaRUbTMU2dKOx1aY551rVq2c9E%2BuMM6l1yXxVf7H0BSGU0OKNwVwRzVvukUSigI6xajOkeEwDSeVFYj1Q4cM%2BGxNXSQiBTGPIrENn4up&__VIEWSTATEGENERATOR=01070692&ctl00%24BodyContent%24Username=autouser&ctl00%24BodyContent%24Password=Qianqian1963";
			System.out.println("diyici");
			// 第一次登录会保存cookie
			doGet(loginUrl, getLoginNameValuePairList());
		}
		String retStr = "";
		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = null;
		closeableHttpClient = httpClientBuilder.setDefaultCookieStore(cookieStore).build();

		HttpPost httpPost = new HttpPost(postUrl);
		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpPost.setConfig(requestConfig);
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameterList, "UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
			// setCookieStore(response);

			HttpEntity httpEntity = response.getEntity();
			retStr = EntityUtils.toString(httpEntity, "UTF-8");
			System.out.println(retStr);
			// 释放资源
			closeableHttpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR:" + e);
		}
		return retStr;
	}

	public static void main(String[] args) {

		String queryReginUrl = "http://10.116.218.79/api/ActiveAlertsOnThisEntity/GetActiveAlerts";
		// 第二次操作会调用已经存在的cookie
		doPost(queryReginUrl, getQueryNameValuePairList());
		
		doPost(queryReginUrl, getQueryNameValuePairList());

	}

	private static void print(CookieStore cookieStore) {
		for (Cookie c : cookieStore.getCookies()) {
			System.out.println(c.getName() + "----" + c.getValue());
		}
	}
}
