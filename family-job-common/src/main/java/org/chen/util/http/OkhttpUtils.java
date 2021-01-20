package org.chen.util.http;

import org.chen.constant.GlobeConstant;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * 获取okhttp实例  全局一个就好
 *
 * @author YuChen
 * @date 2020/1/8 9:53
 **/

@Slf4j
public class OkhttpUtils {

	/**
	 * 获取忽略证书验证无代理的客户端
	 *
	 * @param
	 * @return
	 * @author YuChen
	 * @date 2020/7/27 16:12
	 */
	public static OkHttpClient getHttpClient() {
		return OkhttpClientGetter.httpClient;
	}

	/**
	 * 获取配置了代理服务器的http客户端  懒加载
	 *
	 * @param
	 * @return
	 * @author YuChen
	 * @date 2020/7/27 16:14
	 */
	public static OkHttpClient getOkhttpClientWithProxy() {
		return OkhttpClientWithProxyGetter.httpClientWithProxy;
	}


	private static class OkhttpClientGetter{
		/**
		 * okhttp实例  全局一个就好  懒加载
		 */
		private static OkHttpClient httpClient;

		static {
			try {
				// Create a trust manager that does not validate certificate chains
				final TrustManager[] trustAllCerts = new TrustManager[]{
						new X509TrustManager() {
							@Override
							public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
							}

							@Override
							public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
							}

							@Override
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return new java.security.cert.X509Certificate[]{};
							}
						}
				};

				// Install the all-trusting trust manager
				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				// Create an ssl socket factory with our all-trusting manager
				final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


				httpClient = new OkHttpClient.Builder()
						// 连接池  都是从本地发送的请求 可以复用 回收时间为5分钟   查了一下  cpu有16个  这里配置16个
						.connectionPool(new ConnectionPool(16, 5, TimeUnit.MINUTES))
						// 用curl测试  该接口访问还行  设10S超时
						.connectTimeout(10, TimeUnit.SECONDS)
						// 跳过证书验证
						.sslSocketFactory(sslSocketFactory)
						.hostnameVerifier(new HostnameVerifier() {
							@Override
							public boolean verify(String hostname, SSLSession session) {
								return true;
							}
						})
						.retryOnConnectionFailure(true)
						.build();
			} catch (Exception e) {
				log.error("初始化http客户端失败", e);
			}
		}
	}


	private static class OkhttpClientWithProxyGetter {
		private static OkHttpClient httpClientWithProxy;

		static {
			try {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(GlobeConstant.PROXY_HOST, GlobeConstant.PROXY_PORT));
				// Create a trust manager that does not validate certificate chains
				final TrustManager[] trustAllCerts = new TrustManager[]{
						new X509TrustManager() {
							@Override
							public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
							}

							@Override
							public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
							}

							@Override
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return new java.security.cert.X509Certificate[]{};
							}
						}
				};

				// Install the all-trusting trust manager
				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				// Create an ssl socket factory with our all-trusting manager
				final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


				httpClientWithProxy = new OkHttpClient.Builder()
						// 连接池  都是从本地发送的请求 可以复用 回收时间为5分钟   查了一下  cpu有16个  这里配置16个
						.connectionPool(new ConnectionPool(16, 5, TimeUnit.MINUTES))
						// 用curl测试  该接口访问还行  设10S超时
						.connectTimeout(10, TimeUnit.SECONDS)
						// 跳过证书验证
						.sslSocketFactory(sslSocketFactory)
						.hostnameVerifier(new HostnameVerifier() {
							@Override
							public boolean verify(String hostname, SSLSession session) {
								return true;
							}
						})
						.retryOnConnectionFailure(true)
						.proxy(proxy)
						.build();
			} catch (Exception e) {
				log.error("初始化http客户端(代理模式)失败", e);
			}
		}
	}



}
