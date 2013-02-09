package org.coding4coffee.diaspora.api;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.coding4coffee.diaspora.api.ssl.MockSSLSocketFactory;

/**
 * @author Benjamin Neff
 */
public class DiasporaClientFactory {

	/**
	 * Creates a new Diaspora Client.
	 * 
	 * @param podUrl
	 *            the pod url
	 * @return the diaspora client
	 */
	public static DiasporaClient createDiasporaClient(final String podUrl) {
		return createDiasporaClient(podUrl, false);
	}

	/**
	 * Creates a new Diaspora Client.
	 * 
	 * @param podUrl
	 *            the pod url
	 * @param ignoreSSL
	 *            ignore ssl certificates (does not work on android!)
	 * @return the diaspora client
	 */
	public static DiasporaClient createDiasporaClient(final String podUrl, final boolean ignoreSSL) {
		if (ignoreSSL) {
			final SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			try {
				schemeRegistry.register(new Scheme("https", new MockSSLSocketFactory(), 443));
			} catch (final Exception e) {
				throw new IllegalStateException("could not create ssl socket factory", e);
			}

			final ClientConnectionManager cm = new SingleClientConnManager(null, schemeRegistry);

			return new DiasporaClientImpl(podUrl, cm);
		}

		return new DiasporaClientImpl(podUrl, null);
	}
}
