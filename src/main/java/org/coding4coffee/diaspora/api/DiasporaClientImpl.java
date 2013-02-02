package org.coding4coffee.diaspora.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Benjamin Neff
 */
public class DiasporaClientImpl implements DiasporaClient {

	private final String podUrl;

	private final HttpClient session;

	DiasporaClientImpl(final String podUrl) {
		this.podUrl = podUrl;

		session = new DefaultHttpClient();
	}

	@Override
	public boolean login(final String username, final String password) {
		final HttpPost signInRequest = new HttpPost(podUrl + "/users/sign_in");

		try {
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user[username]", username));
			nameValuePairs.add(new BasicNameValuePair("user[password]", password));
			nameValuePairs.add(new BasicNameValuePair("user[remember_me]", "1"));
			signInRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			final HttpResponse response = session.execute(signInRequest);

			return response.getStatusLine().getStatusCode() == 302;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
