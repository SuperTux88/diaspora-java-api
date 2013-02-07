package org.coding4coffee.diaspora.api.exceptions;

public class NotLoggedInException extends PodFailureException {

	private static final long serialVersionUID = 1L;

	public NotLoggedInException(final String message) {
		super(message);
	}
}
