package de.kauz.starcitizen.informer.activities;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.fragments.ImageSlide;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * A gallery for swiping images.
 * 
 * @author MadKauz
 * 
 */
public class ImageGallery extends FragmentActivity {

	public ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

	private Typeface font;
	private Button closeButton;
	private String firstBitmapUrl;
	private ArrayList<CharSequence> imageUrls = new ArrayList<CharSequence>();
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private int numPages = 1;
	private TextView infoText;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagegallery);

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setIcon(InformerConstants.MENU_ICONS[1]);
		}

		this.infoText = (TextView) findViewById(R.id.ImageGalleryInfoText);
		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");
		this.closeButton = (Button) findViewById(R.id.ImageGalleryCloseButton);
		this.infoText.setTypeface(font);
		this.closeButton.setTypeface(font);
		this.closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.firstBitmapUrl = extras
					.getString(InformerConstants.EXTRAS_ACTIVITY_FIRST_BITMAP);
			this.imageUrls = extras
					.getCharSequenceArrayList(InformerConstants.EXTRAS_ACTIVITY_IMAGEURLLIST);
			this.imageUrls.add(0, this.firstBitmapUrl);

			this.numPages = imageUrls.size();

		}

		setTitle("" + getString(R.string.imageGalleryTitle) + " " + 1 + " of "
				+ numPages);

		mPager = (ViewPager) findViewById(R.id.ImageGalleryPager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		mPager.setOnTouchListener(new View.OnTouchListener() {
			float oldX = 0, newX = 0, sens = 5;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					oldX = event.getX();
					break;

				case MotionEvent.ACTION_UP:
					newX = event.getX();
					if (Math.abs(oldX - newX) < sens) {
						if (closeButton.getVisibility() == View.INVISIBLE) {
							closeButton.setVisibility(View.VISIBLE);
						} else {
							closeButton.setVisibility(View.INVISIBLE);
						}
						return true;
					}
					oldX = 0;
					newX = 0;
					break;
				}

				return false;
			}
		});

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				setTitle("" + getString(R.string.imageGalleryTitle) + " "
						+ (position + 1) + " of " + numPages);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			super.onBackPressed();
		} else {
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	@Override
	protected void onDestroy() {
		for (Bitmap bitmap : bitmaps) {
			if (bitmap != null) {
				bitmap.recycle();
			}
		}
		super.onDestroy();
	}

	/**
	 * Adds an image to all images of the gallery.
	 * 
	 * @param scaledBitmap
	 *            the image to add (scaled)
	 */
	public void addImage(Bitmap scaledBitmap) {
		this.bitmaps.add(scaledBitmap);
	}

	/**
	 * A simple pager adapter.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle arguments = new Bundle();
			ImageSlide slide = new ImageSlide();
			if (bitmaps.size() <= position) {
				arguments
						.putParcelable(InformerConstants.ARGUMENTS_IMAGE, null);
				if (imageUrls.get(position) != null) {
					arguments.putString(InformerConstants.ARGUMENTS_IMAGE_ULS,
							(String) imageUrls.get(position));
				}
			} else {
				arguments.putParcelable(InformerConstants.ARGUMENTS_IMAGE,
						bitmaps.get(position));
				if (imageUrls.get(position) != null) {
					arguments.putString(InformerConstants.ARGUMENTS_IMAGE_ULS,
							(String) imageUrls.get(position));
				}
			}
			slide.setArguments(arguments);
			return slide;
		}

		@Override
		public int getCount() {
			return numPages;
		}
	}
}
