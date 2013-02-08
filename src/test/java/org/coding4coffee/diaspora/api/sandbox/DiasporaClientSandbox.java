package org.coding4coffee.diaspora.api.sandbox;

import java.io.FileInputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.coding4coffee.diaspora.api.AspectConstants;
import org.coding4coffee.diaspora.api.ClientFactory;
import org.coding4coffee.diaspora.api.DiasporaClient;
import org.coding4coffee.diaspora.api.upload.ProgressListener;

public class DiasporaClientSandbox {

	public static void main(final String[] args) throws Exception {
		final DiasporaClient client = ClientFactory.createDiasporaClient("http://localhost:3000");

		try {
			System.out.println("aspects: " + client.getAspects());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("post id: " + client.createPost("test", "public"));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println("login successful: " + client.login("api", "wrongPW"));
		System.out.println("login successful: " + client.login("api", "apitest"));

		System.out.println("aspects: " + client.getAspects());

		final ProgressListener listener = new ProgressListener() {

			@Override
			public void transferred(final long bytes) {
				System.out.print(bytes + "|");
			}
		};

		System.out.println("photo: "
				+ client.uploadPhoto(IOUtils.toByteArray(new FileInputStream("/home/benjamin/upload.jpg")), listener));

		System.out.println("post id: " + client.createPost("test 123", AspectConstants.PUBLIC));
		System.out.println("post id: " + client.createPost("test 1234", AspectConstants.ALL_ASPECTS));
		System.out.println("post id: " + client.createPost("test 12345", Arrays.asList(new String[] { "21", "22" })));
	}
}
