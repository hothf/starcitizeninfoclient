package de.kauz.starcitizen.informer.activities;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.utils.Translator;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.ImageDownload.ImageDownloadListener;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadActivity;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import de.kauz.starcitizen.informer.utils.ZoomableOverlayImage;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Detail View containing detailed info of a news.
 * 
 * @author MadKauz
 * 
 */
public class Detail extends JsoupDownloadActivity implements OnClickListener {

	private TextView detailLoading, detailPosted, detailComments,
			detailParagraph1, detailParagraph2, detailIntroduction,
			scrollToBottom, scrollToTop;
	private Button detailLinkButton;
	private ImageButton shareButton;
	private ImageView detailCommentsImage;
	private ZoomableOverlayImage detailPreviewImage;
	private ProgressBar detailImageLoadingProgress;
	private Typeface font;
	private String type;
	private Button detailVideoStartButton, detailGalleryOpenButton;
	private ScrollView downloadContainer;
	private RelativeLayout scrollHelper;
	private boolean isAlreadyFavourite = false;
	private Favourite fav;
	private String title;

	private String url = "", shareText = "", posted = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		this.detailIntroduction = (TextView) findViewById(R.id.DetailTextIntroduction);
		this.detailPosted = (TextView) findViewById(R.id.DetailPosted);
		this.shareButton = (ImageButton) findViewById(R.id.DetailShareButton);
		this.detailComments = (TextView) findViewById(R.id.DetailComments);
		this.detailParagraph1 = (TextView) findViewById(R.id.DetailTextParagraph1);
		this.detailParagraph2 = (TextView) findViewById(R.id.DetailTextParagraph2);
		this.scrollToBottom = (TextView) findViewById(R.id.scrollBottom);
		this.scrollToTop = (TextView) findViewById(R.id.scrollTop);
		this.detailImageLoadingProgress = (ProgressBar) findViewById(R.id.DetailImageLoadingProgress);
		this.detailLinkButton = (Button) findViewById(R.id.DetailLinkButton);
		this.detailLoading = (TextView) findViewById(R.id.DetailLoading);
		this.detailPreviewImage = (ZoomableOverlayImage) findViewById(R.id.DetailPreViewImage);
		this.detailVideoStartButton = (Button) findViewById(R.id.DetailPrevStartVideo);
		this.detailGalleryOpenButton = (Button) findViewById(R.id.DetailPreOpenGallery);
		this.downloadContainer = (ScrollView) findViewById(R.id.downloadContainer);
		this.scrollHelper = (RelativeLayout) findViewById(R.id.scrollHelper);

		this.detailCommentsImage = (ImageView) findViewById(R.id.DetailImage);

		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");
		this.detailLoading.setTypeface(font);
		this.detailComments.setTypeface(font);
		this.detailPosted.setTypeface(font);
		this.detailParagraph1.setTypeface(font);
		this.detailParagraph2.setTypeface(font);
		this.detailLinkButton.setTypeface(font);
		this.detailIntroduction.setTypeface(font);
		this.scrollToBottom.setTypeface(font);
		this.scrollToTop.setTypeface(font);

		Bundle extras = getIntent().getExtras();

		// if this is coming from the featured site, there is fewer
		// information available;
		if (extras != null) {
			title = extras.getString(InformerConstants.DETAIL_EXTRAS_TITLE);
			this.url = extras.getString(InformerConstants.DETAIL_EXTRAS_LINK);
			this.type = extras.getString(InformerConstants.DETAIL_EXTRAS_TYPE);
			posted = extras
					.getString(InformerConstants.DETAIL_EXTRAS_POSTED_TIME);

			setTitle(title);
			getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[1]);

			String introduction = extras
					.getString(InformerConstants.DETAIL_EXTRAS_INFO);
			this.detailCommentsImage.setVisibility(View.GONE);

			if (type == null) {
				type = News.TYPE_NEWS;
			} else {
				if (type.equals("")) {
					type = News.TYPE_NEWS;
				}
			}
			if (posted == null) {
				posted = getResources().getString(R.string.mainNewsFound) + " "
						+ MyApp.getInstance().retrievePostingTime();
			}
			if (this.url == null) {
				this.url = "https://robertsspaceindustries.com/";
			} else {
				if (this.url.equals("")) {
					this.url = "https://robertsspaceindustries.com/";
				}
			}

