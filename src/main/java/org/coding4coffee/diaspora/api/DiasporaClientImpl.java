package org.coding4coffee.diaspora.api;

import java.io.BufferedReader;
import java.io.IOException;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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
import org.coding4coffee.diaspora.api.exceptions.AspectsNotFoundException;
import org.coding4coffee.diaspora.api.exceptions.CsrfTokenNotFoundException;
import org.coding4coffee.diaspora.api.exceptions.NotLoggedInException;
import org.coding4coffee.diaspora.api.exceptions.PodFailureException;
import org.coding4coffee.diaspora.api.exceptions.PostingException;
import org.coding4coffee.diaspora.api.upload.ProgressByteArrayEntity;
import org.coding4coffee.diaspora.api.upload.ProgressListener;
import org.json.JSONArray;
import org.json.JSONException;
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
	public boolean login(final String username, final String password) throws IOException {
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
			return isRedirected(response);
		} catch (final IOException e) {
			// reset http connection
			signInRequest.abort();
			throw e;
		}
	}

	@Override
	public String createPost(final String text, final String aspect) throws IOException, PodFailureException {
		return createPost(text, Arrays.asList(new String[] { aspect }));
	}

	@Override
	public String createPost(final String text, final Collection<String> aspects) throws IOException,
			PodFailureException {
		// get CSRF token (and check if logged in)
		final String csrfToken = getCsrfToken();

		final HttpPost postRequest = new HttpPost(podUrl + "/status_messages");

		try {
			// add header
			postRequest.addHeader("content-type", "application/json");
			postRequest.addHeader("accept", "application/json");
			postRequest.addHeader("X-CSRF-Token", csrfToken);

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
			}
			// ignore content if not successful
			response.getEntity().consumeContent();
			throw new PostingException("Error while creating the post! Probably the diaspora behavior has changed.");
		} catch (final IOException e) {
			// reset http connection
			postRequest.abort();
			throw e;
		} catch (final ParseException e) {
			// reset http connection
			postRequest.abort();
			throw new IOException(e);
		} catch (final JSONException e) {
			// reset http connection
			postRequest.abort();
			throw new PodFailureException(e);
		}
	}

	@Override
	public String uploadPhoto(final byte[] photoBytes, final ProgressListener listener) throws IOException,
			PodFailureException {
		// get CSRF token (and check if logged in)
		final String csrfToken = getCsrfToken();

		final HttpPost photoRequest = new HttpPost(podUrl + "/photos?photo%5Baspect_ids%5D=all&qqfile=uploaded.jpg");

		try {
			// add header
			photoRequest.addHeader("content-type", "application/octet-stream");
			photoRequest.addHeader("X-CSRF-Token", csrfToken);

			final HttpEntity photoEntity = new ProgressByteArrayEntity(photoBytes, listener);
			photoRequest.setEntity(photoEntity);

			// send request
			final HttpResponse response = session.execute(photoRequest);
			if (response.getStatusLine().getStatusCode() == 200) { // successful
				// get guid
				final JSONObject photoJson = new JSONObject(EntityUtils.toString(response.getEntity()));
				final JSONObject photoData = photoJson.getJSONObject("data").getJSONObject("photo");
				System.out.println(photoData.getJSONObject("unprocessed_image").getString("url"));
				return photoData.getString("guid");
			}
			// ignore content if not successful
			// response.getEntity().consumeContent();
			System.out.println(EntityUtils.toString(response.getEntity()));
			// TODO
			throw new PodFailureException("Error while creating the post! Probably the diaspora behavior has changed.");
		} catch (final IOException e) {
			// reset http connection
			photoRequest.abort();
			throw e;
		} catch (final ParseException e) {
			// reset http connection
			photoRequest.abort();
			throw new IOException(e);
		} catch (final JSONException e) {
			// reset http connection
			photoRequest.abort();
			throw new PodFailureException(e);
		}
	}

	@Override
	public Map<String, String> getAspects() throws IOException, PodFailureException {
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
			throw isRedirected(response) ? new NotLoggedInException("Not logged in!") : new AspectsNotFoundException(
					"No user attributes found in response! Probably the diaspora behavior has changed.");
		} catch (final IOException e) {
			// reset http connection
			aspectsRequest.abort();
			throw e;
		} catch (final JSONException e) {
			// reset http connection
			aspectsRequest.abort();
			throw new PodFailureException(e);
		}
	}

	private String getCsrfToken() throws IOException, PodFailureException {
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
					if (csrfMatcher.find()) {
						return csrfMatcher.group(1);
					}
					break;
				}
			}
			throw isRedirected(response) ? new NotLoggedInException("Not logged in!") : new CsrfTokenNotFoundException(
					"CSRF-Token couldn't be found! Probably the diaspora behavior has changed.");
		} catch (final IOException e) {
			// reset http connection
			aspectsRequest.abort();
			throw e;
		}
	}

	private boolean isRedirected(final HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 302;
	}
}
