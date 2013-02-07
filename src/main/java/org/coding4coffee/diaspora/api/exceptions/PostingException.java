package org.coding4coffee.diaspora.api.exceptions;

/**
 * @author Benjamin Neff
 */
public class PostingException extends PodFailureException {

	private static final long serialVersionUID = 1L;

	public PostingException(final String message) {
		super(message);
	}
}
