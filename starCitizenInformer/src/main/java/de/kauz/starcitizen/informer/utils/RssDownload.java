package de.kauz.starcitizen.informer.utils;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Object for downloading RSS feeds.
 * 
 * @author MadKauz
 * 
 */
public class RssDownload {

	private Context context;
	private RssDownloadListener listener;

	/**
	 * Listener for the download.
	 * 
	 * @author MadKauz
	 * 
	 */
	public interface RssDownloadListener {

		/**
		 * Called when a RSS Download is completed.
		 * 
		 * @param items
		 *            the downloaded items
		 */
		void onRssDownloadComplete(ArrayList<RssItem> items);

		/**
		 * Called on errors.
		 */
		void onRSSDownloadError();

	}

	/**
	 * Create a new RssDownload
	 * 
	 * @param context
	 *            of the download
	 */
	public RssDownload(Context context) {
		this.context = context;
	}

	/**
	 * Starts the RSS downloading process
	 * 
	 * @param serviceIntent
	 *            the intent to start the rss service.
	 */
	public void onStartRSSDownload(Intent serviceIntent) {
		serviceIntent.putExtra(RssService.RECEIVER, resultReceiver);
		context.startService(serviceIntent);
	}

	/**
	 * Once the {@link RssService} finishes its task, the result is sent to this
	 * ResultReceiver.
	 */
	private final ResultReceiver resultReceiver = new ResultReceiver(
			new Handler()) {
		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			if (resultCode == RssService.ERROR_CODE) {
				if (listener != null) {
					listener.onRSSDownloadError();
				}
			} else {

				ArrayList<RssItem> items = (ArrayList<RssItem>) resultData
						.getSerializable(RssService.ITEMS);
				if (items != null) {
					if (items.size() > 0) {
						items.remove(0);
					}
					onReceived(items);
				} else {
					// rss error
				}
			}
			;
		}
	};

	/**
	 * Set the download listener for RSS downloads.
	 * 
	 * @param listener
	 *            to set
	 */
	public void setRssDownloadListener(RssDownloadListener listener) {
		this.listener = listener;
	}

	/**
	 * Called when the RSS feed is completely downloaded and transformed into
	 * RSSItems.
	 * 
	 * @param items
	 */
	public void onReceived(ArrayList<RssItem> items) {
		this.listener.onRssDownloadComplete(items);
	}

}
