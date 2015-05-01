package de.kauz.starcitizen.informer.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Activity with a (support) actionbar which offers a download of an online
 * document.
 * 
 * @author MadKauz
 * 
 */
public class JsoupDownloadActivity extends ActionBarActivity {

	private boolean isError;

	/**
	 * Starts downloading of a online resource.
	 * 
	 * @param url
	 *            the url to download and parse from
	 */
	public void onStartDownloading(View loadingView, String url) {
		Download download = new Download(loadingView);
		download.execute(url);
	}

	/**
	 * Determines if an error occurs while downloading.
	 */
	private void isDownloadError(boolean isError) {
		this.isError = isError;
	}

	/**
	 * Retrieves the error status of the download.
	 * 
	 * @return true if an error occured, false otherwise
	 */
	public boolean hasErrorDownloading() {
		return isError;
	}

	/**
	 * Called when downloading of the document is completed.
	 * 
	 * @param doc
	 *            the document which was downloaded
	 */
	public void onDownloadComplete(Document doc) {
		if (doc == null) {
		}
	}

	private class Download extends AsyncTask<String, Integer, Document> {

		private View loadingView;

		public Download(View loadingView) {
			this.loadingView = loadingView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ViewHelper.animateLoading(loadingView);
		}

		@Override
		protected Document doInBackground(String... params) {

			try {
				Connection.Response response = Jsoup
						.connect(params[0])
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1) Gecko/20100101 Firefox/8.0.1")
						.ignoreHttpErrors(true)
						.timeout(InformerConstants.TIMEOUT_CONNECTION)
						.execute();
				int statusCode = response.statusCode();
				if (statusCode == 200) {
					isDownloadError(false);
					Document doc = response.parse();
					return doc;
				} else {
					isDownloadError(true);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Document doc) {
			ViewHelper.stopAnimatingLoading(loadingView);

			onDownloadComplete(doc);
		}
	}
}
