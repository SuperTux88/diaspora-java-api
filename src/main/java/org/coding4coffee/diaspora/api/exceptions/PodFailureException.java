package org.coding4coffee.diaspora.api.exceptions;

/**
 * @author Benjamin Neff
 */
public class PodFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public PodFailureException(final Throwable t) {
		super(t);
	}

	public PodFailureException(final String message) {
		super(message);
	}
}
