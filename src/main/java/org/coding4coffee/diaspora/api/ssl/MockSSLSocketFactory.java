package org.coding4coffee.diaspora.api.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * @author Benjamin Neff
 */
public class MockSSLSocketFactory extends SSLSocketFactory {

	public MockSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
		super(getMockSSLContext());
	}

	private static SSLContext getMockSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		final SSLContext sslContext = SSLContext.getInstance("SSL");

		// set up a TrustManager that trusts everything
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// ignore check
				return null;
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
				// ignore check
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
				// ignore check
			}
		} }, new SecureRandom());
		return sslContext;
	}
}
