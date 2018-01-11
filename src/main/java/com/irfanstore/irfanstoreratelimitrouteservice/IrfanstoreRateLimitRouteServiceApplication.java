package com.irfanstore.irfanstoreratelimitrouteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class IrfanstoreRateLimitRouteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IrfanstoreRateLimitRouteServiceApplication.class, args);
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate< String, Object > redisTemplate() {

		final RedisTemplate< String, Object > template =  new RedisTemplate< String,Object >();
		template.setConnectionFactory( redisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		template.setHashValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
		template.setValueSerializer(new GenericToStringSerializer< Object >( Object.class ) );

		template.afterPropertiesSet();

		return template;
	}


	@Bean
	RestOperations restOperations() {
		RestTemplate restTemplate = new RestTemplate(new TrustEverythingClientHttpRequestFactory());
		restTemplate.setErrorHandler(new NoErrorsResponseErrorHandler());
		return restTemplate;
	}

	private static final class NoErrorsResponseErrorHandler extends DefaultResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

	}

	private static final class TrustEverythingClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

		@Override
		protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
			HttpURLConnection connection = super.openConnection(url, proxy);

			if (connection instanceof HttpsURLConnection) {
				HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;

				httpsConnection.setSSLSocketFactory(getSslContext(new TrustEverythingTrustManager()).getSocketFactory());
				httpsConnection.setHostnameVerifier(new TrustEverythingHostNameVerifier());
			}

			return connection;
		}

		private static SSLContext getSslContext(TrustManager trustManager) {
			try {
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[]{trustManager}, null);
				return sslContext;
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				throw new RuntimeException(e);

			}

		}
	}

	private static final class TrustEverythingHostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}

	}

	private static final class TrustEverythingTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

	}
}
