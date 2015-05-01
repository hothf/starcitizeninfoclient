package de.kauz.starcitizen.informer.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * A download of multiple images.
 * 
 * @author MadKauz
 * 
 */
public class MultiImageDownload extends
		AsyncTask<String, Integer, ArrayList<Bitmap>> {

	private ProgressBar progress;
	private ImageView imageView;
	private Context context;
	private MultiImageDownloadListener listener;
	private String[] urls;
	private int maxItemsToDownload = 0;

	/**
	 * Listener for downloads.
	 * 
	 * @author MadKauz
	 * 
	 */
	public interface MultiImageDownloadListener {

		/**
		 * Should be called when a download is complete.
		 * 
		 * @param result
		 */
		void onImagesDownloadComplete(ArrayList<Bitmap> result);

	}

	/**
	 * Initiates a download of several images. A DownloadListener can be applied
	 * to this download to listen for completed downloads.
	 * 
	 * @param context
	 * @param progress
	 * @param imageView
	 * @param urls
	 * @param maxItemsToDownload
	 */
	public MultiImageDownload(Context context, ProgressBar progress,
			ImageView imageView, String[] urls, int maxItemsToDownload) {
		if (progress != null) {
			this.progress = progress;
		}
		this.imageView = imageView;
		this.context = context;
		this.urls = urls;
		this.maxItemsToDownload = maxItemsToDownload;
	}

	/**
	 * Sets the download listener for this download
	 * 
	 * @param downloadListener
	 *            to set
	 */
	public void setOnDownloadListener(
			MultiImageDownloadListener downloadListener) {
		this.listener = downloadListener;
	}

	/**
	 * Shows an timeout error
	 */
	public void onShowError() {
		 MyApp.getInstance().showError(context,context.getResources().getString(
		 R.string.errorDownloadImageProblems));
	}

	/**
	 * Retrieves the download listener of this class.
	 * 
	 * @return the listener
	 */
	public MultiImageDownloadListener getOnImageDownloadListener() {
		return this.listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (progress != null) {
			progress.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected ArrayList<Bitmap> doInBackground(String... params) {
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		try {
			for (int i = 0; i < maxItemsToDownload; i++) {
				URL newurl = new URL(urls[i]);
				Bitmap image = null;
				try {
					if (newurl != null) {
						try {
							HttpURLConnection connection = (HttpURLConnection) newurl
									.openConnection();
							connection
									.setConnectTimeout(InformerConstants.TIMEOUT_CONNECTION);
							image = MyApp.decodeImageStream(
									connection.getInputStream(), newurl);
							images.add(image);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return images;
	}

	@Override
	protected void onPostExecute(ArrayList<Bitmap> result) {
		if (listener != null) {
			this.listener.onImagesDownloadComplete(result);
		}
		if (progress != null) {
			progress.setVisibility(View.INVISIBLE);
		}
		AlphaAnimation loadingAnim = new AlphaAnimation(0F, 1F);
		loadingAnim.setDuration(1000);
		loadingAnim.setFillAfter(true);
		imageView.startAnimation(loadingAnim);
		if (result != null) {
			if (result.size() <= 0){
				imageView.setImageResource(R.drawable.logo_sc);
			} else {
				imageView.setImageBitmap(result.get(0));
			}
		} else {
			onShowError();
			imageView.setImageDrawable(context.getResources().getDrawable(
					R.drawable.rsi_logo));
		}
	}

}
