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
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * A download of a singe image.
 * 
 * @author MadKauz
 * 
 */
public class ImageDownload extends AsyncTask<String, Integer, Bitmap> {

	private ProgressBar progress;
	private ImageView imageView;
	private Context context;
	private ImageDownloadListener listener;

	/**
	 * Listener for downloads.
	 * 
	 * @author MadKauz
	 * 
	 */
	public interface ImageDownloadListener {

		/**
		 * Should be called when a download is complete.
		 * 
		 * @param result
		 */
		void onImageDownloadComplete(Bitmap result);

	}

	/**
	 * Initiates a download of an image. A DownloadListener can be applied to
	 * this download to listen for completed downloads.
	 * 
	 * @param context
	 * @param progress
	 * @param imageView
	 */
	public ImageDownload(Context context, ProgressBar progress,
			ImageView imageView) {
		if (progress != null) {
			this.progress = progress;
		}
		this.imageView = imageView;
		this.context = context;
	}

	/**
	 * Sets the download listener for this download
	 * 
	 * @param downloadListener
	 *            to set
	 */
	public void setOnImageDownloadListener(
			ImageDownloadListener downloadListener) {
		this.listener = downloadListener;
	}

	/**
	 * Shows an timeout error
	 */
	public void onShowError() {
		try {
			Handler errorHandler = new Handler();
			errorHandler.post(new Runnable() {

				@Override
				public void run() {
					MyApp.getInstance().showError(
							context,
							context.getResources().getString(
									R.string.errorDownloadImageProblems));
				}
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the download listener of this class.
	 * 
	 * @return the listener
	 */
	public ImageDownloadListener getOnImageDownloadListener() {
		return this.listener;
	}

	/**
	 * Starts downloading of an image.
	 * 
	 * @author Thomas Hofmann
	 * 
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (progress != null) {
			progress.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		URL newurl = null;
		Bitmap image = null;

		try {
			newurl = new URL(params[0]);
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return image;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (listener != null) {
			this.listener.onImageDownloadComplete(result);
		}
		if (progress != null) {
			progress.setVisibility(View.INVISIBLE);
		}
		AlphaAnimation loadingAnim = new AlphaAnimation(0F, 1F);
		loadingAnim.setDuration(1000);
		loadingAnim.setFillAfter(true);
		imageView.startAnimation(loadingAnim);
		if (result != null) {
			imageView.setImageBitmap(result);
		} else {
			onShowError();
			try {
				imageView.setImageDrawable(context.getResources().getDrawable(
						R.drawable.logo_sc));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				MyApp.getInstance().showError(
						context,
						context.getResources().getString(
								R.string.errorMemoryImageProblems));
			}

		}
	}

}
