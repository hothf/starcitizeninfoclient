package de.kauz.starcitizen.informer.fragments;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.ImageGallery;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.ZoomableOverlayImage;
import de.kauz.starcitizen.informer.utils.ImageDownload.ImageDownloadListener;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Page with a downloaded image.
 * 
 * @author MadKauz
 * 
 */
public class ImageSlide extends Fragment {

	private ZoomableOverlayImage image;
	private ProgressBar progress;
	private Bitmap imageBitmap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_images_slide, container, false);
		this.image = (ZoomableOverlayImage) rootView.findViewById(R.id.imagesSlideImage);
		this.progress = (ProgressBar) rootView
				.findViewById(R.id.imagesSlideProgress);
	
		Bundle arguments = getArguments();
		if (arguments != null) {

			this.imageBitmap = (Bitmap) arguments
					.getParcelable(InformerConstants.ARGUMENTS_IMAGE);
			String url = arguments
					.getString(InformerConstants.ARGUMENTS_IMAGE_ULS);
			this.image.setURL(url);
			// if image is available then show it
			if (this.imageBitmap != null) {
				this.image.setImageBitmap(imageBitmap);
			} else { // download otherwise
				ImageDownload download = new ImageDownload(getActivity(), progress , image);
				download.setOnImageDownloadListener(new ImageDownloadListener() {
					
					@Override
					public void onImageDownloadComplete(Bitmap result) {
						try {
							if ((ImageGallery) getActivity() != null) {
								((ImageGallery) getActivity()).addImage(result);
							}
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
				});
				download.execute(url);
			}

		}

		return rootView;
	}
}
