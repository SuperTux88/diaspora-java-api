package org.coding4coffee.diaspora.api.exceptions;

/**
 * @author Benjamin Neff
 */
public class CsrfTokenNotFoundException extends PodFailureException {

	private static final long serialVersionUID = 1L;

	public CsrfTokenNotFoundException(final String message) {
		super(message);
	}
}
