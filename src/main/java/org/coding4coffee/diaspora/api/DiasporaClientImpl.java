package org.coding4coffee.diaspora.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
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
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user[username]", username));
			nameValuePairs.add(new BasicNameValuePair("user[password]", password));
			nameValuePairs.add(new BasicNameValuePair("user[remember_me]", "1"));
			signInRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			final HttpResponse response = session.execute(signInRequest);
			response.getEntity().consumeContent();

			return response.getStatusLine().getStatusCode() == 302;
		} catch (final Exception e) {
			signInRequest.abort();
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String post(final String text, final String... aspects) {
		return getCsrfToken();
	}

	@Override
	public Map<String, String> getAspects() {
		final HttpGet aspectsRequest = new HttpGet(podUrl + "/bookmarklet");

		try {
			final HttpResponse response = session.execute(aspectsRequest);
			final InputStream content = response.getEntity().getContent();
			final BufferedReader br = new BufferedReader(new InputStreamReader(content));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains("window.current_user_attributes")) {
					final String jsonString = strLine.substring(strLine.indexOf("= ") + 2);
					final JSONObject userInfo = new JSONObject(jsonString);
					final JSONArray aspects = userInfo.getJSONArray("aspects");
					final Map<String, String> aspectMap = new HashMap<String, String>();
					for (int i = 0; i < aspects.length(); ++i) {
						final JSONObject aspect = aspects.getJSONObject(i);
						aspectMap.put(aspect.getString("id"), aspect.getString("name"));
					}
					response.getEntity().consumeContent();
					return aspectMap;
				}
			}
		} catch (final Exception e) {
			aspectsRequest.abort();
			e.printStackTrace();
		}
		return null;
	}

	private String getCsrfToken() {
		final HttpGet aspectsRequest = new HttpGet(podUrl + "/bookmarklet");

		try {
			final HttpResponse response = session.execute(aspectsRequest);
			final InputStream content = response.getEntity().getContent();
			final BufferedReader br = new BufferedReader(new InputStreamReader(content));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains("csrf-token")) {
					response.getEntity().consumeContent();
					final Matcher csrfMatcher = CSRF_TOKEN_REGEX.matcher(strLine);
					return csrfMatcher.find() ? csrfMatcher.group(1) : null;
				}
			}
		} catch (final Exception e) {
			aspectsRequest.abort();
			e.printStackTrace();
		}
		return null;
	}
}
