package org.coding4coffee.diaspora.api;

import java.util.Collection;
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
	 * @param aspect
	 *            the aspect: public, all_aspects or ID
	 * @return the post guid (null, if not successful)
	 */
	String post(String text, final String aspect);

	/**
	 * Post.
	 * 
	 * @param text
	 *            the text
	 * @param aspects
	 *            the aspects: list with IDs
	 * @return the post guid (null, if not successful)
	 */
	String post(String text, final Collection<String> aspects);

	/**
	 * Gets the aspects.
	 * 
	 * @return the aspects:<br>
	 *         <b>key</b>: ID<br>
	 *         <b>value</b>: name
	 */
	Map<String, String> getAspects();

}
