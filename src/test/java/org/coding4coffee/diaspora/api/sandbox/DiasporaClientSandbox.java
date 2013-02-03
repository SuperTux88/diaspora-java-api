package org.coding4coffee.diaspora.api.sandbox;

import org.coding4coffee.diaspora.api.ClientFactory;
import org.coding4coffee.diaspora.api.DiasporaClient;

public class DiasporaClientSandbox {

	public static void main(final String[] args) throws Exception {
		final DiasporaClient client = ClientFactory.createDiasporaClient("http://localhost:3000");

		System.out.println("login successful: " + client.login("api", "apitest"));

		System.out.println("aspects: " + client.getAspects());
	}
}
