package org.coding4coffee.diaspora.api.exceptions;

/**
 * @author Benjamin Neff
 */
public class AspectsNotFoundException extends PodFailureException {

	private static final long serialVersionUID = 1L;

	public AspectsNotFoundException(final Throwable t) {
		super(t);
	}

	public AspectsNotFoundException(final String message) {
		super(message);
	}
}
