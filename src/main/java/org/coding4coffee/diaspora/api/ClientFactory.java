package org.coding4coffee.diaspora.api;

/**
 * @author Benjamin Neff
 */
public class ClientFactory {

	/**
	 * Creates a new Diaspora Client.
	 * 
	 * @param podUrl
	 *            the pod url
	 * @return the diaspora client
	 */
	public static DiasporaClient createDiasporaClient(final String podUrl) {
		return new DiasporaClientImpl(podUrl);
	}
}
