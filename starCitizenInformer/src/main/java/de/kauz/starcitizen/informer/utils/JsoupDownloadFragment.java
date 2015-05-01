package de.kauz.starcitizen.informer.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * A fragment with a download of an online document.
 * 
 * @author MadKauz
 * 
 */
public class JsoupDownloadFragment extends Fragment {

	private boolean isDownloadAnimationHidden = false;
	private Download download;

	/**
	 * Starts downloading of a online resource.
	 * 
	 * @param url
	 *            the url to download and parse from
	 */
	public void onStartDownloading(Context context, View loadingView, String url) {
		download = new Download(loadingView);
		download.execute(url);
	}

	/**
	 * Hides the download animation
	 */
	public void hideDownloadAnimation() {
		isDownloadAnimationHidden = true;
	}

	/**
	 * Enables the download animation
	 */
	public void enableDownloadAnimation() {
		isDownloadAnimationHidden = false;
	}

	/**
	 * Cancels the current download.
	 */
	public void onCancelDownload() {
		if (download != null) {
			download.cancel(true);
		}
	}

	/**
	 * Called when downloading of the document is completed.
	 * 
	 * @param doc
	 *            the document which was downloaded
	 */
	public void onDownloadComplete(Document doc) {
		if (doc == null) {
			if (getActivity() != null) {
				onDownloadError();
			}
		}
	}

	/**
	 * Called when downloading encountered an error.
	 */
	public void onDownloadError() {

	}

	private class Download extends AsyncTask<String, Integer, Document> {

		private View loadingView;

		public Download(View loadingView) {
			this.loadingView = loadingView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isDownloadAnimationHidden) {
				ViewHelper.animateLoading(loadingView);
			}
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
					try {
						Document doc = response.parse();
						return doc;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						return null;
					}

				} else {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Document doc) {
			ViewHelper.stopAnimatingLoading(loadingView);

			onDownloadComplete(doc);
		}
	}
}
