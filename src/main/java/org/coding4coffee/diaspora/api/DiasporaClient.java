package org.coding4coffee.diaspora.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.coding4coffee.diaspora.api.exceptions.PodFailureException;
import org.coding4coffee.diaspora.api.upload.ProgressListener;

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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. (Network error)
	 */
	boolean login(String username, String password) throws IOException;

	/**
	 * Create a new post.
	 * 
	 * @param text
	 *            the text
	 * @param aspect
	 *            the aspect: "public", "all_aspects" or ID
	 * @return the post guid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. (Network error)
	 * @throws PodFailureException
	 *             Errors while parsing the response from the pod
	 */
	String createPost(String text, final String aspect) throws IOException, PodFailureException;

	/**
	 * Create a new post.
	 * 
	 * @param text
	 *            the text
	 * @param aspects
	 *            the aspects: list with IDs
	 * @return the post guid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. (Network error)
	 * @throws PodFailureException
	 *             Errors while parsing the response from the pod
	 */
	String createPost(String text, final Collection<String> aspects) throws IOException, PodFailureException;

	/**
	 * Upload photo with progress listener.
	 * 
	 * @param photoBytes
	 *            the photo byte array
	 * @param listener
	 *            the progress listener
	 * @return the photo guid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. (Network error)
	 * @throws PodFailureException
	 *             Errors while parsing the response from the pod
	 */
	String uploadPhoto(byte[] photoBytes, ProgressListener listener) throws IOException, PodFailureException;

	/**
	 * Gets the aspects.
	 * 
	 * @return the aspects:<br>
	 *         <b>key</b>: ID<br>
	 *         <b>value</b>: name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred. (Network error)
	 * @throws PodFailureException
	 *             Errors while parsing the response from the pod
	 */
	Map<String, String> getAspects() throws IOException, PodFailureException;

}
