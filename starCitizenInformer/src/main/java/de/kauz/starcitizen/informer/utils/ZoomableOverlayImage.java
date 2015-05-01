package de.kauz.starcitizen.informer.utils;

import de.kauz.starcitizen.informer.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * A zoom-able image. Zoom function is only available in an overlay
 * 
 * @author MadKauz
 * 
 */
public class ZoomableOverlayImage extends ImageView {

	private final Context context;
	private SingleImageViewerPopup viewer;
	private String url = "";

	public ZoomableOverlayImage(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ZoomableOverlayImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ZoomableOverlayImage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init() {
	}

	@Override
	public void setImageBitmap(final Bitmap bm) {
		super.setImageBitmap(bm);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewer = new SingleImageViewerPopup(context,
						ZoomableOverlayImage.this);
				try {
					if (url.length() > 0) {
						viewer.showBig(url);
					} else {
						viewer.setBitmap(bm);
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					MyApp.getInstance().showError(
							context,
							context.getResources().getString(
									R.string.errorMemoryImageProblems));
				}

				viewer.open();
			}
		});
	}

	/**
	 * Set a url of an image.
	 * 
	 * @param url
	 *            to set
	 */
	public void setURL(String url) {
		this.url = url;
	}

}
