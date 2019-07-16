/**
 * 
 */
package com.alibaba.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class HttpClientUtil {

	private DefaultHttpClient httpClient = new DefaultHttpClient();
	private String url;
	private String userName;
	private String password;

	public static final int TYPE_POST = 0;
	public static final int TYPE_GET = 1;

	private static final Logger logger = Logger.getLogger(HttpClientUtil.class);

	public HttpClientUtil(String url) {
		this.url = url;
	}

	/**
	 * 代理方式访问
	 * 
	 * @param url
	 *            接口地址
	 * @param proxyHost
	 *            代理服务器地址
	 * @param proxyPort
	 *            代理服务端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 */
	@SuppressWarnings("deprecation")
	public HttpClientUtil(String url, String proxyHost, int proxyPort, String userName, String password) {
		this.url = url;
		this.userName = userName;
		this.password = password;
		HttpHost httpHost = new HttpHost(proxyHost, proxyPort);
		httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(this.userName, this.password));
	}

	/**
	 * 
	 * @param url
	 *            接口地址
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 */
	public HttpClientUtil(String url, String userName, String password) {
		this.url = url;
		this.userName = userName;
		this.password = password;
	}

	@SuppressWarnings("deprecation")
	public String doGet(Map<String, String> paramsMap) throws Exception {
		String resultStr = null;
		try {
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 5000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(httpClient.getParams(), 5000);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			if (paramsMap != null && paramsMap.size() > 0) {
				for (Map.Entry<String, String> e : paramsMap.entrySet()) {
					list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
				}
			}
			HttpGet httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(list, Consts.UTF_8));
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

	/**
	 * contentType:application/json类型post请求
	 * 
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public String doPost(Map<String, String> paramsMap) throws Exception {
		String resultStr = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity stringEntity = new StringEntity(JSONObject.toJSONString(paramsMap), Consts.UTF_8);
			stringEntity.setContentType("application/json");
			stringEntity.setContentEncoding("UTF-8");
			httpPost.setEntity(stringEntity);
			// 发送请求
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

	/**
	 * 表单 post提交
	 * 
	 * @param paramsMap
	 * @param headers
	 *            post请求的时候的头
	 * @return
	 * @throws Exception
	 */
	public String doPostByForm(Map<String, String> paramsMap) throws Exception {
		String resultStr = null;
		try {
			HttpResponse response = doPostByParamMap(paramsMap, null);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

	/**
	 * 表单 post提交
	 * 
	 * @param paramsMap
	 * @param headers
	 *            post请求的时候的头
	 * @return
	 * @throws Exception
	 */
	public String doPostByForm(Map<String, String> paramsMap, Header[] headers) throws Exception {
		String resultStr = null;
		try {
			HttpResponse response = doPostByParamMap(paramsMap, headers);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

	/**
	 * 表单 post提交
	 * 
	 * 返回带http状态码的结果
	 * 
	 * @param paramsMap
	 * @param headers
	 *            头信息设置
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> doRequestReturnMap(Map<String, String> paramsMap, int type, Header[] headers)
			throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			HttpResponse response = null;
			if (type == TYPE_POST) {
				response = doPostByParamMap(paramsMap, headers);
			} else if (type == TYPE_GET) {
				response = doGetByParamMap(paramsMap, headers);
			} else {
				throw new Exception("异常的请求type。");
			}
			HttpEntity entity = response.getEntity();
			resultMap.put("code", String.valueOf(response.getStatusLine().getStatusCode()));

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultMap.put("data", resultSb.toString());
				}
				entity.consumeContent();
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				logger.error("location:" + response.getHeaders("Location").toString());
				resultMap.put("reason", "302跳转");
			} else {
				resultMap.put("reason", response.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultMap;
	}

	private HttpResponse doPostByParamMap(Map<String, String> paramsMap, Header[] headers)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			httpPost.setHeaders(headers);
		}
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (paramsMap != null && paramsMap.size() > 0) {
			for (Map.Entry<String, String> e : paramsMap.entrySet()) {
				list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(entity);
		}
		// 发送请求
		HttpResponse response = httpClient.execute(httpPost);
		return response;
	}

	private HttpResponse doGetByParamMap(Map<String, String> paramsMap, Header[] headers)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (paramsMap != null && paramsMap.size() > 0) {
			for (Map.Entry<String, String> e : paramsMap.entrySet()) {
				list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
		}
		// 发送请求
		HttpGet httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(list, Consts.UTF_8));
		if (headers != null) {
			httpGet.setHeaders(headers);
		}
		HttpResponse response = httpClient.execute(httpGet);
		return response;
	}

	/**
	 * contentType:application/json类型post请求
	 * 
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public String doPost(JSONObject paramsMap) throws Exception {
		return doPost(null, paramsMap);
	}

	/**
	 * 发送post Json和header
	 * 
	 * @param headerMap
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public String doPost(Map<String, String> headerMap, JSONObject paramsMap) throws Exception {
		String resultStr = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity stringEntity = new StringEntity(paramsMap.toString(), Consts.UTF_8);
			stringEntity.setContentType("application/json");
			stringEntity.setContentEncoding("UTF-8");
			httpPost.setEntity(stringEntity);

			if (headerMap != null) {
				Iterator headerIterator = headerMap.entrySet().iterator(); // 循环增加header
				while (headerIterator.hasNext()) {
					Entry<String, String> elem = (Entry<String, String>) headerIterator.next();
					httpPost.addHeader(elem.getKey(), elem.getValue());
				}
			}

			// 发送请求
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

	@SuppressWarnings("deprecation")
	public String doGet(Map<String, String> paramsMap, Map<String, String> header) throws Exception {
		String resultStr = null;
		try {
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 5000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(httpClient.getParams(), 5000);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			if (paramsMap != null && paramsMap.size() > 0) {
				for (Map.Entry<String, String> e : paramsMap.entrySet()) {
					list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
				}
			}
			HttpGet httpGet = null;
			if (list.isEmpty()) {
				httpGet = new HttpGet(url);
			} else {
				httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(list, Consts.UTF_8));
			}
			for (String name : header.keySet()) {
				httpGet.setHeader(name, header.get(name));
			}

			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
				// 显示结果
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line = null;
				StringBuffer resultSb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					resultSb.append(line);
				}
				if (resultSb.length() > 0) {
					resultStr = resultSb.toString();
				}
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.close();
		}
		return resultStr;
	}

}
