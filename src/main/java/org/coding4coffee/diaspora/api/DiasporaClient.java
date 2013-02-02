package org.coding4coffee.diaspora.api;

/**
 * @author Benjamin Neff
 */
public interface DiasporaClient {

	/**
	 * Login with username and password.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return true, if successful
	 */
	boolean login(String username, String password);

}
