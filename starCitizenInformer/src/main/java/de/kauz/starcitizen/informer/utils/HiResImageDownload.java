package de.kauz.starcitizen.informer.utils;

import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import de.kauz.starcitizen.informer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * A download of a single high resolution image.
 * 
 * @author MadKauz
 * 
 */
public class HiResImageDownload extends AsyncTask<String, Integer, Bitmap> {

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
	public HiResImageDownload(Context context, ProgressBar progress,
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
	 * @author MadKauz
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
		final DefaultHttpClient client = new DefaultHttpClient();

		final HttpGet getRequest = new HttpGet(params[0]);
		try {

			HttpResponse response = client.execute(getRequest);

			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				return null;

			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();

					Bitmap bitmap = null;

					try {

						bitmap = BitmapFactory.decodeStream(inputStream);

						if (bitmap.getHeight() > 2048
								|| bitmap.getWidth() > 2048) {

							int width, height;

							if (bitmap.getHeight() >= bitmap.getWidth()) {
								float res = (float) bitmap.getWidth()
										/ (float) bitmap.getHeight();

								width = (int) (2048 * res);
								height = 2048;
								try {
									bitmap = Bitmap.createScaledBitmap(bitmap,
											width, height, false);
								} catch (OutOfMemoryError e) {
									e.printStackTrace();
									bitmap = null;
								}

							} else {
								float res = (float) bitmap.getHeight()
										/ (float) bitmap.getWidth();

								width = 2048;
								height = (int) (2048 * res);
								try {
									bitmap = Bitmap.createScaledBitmap(bitmap,
											width, height, false);
								} catch (OutOfMemoryError e) {
									e.printStackTrace();
									bitmap = null;
								}

							}

						}
					} catch (OutOfMemoryError e) {
					}

					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
		}

		return null;
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
