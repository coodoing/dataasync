package com.dataasync;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpHandler {

	public static HttpClient createHttpClient() {
		DefaultHttpClient httpClient = createDefaultHttpClient(false, false);
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");
		return httpClient;
	}

	public static DefaultHttpClient createDefaultHttpClient(boolean threadSafe,
			boolean enableRedirect) {
		HttpParams httpParams = new BasicHttpParams();
		HttpClientParams.setRedirecting(httpParams, enableRedirect);
		setTimeout(httpParams, Constants.HTTP_REQUEST_TIME_OUT * 1000);

		if (threadSafe) {
			SchemeRegistry supportedSchemes = new SchemeRegistry();
			SocketFactory sf = PlainSocketFactory.getSocketFactory();
			supportedSchemes.register(new Scheme("http", sf, 80));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					httpParams, supportedSchemes);
			return new DefaultHttpClient(ccm, httpParams);
		} else {
			return new DefaultHttpClient(httpParams);
		}
	}

	public static String postForString(String url,
			Map<String, String> httpParams) {
		HttpResponse response = post(url, httpParams, null);
		return getResponseString(response);
	}

	/**
	 * @param url
	 * @param params
	 * @param customHttpPost
	 * @return response
	 */
	public static HttpResponse post(String url, Map<String, String> params,
			HttpPost customHttpPost) {
		if (url == null) {
			return null;
		}

		HttpPost httpPost;
		if (customHttpPost == null) {
			httpPost = createHttpPost(url);
		} else {
			httpPost = customHttpPost;
			URI uri = null;
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			httpPost.setURI(uri);
		}

		if (params != null && params.size() > 0) {
		}

		HttpPost httpPostWithCookie = httpPost;
		httpPostWithCookie.addHeader("Accept-Encoding", "gzip, deflate");

		Map<String, String> newHttpParams = new HashMap<String, String>();

		List<NameValuePair> ps = getParamsList(newHttpParams);
		if (ps != null) {
			UrlEncodedFormEntity entity;
			try {
				entity = new UrlEncodedFormEntity(ps, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
			httpPostWithCookie.setEntity(entity);
		}
		return getHttpResponse(createHttpClient(), httpPostWithCookie);
	}

	public static String getForString(String url) {
		HttpResponse response = get(url, null, null);
		return getResponseString(response);
	}

	/**
	 * @param url
	 * @param params
	 * @param headers
	 */
	public static HttpResponse get(String url, Map<String, String> params,
			Map<String, String> headers) {
		if (url == null || url.equals("")) {
			return null;
		}

		Map<String, String> newHttpParams = new HashMap<String, String>();
		List<NameValuePair> ps = getParamsList(newHttpParams);
		String separator;
		if (url.contains("?")) { 
			separator = "&";
		} else {
			separator = "?";
		}
		url += separator + URLEncodedUtils.format(ps, "utf-8");

		HttpGet httpGet = createHttpGet(url);
		HttpUriRequest requestWithCookie = httpGet;
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				requestWithCookie.addHeader(header.getKey(), header.getValue());
			}
		}
		requestWithCookie.addHeader("Accept-Encoding", "gzip, deflate");
		return getHttpResponse(createHttpClient(), requestWithCookie);
	}

	public static String getResponseString(HttpResponse response) {
		if (response == null) {
			return null;
		}

		try {
			HttpEntity entity = response.getEntity();
			Header contentEncoding = entity.getContentEncoding();
			if (contentEncoding != null) {
				if (contentEncoding.getValue().toLowerCase(Locale.getDefault())
						.contains("gzip")) {
					GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(
							entity);
					return EntityUtils.toString(gzipEntity);
				}
			}
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param paramsMap
	 * @return NameValuePair
	 */
	public static Bitmap getImageFromNetworkByHttpURLConnection(String url,
			Map<String, String> params) throws Exception {
		Bitmap bmp = null;
		InputStream inputStream = getImageStreamByURLConnection(url);
		bmp = BitmapFactory.decodeStream(inputStream);
		return bmp;
	}

	/**
	 * @param paramsMap
	 * @return NameValuePair
	 */
	@SuppressWarnings("unused")
	public static Bitmap getImageFromNetworkByHttpClient(String url,
			Map<String, String> params) throws Exception {
		HttpGet httpGet = createHttpGet(url);
		HttpClient httpClient = createHttpClient();
		InputStream is = null;
		ByteArrayOutputStream out = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			long length = httpEntity.getContentLength();
			is = httpEntity.getContent();
			if (is != null) {
				out = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int read = -1;
				int count = 0;
				while ((read = is.read(buf)) != -1) {
					out.write(buf, 0, read);
					count += read;
				}
				byte[] data = out.toByteArray();
				Bitmap bmp = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				return bmp;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static void setTimeout(HttpParams params, int timeout) {
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
	}

	private static HttpPost createHttpPost(String url) {
		try {
			return new HttpPost(url);
		} catch (IllegalArgumentException e) { // not a valid url
			return null;
		}
	}

	private static HttpGet createHttpGet(String url) {
		try {
			return new HttpGet(url);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static HttpResponse getHttpResponse(HttpClient httpClient,
			HttpUriRequest request) {
		if (request == null || request.getURI() == null) {
			return null;
		}

		try {
			httpClient.getConnectionManager().closeExpiredConnections();
			return httpClient.execute(request);
		} catch (IOException e) {
			request.abort();
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * convert the key/value to NameValuePair
	 * 
	 * @param paramsMap
	 * @return NameValuePair
	 */
	private static List<NameValuePair> getParamsList(
			Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}
		return params;
	}

	/**
	 * @param path
	 * @return byte[]
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static byte[] getImageByURLConnection(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	/**
	 * @param path
	 * @return InputStream
	 * @throws Exception
	 */
	private static InputStream getImageStreamByURLConnection(String path)
			throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}
}
