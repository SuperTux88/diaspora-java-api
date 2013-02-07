package org.coding4coffee.diaspora.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Benjamin Neff
 */
public class DiasporaClientImpl implements DiasporaClient {

	private static final Pattern CSRF_TOKEN_REGEX = Pattern.compile("content=\"([^\"]*)\"");

	private final String podUrl;

	private final HttpClient session;

	DiasporaClientImpl(final String podUrl, final ClientConnectionManager cm) {
		this.podUrl = podUrl;

		final HttpParams httpParams = new BasicHttpParams();
		HttpClientParams.setRedirecting(httpParams, false);
		session = new DefaultHttpClient(cm, httpParams);
	}

	@Override
	public boolean login(final String username, final String password) {
		final HttpPost signInRequest = new HttpPost(podUrl + "/users/sign_in");

		try {
			// add parameters
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user[username]", username));
			nameValuePairs.add(new BasicNameValuePair("user[password]", password));
			nameValuePairs.add(new BasicNameValuePair("user[remember_me]", "1"));
			signInRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// send request
			final HttpResponse response = session.execute(signInRequest);
			// ignore content
			response.getEntity().consumeContent();

			// successful if redirect to startpage
			return response.getStatusLine().getStatusCode() == 302;
		} catch (final Exception e) {
			// reset http connection
			signInRequest.abort();
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String post(final String text, final String aspect) {
		return post(text, Arrays.asList(new String[] { aspect }));
	}

	@Override
	public String post(final String text, final Collection<String> aspects) {
		final HttpPost postRequest = new HttpPost(podUrl + "/status_messages");

		try {
			// add header
			postRequest.addHeader("content-type", "application/json");
			postRequest.addHeader("accept", "application/json");
			postRequest.addHeader("X-CSRF-Token", getCsrfToken());

			// build json with post data
			final JSONObject post = new JSONObject();
			post.put("status_message", new JSONObject().put("text", text));
			post.put("aspect_ids", new JSONArray(aspects));

			// add json to request
			postRequest.setEntity(new StringEntity(post.toString()));

			// send request
			final HttpResponse response = session.execute(postRequest);
			if (response.getStatusLine().getStatusCode() == 201) { // successful
				// get guid
				final JSONObject postInfo = new JSONObject(EntityUtils.toString(response.getEntity()));
				return postInfo.getString("guid");
			} else { // ignore content if not successful
				response.getEntity().consumeContent();
			}
		} catch (final Exception e) {
			// reset http connection
			postRequest.abort();
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, String> getAspects() {
		final HttpGet aspectsRequest = new HttpGet(podUrl + "/bookmarklet");

		try {
			// send request
			final HttpResponse response = session.execute(aspectsRequest);
			// read response
			final InputStream content = response.getEntity().getContent();
			final BufferedReader br = new BufferedReader(new InputStreamReader(content));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				// read until the user attributes are found
				if (strLine.contains("window.current_user_attributes")) {
					// get json
					final String jsonString = strLine.substring(strLine.indexOf("= ") + 2);
					// parse json
					final JSONObject userInfo = new JSONObject(jsonString);
					final JSONArray aspects = userInfo.getJSONArray("aspects");

					final Map<String, String> aspectMap = new HashMap<String, String>();
					for (int i = 0; i < aspects.length(); ++i) {
						// read all aspects and add to map
						final JSONObject aspect = aspects.getJSONObject(i);
						aspectMap.put(aspect.getString("id"), aspect.getString("name"));
					}

					// skip the rest of the content
					response.getEntity().consumeContent();
					return aspectMap;
				}
			}
		} catch (final Exception e) {
			// reset http connection
			aspectsRequest.abort();
			e.printStackTrace();
		}
		return null;
	}

	private String getCsrfToken() {
		final HttpGet aspectsRequest = new HttpGet(podUrl + "/bookmarklet");

		try {
			// send request
			final HttpResponse response = session.execute(aspectsRequest);
			// read response
			final InputStream content = response.getEntity().getContent();
			final BufferedReader br = new BufferedReader(new InputStreamReader(content));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				// read until the csrf-token is found
				if (strLine.contains("csrf-token")) {
					// skip the rest of the content
					response.getEntity().consumeContent();
					final Matcher csrfMatcher = CSRF_TOKEN_REGEX.matcher(strLine);
					return csrfMatcher.find() ? csrfMatcher.group(1) : null;
				}
			}
		} catch (final Exception e) {
			// reset http connection
			aspectsRequest.abort();
			e.printStackTrace();
		}
		return null;
	}
}
