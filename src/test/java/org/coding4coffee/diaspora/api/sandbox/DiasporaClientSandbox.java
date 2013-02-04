package org.coding4coffee.diaspora.api.sandbox;

import java.util.Arrays;

import org.coding4coffee.diaspora.api.AspectConstants;
import org.coding4coffee.diaspora.api.ClientFactory;
import org.coding4coffee.diaspora.api.DiasporaClient;

public class DiasporaClientSandbox {

	public static void main(final String[] args) throws Exception {
		final DiasporaClient client = ClientFactory.createDiasporaClient("http://localhost:3000");

		System.out.println("aspects: " + client.getAspects());
		System.out.println("post id: " + client.post("test", "public"));

		System.out.println("login successful: " + client.login("api", "apitest"));

		System.out.println("aspects: " + client.getAspects());
		System.out.println("post id: " + client.post("test 123", AspectConstants.PUBLIC));
		System.out.println("post id: " + client.post("test 1234", AspectConstants.ALL_ASPECTS));
		System.out.println("post id: " + client.post("test 12345", Arrays.asList(new String[] { "21", "22" })));
	}
}
