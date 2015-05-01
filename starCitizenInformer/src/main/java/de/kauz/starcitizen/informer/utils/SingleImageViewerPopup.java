package de.kauz.starcitizen.informer.utils;

import java.io.File;
import java.io.FileOutputStream;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.utils.HiResImageDownload.ImageDownloadListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Views a single image in a overlay.
 * 
 * @author MadKauz
 * 
 */
public class SingleImageViewerPopup {

	private Dialog overlay;
	private int height, width;
	private Context context;

	/**
	 * Creates an overlay with a zoomed image.
	 * 
	 * @param image
	 *            to be displayed in the overlay
	 */
	public SingleImageViewerPopup(Context context, View parent) {
		this.overlay = new Dialog(context);
		this.overlay.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		this.width = metrics.widthPixels;
		this.height = metrics.heightPixels;

		overlay.setContentView(R.layout.popup_single_image);

	}

	/**
	 * Sets the bitmap to display.
	 * 
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		if (height > width) {
			overlay.setContentView(R.layout.popup_single_image);
		} else {
			overlay.setContentView(R.layout.popup_single_image_landscape);
		}
		ImageView viewToShowImage = (ImageView) overlay
				.findViewById(R.id.popUpSingleImage);

		if (bitmap != null) {

			LayoutParams params = null;
			if (height > width) {
				float ratio = ((float) (bitmap.getWidth()) / (float) (bitmap
						.getHeight()));
				params = new LayoutParams((int) ((width * 0.8) * ratio),
						(int) (height * 0.5));
			} else {
				float ratio = ((float) (bitmap.getHeight()) / (float) (bitmap
						.getWidth()));
				params = new LayoutParams((int) ((width * 0.5)),
						(int) ((height * 0.8) * ratio));
			}
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			viewToShowImage.setLayoutParams(params);
			viewToShowImage.setImageBitmap(bitmap);

			viewToShowImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					close();

				}
			});
		}
	}

	/**
	 * Shows the overlay with the image.
	 */
	public void open() {
		overlay.show();
	}

	/**
	 * Closes the overlay.
	 */
	public void close() {
		overlay.dismiss();
	}

	/**
	 * Downloads high res image and displays it.
	 * 
	 * @param url
	 */
	public void showBig(String url) {

		final Button saveButton = (Button) overlay
				.findViewById(R.id.popUpSingleImageSaveButton);
		final ImageButton shareButton = (ImageButton) overlay
				.findViewById(R.id.popUpSingleImageShareButton);
		final ImageView viewToShowImage = (ImageView) overlay
				.findViewById(R.id.popUpSingleImage);
		final ProgressBar progress = (ProgressBar) overlay
				.findViewById(R.id.popUpSingleImageProgress);

		HiResImageDownload download = new HiResImageDownload(context, progress,
				viewToShowImage);
		download.setOnImageDownloadListener(new ImageDownloadListener() {

			@Override
			public void onImageDownloadComplete(final Bitmap bitmap) {
				if (bitmap != null) {

					LayoutParams params = null;
					if (height > bitmap.getHeight()) {
						params = new LayoutParams(
								bitmap.getWidth() * 3, bitmap
										.getHeight() * 3);
					} else {
						params = new LayoutParams((bitmap.getWidth()),
								(bitmap.getHeight()));
					}
					params.addRule(RelativeLayout.CENTER_IN_PARENT,
							RelativeLayout.TRUE);
					viewToShowImage.setLayoutParams(params);
					viewToShowImage.setImageBitmap(bitmap);

					saveButton.setVisibility(View.VISIBLE);
					saveButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String title = context.getResources().getString(
									R.string.saveImageTitlePrefix)
									+ System.currentTimeMillis();
							String description = context.getResources()
									.getString(R.string.saveImageDescription);
							MyApp.insertImage(context, bitmap, title,
									description);
							saveButton.setVisibility(View.GONE);
						}
					});

					shareButton.setVisibility(View.VISIBLE);
					shareButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							String file_path = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/SCInformerShared";
							File dir = new File(file_path);
							if (!dir.exists())
								dir.mkdirs();
							String title = context.getResources().getString(
									R.string.saveImageTitlePrefix)
									+ System.currentTimeMillis();
							File file = new File(dir, title);
							FileOutputStream fOut;
							try {
								fOut = new FileOutputStream(file);
								bitmap.compress(Bitmap.CompressFormat.PNG, 85,
										fOut);
								fOut.flush();
								fOut.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

							Uri uri = Uri.fromFile(file);
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_SEND);
							intent.setType("image/*");

							intent.putExtra(
									android.content.Intent.EXTRA_SUBJECT, "");
							intent.putExtra(android.content.Intent.EXTRA_TEXT,
									"");
							intent.putExtra(Intent.EXTRA_STREAM, uri);
							context.startActivity(Intent.createChooser(
									intent,
									context.getResources().getText(
											R.string.shareImage)));

						}
					});

					viewToShowImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							close();

						}
					});
				}

			}
		});
		download.execute(url);

	}

}
