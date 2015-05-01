package de.kauz.starcitizen.informer.utils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

/**
 * Handles downloading of the RSI API.
 * 
 * @author MadKauz
 * 
 */
public class ApiDownload {

	private ApiDownloadListener listener;

	/**
	 * Creates a new download for the API.
	 * 
	 * @param url
	 *            the url to use
	 * @param payload
	 *            the payload
	 */
	public ApiDownload(String url, String payload) {
		RestPost download = new RestPost(payload);
		download.execute(url);
		listener = null;
	}

	/**
	 * Interface for downloading callbacks.
	 * 
	 * @author MadKauz
	 * 
	 */
	public interface ApiDownloadListener {

		public void onApiDownloadComplete(String result);

	}

	public void setApiDownloadListener(ApiDownloadListener listener) {
		this.listener = listener;
	}

	public ApiDownloadListener getApiDownloadListener() {
		return this.listener;
	}

	/**
	 * Sends request.
	 * 
	 * @author MadKauz
	 * 
	 */
	private class RestPost extends AsyncTask<String, Integer, String> {

		private String payload;

		/**
		 * Creates a POST with the specified payload.
		 * 
		 * @param payload the payload
		 */
		public RestPost(String payload) {
			this.payload = payload;
		}

		HttpURLConnection connection = null;

		@Override
		protected String doInBackground(String... params) {

			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection
						.setConnectTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.setReadTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.setDoOutput(true);

				OutputStream os = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(this.payload);
				writer.flush();
				writer.close();
				os.close();

				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "error";
				} else {
					@SuppressWarnings("resource")
					java.util.Scanner s = new java.util.Scanner(
							connection.getInputStream()).useDelimiter("\\A");
					if (s.hasNext()) {
						String ss = s.next();
						s.close();
						return ss;
					} else {
						s.close();
						return "";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		};

		@Override
		protected void onPostExecute(String result) {
			if (listener != null) {
				listener.onApiDownloadComplete(result);
			}
		}

	}

}
