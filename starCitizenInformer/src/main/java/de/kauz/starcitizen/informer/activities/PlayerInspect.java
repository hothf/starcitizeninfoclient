package de.kauz.starcitizen.informer.activities;

import org.jsoup.nodes.Document;
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
 * Player inspection to view info on players.
 * 
 * @author MadKauz
 * 
 */
public class PlayerInspect extends JsoupDownloadActivity {

	private TextView playerLoading, playerInfos1, playerInfos2, playerInfos3,
			playerInfos4, playerInfos5, playerInfos6, playerInfos7,
			playerInfos8, playerInfos9, playerInfos1Desc, playerInfos2Desc,
			playerInfos3Desc, playerInfos4Desc, playerInfos5Desc,
			playerInfos6Desc, playerInfos7Desc, playerInfos8Desc,
			playerInfos9Desc, playerInfoNotAvailable;
	private ProgressBar playerImageProgress;
	private ZoomableOverlayImage playerImage, playerRoleImage;
	private Button playerDetailButton, playerOrgaButton;
	private ScrollView downloadContainer;
	private String handle;
	private Favourite fav;
	private boolean isAlreadyFavourite = false;

	private Typeface font;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playerinspect);

		this.playerInfoNotAvailable = (TextView) findViewById(R.id.PlayerInfosNotAvailable);
		this.playerLoading = (TextView) findViewById(R.id.PlayerLoading);
		this.playerInfos1Desc = (TextView) findViewById(R.id.PlayerInfos1Desc);
		this.playerInfos2Desc = (TextView) findViewById(R.id.PlayerInfos2Desc);
		this.playerInfos3Desc = (TextView) findViewById(R.id.PlayerInfos3Desc);
		this.playerInfos4Desc = (TextView) findViewById(R.id.PlayerInfos4Desc);
		this.playerInfos5Desc = (TextView) findViewById(R.id.PlayerInfos5Desc);
		this.playerInfos6Desc = (TextView) findViewById(R.id.PlayerInfos6Desc);
		this.playerInfos7Desc = (TextView) findViewById(R.id.PlayerInfos7Desc);
		this.playerInfos8Desc = (TextView) findViewById(R.id.PlayerInfos8Desc);
		this.playerInfos9Desc = (TextView) findViewById(R.id.PlayerInfos9Desc);
		this.playerInfos1 = (TextView) findViewById(R.id.PlayerInfos1);
		this.playerInfos2 = (TextView) findViewById(R.id.PlayerInfos2);
		this.playerInfos3 = (TextView) findViewById(R.id.PlayerInfos3);
		this.playerInfos4 = (TextView) findViewById(R.id.PlayerInfos4);
		this.playerInfos5 = (TextView) findViewById(R.id.PlayerInfos5);
		this.playerInfos6 = (TextView) findViewById(R.id.PlayerInfos6);
		this.playerInfos7 = (TextView) findViewById(R.id.PlayerInfos7);
		this.playerInfos8 = (TextView) findViewById(R.id.PlayerInfos8);
		this.playerInfos9 = (TextView) findViewById(R.id.PlayerInfos9);
		this.playerImageProgress = (ProgressBar) findViewById(R.id.PlayerImageProgress);
		this.playerImage = (ZoomableOverlayImage) findViewById(R.id.PlayerImage);
		this.playerRoleImage = (ZoomableOverlayImage) findViewById(R.id.PlayerInfos4RoleImage);
		this.playerDetailButton = (Button) findViewById(R.id.PlayerDetailButton);
		this.playerOrgaButton = (Button) findViewById(R.id.PlayerInfos5OrgaButton);
		this.downloadContainer = (ScrollView) findViewById(R.id.downloadContainer);

		this.font = Typeface.createFromAsset(getBaseContext().getAssets(),
				"Electrolize-Regular.ttf");

		this.playerDetailButton.setTypeface(font);
		this.playerInfoNotAvailable.setTypeface(font);
		this.playerLoading.setTypeface(font);
		this.playerInfos1Desc.setTypeface(font);
		this.playerInfos2Desc.setTypeface(font);
		this.playerInfos3Desc.setTypeface(font);
		this.playerInfos4Desc.setTypeface(font);
		this.playerInfos5Desc.setTypeface(font);
		this.playerInfos6Desc.setTypeface(font);
		this.playerInfos7Desc.setTypeface(font);
		this.playerInfos8Desc.setTypeface(font);
		this.playerInfos9Desc.setTypeface(font);
		this.playerInfos1.setTypeface(font);
		this.playerInfos2.setTypeface(font);
		this.playerInfos3.setTypeface(font);
		this.playerInfos4.setTypeface(font);
		this.playerInfos5.setTypeface(font);
		this.playerInfos6.setTypeface(font);
		this.playerInfos7.setTypeface(font);
		this.playerInfos8.setTypeface(font);
		this.playerInfos9.setTypeface(font);
		this.playerOrgaButton.setTypeface(font);

		this.playerInfoNotAvailable.setVisibility(View.INVISIBLE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.url = extras
					.getString(InformerConstants.DETAIL_EXTRAS_PLAYER_SEARCH_LINK);
			handle = extras
					.getString(InformerConstants.DETAIL_EXTRAS_PLAYER_HANDLE);
			setTitle(handle);
			getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[6]);

			if (MyApp.getInstance().isOnline(getApplicationContext())) {
				if (this.url.endsWith(" ")) {
					this.url = url.substring(0, url.length() - 1);
				}
				onStartDownloading(playerLoading, this.url);
			}

			// open up the link in a new browser window
			this.playerDetailButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(PlayerInspect.this,
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
		inflater.inflate(R.menu.playerinspect, menu);
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
			fav = new Favourite(handle, Favourite.TYPE_PLAYERS, url, "", "", "");
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

	// parse all info:
	@Override
	public void onDownloadComplete(Document doc) {
		if (!hasErrorDownloading()) {

			String profileImageUrl = null, roleImageUrl = null;

			if (doc != null) {
				try {
					try {
						Elements imageUrlElements = doc.select("div.thumb");
						profileImageUrl = imageUrlElements.get(0).child(0)
								.attr("abs:src");
					} catch (NullPointerException e) {
						e.printStackTrace();
					}

					try {
						Elements roleUrlElements = doc.select("div.info");
						playerInfos1.setText(roleUrlElements.get(0).child(0)
								.child(0).text());
						roleImageUrl = roleUrlElements.get(0).child(2).child(0)
								.child(0).attr("abs:src");
						playerInfos4.setText(roleUrlElements.get(0).child(2)
								.child(1).text());

					} catch (NullPointerException e) {
						e.printStackTrace();
					}

					if (MyApp.getInstance().isOnline(getApplicationContext())) {
						if (profileImageUrl != null) {
							ImageDownload profileImageDownload = new ImageDownload(
									getBaseContext(), playerImageProgress,
									playerImage);
							profileImageDownload.execute(profileImageUrl);
						}
						if (roleImageUrl != null) {
							ImageDownload roleImageDownload = new ImageDownload(
									getBaseContext(), null, playerRoleImage);
							roleImageDownload.execute(roleImageUrl);
						}
					}

					Elements topElements = doc.select("div.profile-content");
					String number = topElements.get(0).child(0).child(1).text();
					playerInfos2.setText(number);

					Elements furtherElements = doc.select("div.left-col");
					playerInfos3.setText(furtherElements.get(1).child(0)
							.child(0).child(1).text());
					if (furtherElements.get(1).child(0).children().size() - 1 > 0) {
						playerInfos6Desc.setText(furtherElements.get(1)
								.child(0).child(1).child(0).text());
						playerInfos6.setText(furtherElements.get(1).child(0)
								.child(1).child(1).text());
						if (!playerInfos6Desc.getText().equals("Forum")) {
							ViewHelper.fadeIn(playerInfos6Desc);
							ViewHelper.fadeIn(playerInfos6);
						}
					}
					if (furtherElements.get(1).child(0).children().size() - 1 > 1) {
						playerInfos7Desc.setText(furtherElements.get(1)
								.child(0).child(2).child(0).text());
						playerInfos7.setText(furtherElements.get(1).child(0)
								.child(2).child(1).text());
						if (!playerInfos7Desc.getText().equals("Forum")) {
							ViewHelper.fadeIn(playerInfos7Desc);
							ViewHelper.fadeIn(playerInfos7);
						}
					}

					if (furtherElements.get(1).child(0).children().size() - 1 > 2) {
						playerInfos8Desc.setText(furtherElements.get(1)
								.child(0).child(3).child(0).text());
						playerInfos8.setText(furtherElements.get(1).child(0)
								.child(3).child(1).text());
						if (!playerInfos8Desc.getText().equals("Forum")) {
							ViewHelper.fadeIn(playerInfos8Desc);
							ViewHelper.fadeIn(playerInfos8);
						}
					}

					if (furtherElements.get(1).child(0).children().size() - 1 > 3) {
						playerInfos9Desc.setText(furtherElements.get(1)
								.child(0).child(4).child(0).text());
						playerInfos9.setText(furtherElements.get(1).child(0)
								.child(4).child(1).text());
						if (!playerInfos9Desc.getText().equals("Forum")) {
							ViewHelper.fadeIn(playerInfos9Desc);
							ViewHelper.fadeIn(playerInfos9);
						}
					}

					try {
						final Elements orgElements = doc.select("div.main-org");

						playerOrgaButton.setVisibility(View.VISIBLE);
						String orga = orgElements.get(0).child(1).child(1)
								.text();

						playerOrgaButton.setText(orga);

						playerOrgaButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent intent = new Intent(
												PlayerInspect.this,
												OrgaInspect.class);
										String orgaLink = orgElements.get(0)
												.child(1).child(1).child(0)
												.child(0).attr("abs:href");
										intent.putExtra(
												InformerConstants.DETAIL_EXTRAS_ORGA_SEARCH_LINK,
												orgaLink);
										intent.putExtra(
												InformerConstants.DETAIL_EXTRAS_ORGA_HANLDE,
												orgElements.get(0).text());
										startActivity(intent);
									}
								});
					} catch (NullPointerException e) {
						e.printStackTrace();
						playerInfos5.setText("-");
						playerOrgaButton.setVisibility(View.INVISIBLE);
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
						playerInfos5.setText("-");
						playerOrgaButton.setVisibility(View.INVISIBLE);
					}
					ViewHelper.fadeIn(downloadContainer);
				} catch (NullPointerException e) {
					e.printStackTrace();
					MyApp.getInstance().showError(PlayerInspect.this,getResources().getString(R.string.errorParseFault));
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					MyApp.getInstance().showError(PlayerInspect.this,getResources().getString(R.string.errorParseFault));
				}
			} else {
				MyApp.getInstance().showError(PlayerInspect.this,getResources().getString(R.string.errorServerFault));
			}
		} else {
			ViewHelper.fadeIn(downloadContainer);
			playerInfoNotAvailable.setVisibility(View.VISIBLE);
			playerInfos1.setText("-");
			playerInfos2.setText("-");
			playerInfos3.setText("-");
			playerInfos4.setText("-");
			playerInfos5.setText("-");
			playerInfos6.setText("-");
			playerInfos7.setText("-");
			playerInfos8.setText("-");
			playerInfos9.setText("-");
		}

		super.onDownloadComplete(doc);
	}
}
