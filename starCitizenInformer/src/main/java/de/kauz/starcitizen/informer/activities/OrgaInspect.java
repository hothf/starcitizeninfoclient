package de.kauz.starcitizen.informer.activities;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadActivity;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import de.kauz.starcitizen.informer.utils.ZoomableOverlayImage;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Organization inspection to view info of organizations.
 * 
 * @author MadKauz
 * 
 */
public class OrgaInspect extends JsoupDownloadActivity {

	private TextView orgaLoading, orgaInfos1, orgaInfos2, orgaInfos3,
			orgaInfos4, orgaNotFound, orgaMemberInfo, orgaInfo1Desc,
			orgaInfo2Desc, orgaInfo3Desc, orgaInfo4Desc;
	private ProgressBar orgaImageProgress;
	private ZoomableOverlayImage orgaImage;
	private Button orgaDetailButton;
	private ScrollView downloadContainer;

	private String handle;
	private boolean isAlreadyFavourite = false;
	private Favourite fav;

	private Typeface font;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgainspect);

		this.orgaMemberInfo = (TextView) findViewById(R.id.OrgaMemeberInfo);
		this.orgaNotFound = (TextView) findViewById(R.id.OrganizationInfosNotAvailable);
		this.orgaLoading = (TextView) findViewById(R.id.OrgaLoading);
		this.orgaInfo1Desc = (TextView) findViewById(R.id.OrgaInfos1Desc);
		this.orgaInfo2Desc = (TextView) findViewById(R.id.OrgaInfos2Desc);
		this.orgaInfo3Desc = (TextView) findViewById(R.id.OrgaInfos3Desc);
		this.orgaInfo4Desc = (TextView) findViewById(R.id.OrgaInfos4Desc);
		this.orgaInfos1 = (TextView) findViewById(R.id.OrgaInfo1);
		this.orgaInfos2 = (TextView) findViewById(R.id.OrgaInfo2);
		this.orgaInfos3 = (TextView) findViewById(R.id.OrgaInfo3);
		this.orgaInfos4 = (TextView) findViewById(R.id.OrgaInfo4);
		this.orgaImageProgress = (ProgressBar) findViewById(R.id.OrgaImageProgress);
		this.orgaImage = (ZoomableOverlayImage) findViewById(R.id.OrgaImage);
		this.orgaDetailButton = (Button) findViewById(R.id.OrgaDetailButton);
		this.downloadContainer = (ScrollView) findViewById(R.id.downloadContainer);

		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");

		this.orgaDetailButton.setTypeface(font);
		this.orgaMemberInfo.setTypeface(font);
		this.orgaLoading.setTypeface(font);
		this.orgaInfo1Desc.setTypeface(font);
		this.orgaInfo2Desc.setTypeface(font);
		this.orgaInfo3Desc.setTypeface(font);
		this.orgaInfo4Desc.setTypeface(font);
		this.orgaInfos1.setTypeface(font);
		this.orgaInfos2.setTypeface(font);
		this.orgaInfos3.setTypeface(font);
		this.orgaInfos4.setTypeface(font);
		this.orgaNotFound.setTypeface(font);

		this.orgaNotFound.setVisibility(View.INVISIBLE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.url = extras
					.getString(InformerConstants.DETAIL_EXTRAS_ORGA_SEARCH_LINK);
			handle = extras
					.getString(InformerConstants.DETAIL_EXTRAS_ORGA_HANLDE);
			setTitle(handle);
			getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[6]);

			if (MyApp.getInstance().isOnline(getApplicationContext())) {
				if (this.url.endsWith(" ")) {
					this.url = url.substring(0, url.length() - 1);
				}
				onStartDownloading(orgaLoading, this.url);
			}

			// open up the link in a new browser window
			this.orgaDetailButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(OrgaInspect.this,
							BrowserContainer.class);
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
							handle);
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL, url);
					startActivity(intent);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.orgainspect, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		menu.findItem(R.id.action_fav).setVisible(true);
		if (handle != null) {
			fav = new Favourite(handle, Favourite.TYPE_ORGS, url, "", "", "");
			if (MyApp.getInstance().getAgent().isFavAlreadySaved(fav)) {
				isAlreadyFavourite = true;
				menu.getItem(0).setIcon(R.drawable.ic_fav_pressed_big);
				menu.getItem(0).setTitle(
						getResources().getString(R.string.unsetFav));
			} else {
				isAlreadyFavourite = false;
				menu.getItem(0).setIcon(R.drawable.ic_fav_normal_big);
				menu.getItem(0).setTitle(
						getResources().getString(R.string.setAsFav));
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	// parse info:
	@Override
	public void onDownloadComplete(Document doc) {
		if (!hasErrorDownloading()) {
			String imageUrl = null;

			if (doc != null) {
				try {
					Element imageUrlElement = doc.select("div.logo").first();

					try {
						imageUrl = imageUrlElement.child(0).attr("abs:src");
						String memberCount = imageUrlElement.child(1).text();
						orgaMemberInfo.setText(memberCount);
					} catch (NullPointerException e) {
					}

					if (MyApp.getInstance().isOnline(getApplicationContext())
							&& imageUrl != null) {
						ImageDownload download = new ImageDownload(
								getBaseContext(), orgaImageProgress, orgaImage);
						download.execute(imageUrl);
					}

					Elements infoElements = doc.select("div.inner");
					for (Element iElement : infoElements) {
						orgaInfos1.setText(iElement.child(1).text());
						orgaInfos2.setText(iElement.child(2).text());
						orgaInfos3.setText(iElement.child(3).text());
					}

					Elements furtherElements = doc.select("div.body");
					orgaInfos4.setText(furtherElements.get(0).text());
					ViewHelper.fadeIn(downloadContainer);

				} catch (NullPointerException e) {
					MyApp.getInstance().showError(OrgaInspect.this,getResources().getString(R.string.errorParseFault));
				} catch (IndexOutOfBoundsException e) {
					MyApp.getInstance().showError(OrgaInspect.this,getResources().getString(R.string.errorParseFault));
				}
			} else {
				MyApp.getInstance().showError(OrgaInspect.this,getResources().getString(R.string.errorServerFault));
			}

		} else {
			ViewHelper.fadeIn(downloadContainer);
			orgaNotFound.setVisibility(View.VISIBLE);
			orgaInfos1.setText("-");
			orgaInfos2.setText("-");
			orgaInfos3.setText("-");
		}
		super.onDownloadComplete(doc);
	}

}
