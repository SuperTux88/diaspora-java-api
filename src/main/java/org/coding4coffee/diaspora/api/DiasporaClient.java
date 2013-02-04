package org.coding4coffee.diaspora.api;

import java.util.Map;

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

	/**
	 * Post.
	 * 
	 * @param text
	 *            the text
	 * @param aspects
	 *            the aspects
	 * @return the post guid
	 */
	String post(String text, String... aspects);

	/**
	 * Gets the aspects.
	 * 
	 * @return the aspects
	 */
	Map<String, String> getAspects();

}