			News item = new News(url, title, type, 0, posted, introduction);
			item.setStatus("2");
			MyApp.getInstance().getAgent().updateNews(item);

			this.detailIntroduction.setText(introduction);
			this.detailPosted.setText(posted);

			if (introduction != null) {
				this.shareText = title + "\n\n" + introduction + "\n\n" + url
						+ "\n" + getResources().getString(R.string.shareSuffix);
			} else {
				this.shareText = title + "\n\n" + url + "\n"
						+ getResources().getString(R.string.shareSuffix);
				this.detailIntroduction.setVisibility(View.GONE);
			}

			if (MyApp.getInstance().isOnline(this)) {
				onStartDownloading(detailLoading, url);
			}
		}

		this.downloadContainer.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int scrollY = v.getScrollY();
				if (scrollY >= ViewHelper.getDisplayHeight(getBaseContext())) {
					ViewHelper.animateFromBottomToTop(scrollHelper);
				} else {
					ViewHelper.animateFromTopToBottom(scrollHelper);
				}
				return false;
			}
		});

		this.scrollToBottom.setOnClickListener(this);
		this.scrollToTop.setOnClickListener(this);

		this.detailLinkButton.setOnClickListener(this);
		this.shareButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.shareDetails)));
			return true;

		case R.id.action_fav:
			if (isAlreadyFavourite) {
				item.setIcon(R.drawable.ic_fav_normal_big);
				item.setTitle(getResources().getString(R.string.setAsFav));
				MyApp.getInstance().getAgent().deleteFavourite(fav);
				isAlreadyFavourite = false;
			} else {
				item.setIcon(R.drawable.ic_fav_pressed_big);
				item.setTitle(getResources().getString(R.string.unsetFav));
				MyApp.getInstance().getAgent().persistFavourite(fav);
				isAlreadyFavourite = true;
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_share).setVisible(true);
		menu.findItem(R.id.action_fav).setVisible(true);
		if (title != null) {
			fav = new Favourite(title, Favourite.TYPE_NEWS, url, "", "", "");
			if (MyApp.getInstance().getAgent().isFavAlreadySaved(fav)) {
				isAlreadyFavourite = true;
				menu.getItem(1).setIcon(R.drawable.ic_fav_pressed_big);
				menu.getItem(1).setTitle(
						getResources().getString(R.string.unsetFav));
			} else {
				isAlreadyFavourite = false;
				menu.getItem(1).setIcon(R.drawable.ic_fav_normal_big);
				menu.getItem(1).setTitle(
						getResources().getString(R.string.setAsFav));
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	// handles parsing of the news:
	@Override
	public void onDownloadComplete(Document doc) {
		String imageUrl = null;
		boolean isSlideShow = false;

		ViewHelper.fadeIn(downloadContainer);

		if (doc != null) {
			try {
				// check the type of the news
				// if video:
				Elements videoUrlElements = doc.select("div.embed-container");
				Elements imageElements = null;
				if (videoUrlElements.html().length() > 2) {
					type = "video";
					try {
						final String videoUrl = videoUrlElements.get(0)
								.child(0).attr("abs:src");
						detailVideoStartButton.setVisibility(View.VISIBLE);
						detailVideoStartButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Uri uri = Uri.parse(videoUrl);
										Intent intent = new Intent(
												Intent.ACTION_VIEW, uri);
										startActivity(intent);
									}
								});
					} catch (NullPointerException e) {
						MyApp.getInstance().showError(
								Detail.this,
								getResources().getString(
										R.string.errorParseFault));
					}
				}

				Elements imageUrlElements = doc.select("div.content-block2");
				Elements imageUrl2Elements = null;

				try {
					imageUrl = imageUrlElements.first().child(0).child(0)
							.attr("abs:src");

				} catch (NullPointerException e) {
					try {
						imageUrl2Elements = doc
								.select("a.js-open-in-slideshow");
						imageElements = imageUrl2Elements.select("img");
						if (!imageElements.isEmpty()) {
							isSlideShow = true;
							imageUrl = imageElements.first().attr("abs:src");
						}
					} catch (NullPointerException ex) {
						ex.printStackTrace();

					}
				}

				if (imageUrl != null) {
					if (imageUrl.equals("")) {
						try {
							imageUrl2Elements = doc.select("a.download");
							imageElements = null;
							if (!imageUrl2Elements.isEmpty()) {
								isSlideShow = true;
								imageUrl = imageUrl2Elements.first().attr(
										"abs:href");
							}

						} catch (NullPointerException exe) {
							exe.printStackTrace();

						}
					}
				}

				ArrayList<CharSequence> imageUrls = new ArrayList<CharSequence>();
				try {
					if (isSlideShow && imageElements != null) {
						for (int j = 1; j < imageElements.size(); j++) {
							String url = imageElements.get(j).attr("abs:src");
							imageUrls.add(url);
						}
					} else {
						for (int j = 1; j < imageUrl2Elements.size(); j++) {
							if (isSlideShow && imageElements == null) {
								String url = imageUrl2Elements.get(j).attr(
										"abs:href");
								imageUrls.add(url);
							}
						}
					}
				} catch (NullPointerException e) {
					detailPreviewImage.setImageResource(R.drawable.logo_sc);
				}

				final String firstImageUrl = imageUrl;
				final ArrayList<CharSequence> otherImageUrls = imageUrls;

				if (MyApp.getInstance().isOnline(getApplicationContext())
						&& imageUrl != null) {
					final boolean isShow = isSlideShow;
					ImageDownload download = new ImageDownload(
							getBaseContext(), detailImageLoadingProgress,
							detailPreviewImage);

					download.setOnImageDownloadListener(new ImageDownloadListener() {

						@Override
						public void onImageDownloadComplete(Bitmap result) {
							if (!type.equals("video")) {
								if (isShow) {
									showPics(firstImageUrl, otherImageUrls);
								} else {
								}
							}
						}
					});
					download.execute(imageUrl);
				}

				Elements infoElements = doc.select("div.content");
				detailParagraph1.setText(""
						+ Translator.translateContent(infoElements.html()));
			} catch (NullPointerException e) {
				MyApp.getInstance().showError(Detail.this,
						getResources().getString(R.string.errorParseFault));
			} catch (IndexOutOfBoundsException e) {
				MyApp.getInstance().showError(Detail.this,
						getResources().getString(R.string.errorParseFault));
			}
		} else {
			MyApp.getInstance().showError(Detail.this,
					getResources().getString(R.string.errorServerFault));
		}

		if (imageUrl == null) {
			detailPreviewImage.setImageResource(R.drawable.logo_sc);
		} else {
			if (imageUrl.equals("")) {
				detailPreviewImage.setImageResource(R.drawable.logo_sc);
			}
		}

		super.onDownloadComplete(doc);
	}

	/**
	 * Shows pics
	 */
	private void showPics(final String firstBitmapUrl,
			final ArrayList<CharSequence> imageUrls) {

		detailGalleryOpenButton.setVisibility(View.VISIBLE);
		detailGalleryOpenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent slideImageGalleryIntent = new Intent(Detail.this,
						ImageGallery.class);
				slideImageGalleryIntent.putExtra(
						InformerConstants.EXTRAS_ACTIVITY_FIRST_BITMAP,
						firstBitmapUrl);
				slideImageGalleryIntent.putCharSequenceArrayListExtra(
						InformerConstants.EXTRAS_ACTIVITY_IMAGEURLLIST,
						imageUrls);
				startActivity(slideImageGalleryIntent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.DetailLinkButton:

			Intent browserIntent = new Intent(this, BrowserContainer.class);
			browserIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME, title);
			browserIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL, url);
			startActivity(browserIntent);

			break;
		case R.id.scrollBottom:
			downloadContainer.post(new Runnable() {
				@Override
				public void run() {
					downloadContainer.fullScroll(View.FOCUS_DOWN);
					ViewHelper.animateFromTopToBottom(scrollHelper);
				}
			});
			break;
		case R.id.scrollTop:
			downloadContainer.scrollTo(0, 0);
			ViewHelper.animateFromTopToBottom(scrollHelper);
			break;
		case R.id.DetailShareButton:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.shareDetails)));
			break;

		default:
			break;
		}

	}
}
