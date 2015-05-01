package de.kauz.starcitizen.informer.fragments;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.nodes.Document;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Detail;
import de.kauz.starcitizen.informer.activities.Goals;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.NewsFeedListViewAdapter;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import de.kauz.starcitizen.informer.utils.ApiDownload;
import de.kauz.starcitizen.informer.utils.ApiDownload.ApiDownloadListener;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadFragment;
import de.kauz.starcitizen.informer.utils.MultiImageDownload;
import de.kauz.starcitizen.informer.utils.MultiImageDownload.MultiImageDownloadListener;
import de.kauz.starcitizen.informer.utils.RssDownload;
import de.kauz.starcitizen.informer.utils.RssDownload.RssDownloadListener;
import de.kauz.starcitizen.informer.utils.SCDocumentParser.ErrorTypes;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.SCDocumentParser;
import de.kauz.starcitizen.informer.utils.SCDocumentParser.SCDocumentParserListener;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This view contains the news feed of the RSI homepage. It also includes a
 * featured content window.
 * 
 * @author MadKauz
 * 
 */
public class NewsFeed extends JsoupDownloadFragment {

	private PullToRefreshListView informerListView;
	private NewsFeedListViewAdapter informerListViewAdapter;
	private TextView informerLoading, featuredText, featuredInfo,
			emptySearchResult, noConnectionInfo;
	private ImageView featuredImage;
	private ImageButton featuredClose;
	private TranslateAnimation toRight;
	private Typeface font;
	private ProgressBar featuredProgress, informerProgress;
	private RelativeLayout featuredContainer, statsContainer;
	private MultiImageDownload multiImageDownload;

	private int requestedItemIndex = 0;
	private ArrayList<News> tmpSavedNews = new ArrayList<News>();

	private Handler repeatHandler;
	private Handler animationHandler;

	private String[] urls, imgUrls, texts, descriptions;

	private int fetchNewsAmount = 10;

	private boolean isCancelRequested = false;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_newsfeed, container, false);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.emptySearchResult = (TextView) getView().findViewById(
				R.id.newsFeedEmptySearchResult);
		this.featuredText = (TextView) getView().findViewById(
				R.id.MainFeaturedText);
		this.noConnectionInfo = (TextView) getView().findViewById(
				R.id.MaiNoConnectionInfo);
		this.featuredInfo = (TextView) getView().findViewById(
				R.id.MainFeaturedInfo);
		this.featuredImage = (ImageView) getView().findViewById(
				R.id.MainFeaturedImage);
		this.featuredProgress = (ProgressBar) getView().findViewById(
				R.id.MainFeaturedProgress);
		this.featuredClose = (ImageButton) getView().findViewById(
				R.id.MainFeaturedClose);

		this.featuredContainer = (RelativeLayout) getView().findViewById(
				R.id.MainFeaturedContainer);
		this.statsContainer = (RelativeLayout) getView().findViewById(
				R.id.statsConatiner);

		this.informerLoading = (TextView) getView().findViewById(
				R.id.MainInformerLoading);
		this.informerListView = (PullToRefreshListView) getView().findViewById(
				R.id.MainInformerListView);
		this.informerProgress = (ProgressBar) getView().findViewById(
				R.id.MainInformerProgressBar);

		this.toRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
				0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		this.toRight.setFillAfter(true);
		this.toRight.setDuration(650);
		this.toRight.setInterpolator(new AccelerateInterpolator());

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.informerLoading.setTypeface(font);
		this.noConnectionInfo.setTypeface(font);
		this.featuredInfo.setTypeface(font);
		this.featuredText.setTypeface(font);
		this.emptySearchResult.setTypeface(font);

		registerForContextMenu(this.informerListView);
		this.informerListViewAdapter = new NewsFeedListViewAdapter(
				getActivity(), this.font, this);
		this.informerListView.setAdapter(informerListViewAdapter);

		this.informerListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (MyApp.getInstance().isOnline(getActivity())) {
							hideDownloadAnimation();
							fetchInfo();
						} else {
							Handler handler = new Handler();
							handler.post(new Runnable() {

								@Override
								public void run() {
									informerListView.onRefreshComplete();
								}
							});

						}

					}
				});
		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[1]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[1]);

		this.featuredClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHelper.goneView(featuredContainer);
			}
		});
		informerListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}

			@Override
			public void onScrollStateChanged(AbsListView listView,
					int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					int threshold = 2;
					if (listView.getLastVisiblePosition() >= listView
							.getCount() - 1 - threshold) {
						News ne = (News) informerListViewAdapter
								.getItem(informerListViewAdapter.getCount() - 1);
						informerListViewAdapter.removeLoadingItem(ne);
						informerListViewAdapter.addNews(MyApp
								.getInstance()
								.getAgent()
								.fetchNewsTable(
										informerListViewAdapter.getNews()
												.size(), fetchNewsAmount));
						informerListViewAdapter.notifyDataSetChanged();
					}
				}
			}
		});

		playStartingAnimation();
	}

	/**
	 * LifeCycle onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
		informerListViewAdapter.notifyDataSetChanged();
	}

	/**
	 * Parses the crowdFunding information and shows it to the user.
	 * 
	 * @param result
	 *            the info to parse
	 */
	private void parseCrowdFundingInfo(final String result) {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				TextView fansRaised = (TextView) statsContainer
						.findViewById(R.id.crowdStatsFansRaised);
				TextView fansRaisedInfo = (TextView) statsContainer
						.findViewById(R.id.crowdStatsFansRaisedInfo);
				TextView fundsRaised = (TextView) statsContainer
						.findViewById(R.id.crowdStatsFundsRaised);
				TextView fundsRaisedInfo = (TextView) statsContainer
						.findViewById(R.id.crowdStatsFundsRaisedInfo);
				fundsRaised.setTypeface(font);
				fansRaised.setTypeface(font);
				fansRaisedInfo.setTypeface(font);
				fundsRaisedInfo.setTypeface(font);

				JSONTokener tokener = new JSONTokener(result);
				try {
					JSONObject json = new JSONObject(tokener);
					JSONObject data = json.getJSONObject("data");

					String funds = data.getString("funds");
					long fans = data.getLong("fans");
					// final double percentage = goal.getDouble("percentage");
					// String goalFunds = goal.getString("goal");

					String correctedFunds = funds.substring(0,
							funds.length() - 2);
					// String correctedNextGoal = goalFunds.substring(1,
					// goalFunds.length());

					final String fundsText = NumberFormat.getNumberInstance(
							Locale.US).format(Long.valueOf(correctedFunds));
					// final String nextgoalFundsText = correctedNextGoal;
					// final String percentageText = "" + percentage + "%";

					fansRaised.setText(NumberFormat
							.getNumberInstance(Locale.US).format(fans));
					fundsRaised.setText(fundsText);
					// goalPercentage.setText(percentageText);

					ViewHelper.progressfadeIn(statsContainer);

					statsContainer.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent goalsIntent = new Intent(getActivity(),
									Goals.class);
							goalsIntent.putExtra(Goals.EXTRA_FUNDS, fundsText);
							goalsIntent
									.putExtra(Goals.EXTRA_PERCENTAGE, "100%");
							goalsIntent.putExtra(Goals.EXTRA_GOAL_FUNDS,
									"REDACTED");
							goalsIntent.putExtra(Goals.EXTRA_PERCENTAGE_INT,
									100);
							getActivity().startActivity(goalsIntent);
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, 1500);
	}

	@Override
	public void onDestroyView() {
		featuredImage.setImageBitmap(null);
		super.onDestroyView();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.MainInformerListView) {
			String[] menuItems = getResources().getStringArray(
					R.array.newsItemMenu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	/**
	 * Hook for the longlick on an item. Sets the requested item and opens up
	 * the context menu.
	 * 
	 * @param requestedItemIndex
	 *            of an listitem
	 */
	public void openContext(int requestedItemIndex) {
		getActivity().openContextMenu(informerListView);
		this.requestedItemIndex = requestedItemIndex;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			informerListViewAdapter.markItemAsRead(requestedItemIndex);
			informerListViewAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * Starts the download of the website contents and plays an animation.
	 */
	private void playStartingAnimation() {

		ViewHelper.progressfadeIn(informerProgress);
		hideDownloadAnimation();

		// starts the downloading
		fetchInfo();
	}

	/**
	 * This method fetches all infos from the RSI website, if the device is
	 * online.
	 */
	public void fetchInfo() {
		notifyWidget();
		onCancelDownload();
		noConnectionInfo.setVisibility(View.GONE);

		if (multiImageDownload != null) {
			multiImageDownload.cancel(true);
		}
		if (repeatHandler != null) {
			repeatHandler.removeCallbacksAndMessages(null);
			repeatHandler = null;
			isCancelRequested = true;
		}
		if (animationHandler != null) {
			animationHandler.removeCallbacksAndMessages(null);
			animationHandler = null;
			isCancelRequested = true;
		}
		featuredImage.setImageBitmap(null);

		informerListViewAdapter.clearNews();
		tmpSavedNews.clear();
		if (MyApp.getInstance().isOnline(getActivity())) {

			// download featured content
			onStartDownloading(getActivity(), informerLoading,
					InformerConstants.URL_MAIN_HOMEPAGE);
			ViewHelper.fadeOut(statsContainer);

			// download crowdfunding info
			ApiDownload apiDownload = new ApiDownload(
					"https://robertsspaceindustries.com/api/stats/getCrowdfundStats",
					"{\"alpha_slots\": true,\"chart\": \"day\",\"fans\": true,\"funds\": true}");
			apiDownload.setApiDownloadListener(new ApiDownloadListener() {

				@Override
				public void onApiDownloadComplete(String result) {
					if (result != null) {
						if (result.length() > 4) {
							parseCrowdFundingInfo(result);
						}
					}
				}
			});

			// download RSS
			RssDownload download = new RssDownload(getActivity());
			RssDownloadListener rssListener = new RssDownloadListener() {

				@Override
				public void onRssDownloadComplete(ArrayList<RssItem> items) {

					for (RssItem item : items) {

						String url = item.getLink();
						String type = News.TYPE_NEWS;
						String title = item.getTitle();
						String description = item.getDescription();
						News news = new News(url, title, type, 0, "",
								description);
						news.setStatus("1");
						tmpSavedNews.add(news);
					}

					if (tmpSavedNews.size() > 0) {
						if (tmpSavedNews.get(0).getTitle()
								.equals("RSI Comm-Link")) {
							tmpSavedNews.remove(0);
						}
					}

					MyApp.getInstance().getAgent().persistAllNews(tmpSavedNews);
					informerListViewAdapter.setNews(MyApp.getInstance()
							.getAgent().fetchNewsTable(0, fetchNewsAmount));
					informerListViewAdapter.notifyDataSetChanged();
					notifyMain();
					ViewHelper.fadeIn(informerListView);
				}

				@Override
				public void onRSSDownloadError() {
					MyApp.getInstance().showError(MyApp.getInstance(),
							getResources().getString(R.string.errorParseFault));
				}
			};
			download.setRssDownloadListener(rssListener);

			Intent intent = new Intent(getActivity(), RssService.class);
			intent.putExtra(RssService.LINK, RssService.RSSLINKNEWS);
			intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_NEWS);
			download.onStartRSSDownload(intent);
		} else {
			noConnectionInfo.setVisibility(View.VISIBLE);
			informerListView.onRefreshComplete();
			informerListViewAdapter.setNews(MyApp.getInstance().getAgent()
					.fetchNewsTable(0, fetchNewsAmount));
			informerListViewAdapter.notifyDataSetChanged();
			ViewHelper.fadeOut(informerProgress);
		}
	}

	@Override
	public void onDownloadError() {
	}

	@Override
	public void onDownloadComplete(final Document doc) {

		informerListView.onRefreshComplete();

		ViewHelper.fadeOut(informerProgress);
		enableDownloadAnimation();
		SCDocumentParser parser = new SCDocumentParser(doc);
		parser.setSCDocumentParserListener(new SCDocumentParserListener() {

			@Override
			public void onParsingFeaturedComplete(String[] imageUrls,
					String[] texts, String[] urls, String[] descriptions) {
				addFeatured(imageUrls, urls, texts, descriptions);
			}

			@Override
			public void onParsingError(ErrorTypes type) {
				switch (type) {
				case ERROR_NPE:
					if (isAdded()) {
						MyApp.getInstance().showError(
								MyApp.getInstance(),
								getResources().getString(
										R.string.errorParseFault));
						informerListViewAdapter.setNews(MyApp.getInstance()
								.getAgent().fetchNewsTable(0, fetchNewsAmount));
						informerListViewAdapter.notifyDataSetChanged();
					}
					break;
				case ERROR_OUT_OF_BOUNDS:
					MyApp.getInstance().showError(MyApp.getInstance(),
							getResources().getString(R.string.errorParseFault));
					informerListViewAdapter.setNews(MyApp.getInstance()
							.getAgent().fetchNewsTable(0, fetchNewsAmount));
					informerListViewAdapter.notifyDataSetChanged();
					break;

				default:
					break;
				}

			}
		});
		parser.parseFeatured();

		super.onDownloadComplete(doc);
	}

	// TODO Site has changed, change this also!
	/**
	 * Adds the featured container.
	 * 
	 * @param imageUrls
	 *            image urls of the featured content
	 * @param urls
	 *            urls of the featured content
	 * @param texts
	 *            texts of the featured content
	 */
	public void addFeatured(String[] imageUrls, String[] urls, String[] texts,
			String[] descriptions) {
		this.texts = texts;
		this.imgUrls = imageUrls;
		this.urls = urls;
		this.descriptions = descriptions;

		if (imgUrls.length > 0) {
			if (multiImageDownload != null) {
				multiImageDownload.cancel(true);
			}
			multiImageDownload = new MultiImageDownload(getActivity(),
					featuredProgress, featuredImage, imageUrls, 3);
			multiImageDownload.execute();

			multiImageDownload
					.setOnDownloadListener(new MultiImageDownloadListener() {

						@Override
						public void onImagesDownloadComplete(
								ArrayList<Bitmap> result) {
							if (result.size() < 1) {
							} else {
								animationHandler = new Handler();
								repeatHandler = new Handler();
								isCancelRequested = false;
								animateFeatured(result);
							}
						}
					});

			ViewHelper.fadeIn(featuredContainer);
		}

	}

	/**
	 * Queries the list of an argument typed
	 * 
	 * @param arg0
	 *            the argument typed
	 */
	@SuppressLint("DefaultLocale")
	public void queryArgument(String arg0) {
		emptySearchResult.setVisibility(View.GONE);
		if (informerListView != null) {
			if (informerListViewAdapter != null) {
				ArrayList<News> queriedNews = new ArrayList<News>();
				for (News news : tmpSavedNews) {
					if (news.getTitle().toLowerCase()
							.contains(arg0.toLowerCase())) {
						queriedNews.add(news);
					}
				}
				informerListViewAdapter.clearNews();
				informerListViewAdapter.setNews(queriedNews);
				if (queriedNews.size() < 1) {
					emptySearchResult.setVisibility(View.VISIBLE);
				}

			}
		}
	}

	/**
	 * Animates the featured content with a fading animation.
	 */
	private void animateFeatured(final ArrayList<Bitmap> result) {

		int interval = 5000;
		if (!isCancelRequested) {
			for (int i = 0; i < result.size(); i++) {
				final int counter = i;
				final String text = texts[i];
				final String link = urls[i];
				final String description = descriptions[i];
				animationHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						featuredText.setText(text);

						AlphaAnimation loadingAnim = new AlphaAnimation(0F, 1F);
						loadingAnim.setDuration(1000);
						loadingAnim.setFillAfter(true);

						featuredImage.setImageBitmap(result.get(counter));
						featuredImage.startAnimation(loadingAnim);

						featuredImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getActivity(),
										Detail.class);
								intent.putExtra(
										InformerConstants.DETAIL_EXTRAS_TITLE,
										text);
								intent.putExtra(
										InformerConstants.DETAIL_EXTRAS_LINK,
										link);
								intent.putExtra(
										InformerConstants.DETAIL_EXTRAS_INFO,
										description);
								getActivity().startActivity(intent);
							}
						});

					};
				}, 0 + interval * i);

			}
			repeatHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					animateFeatured(result);
				}
			}, interval * result.size());
		}
	}

	/**
	 * Marks all news as read.
	 */
	public void markAllRead() {
		this.informerListViewAdapter.markAllRead();
		this.informerListViewAdapter.notifyDataSetChanged();
	}

	/**
	 * Sends a notificatin for the app widget to refresh itself.
	 *
	 */
	private void notifyWidget() {
		Intent intent = new Intent();
		intent.setAction("update_widget_from_activity");
        if (getActivity() != null){
            getActivity().sendBroadcast(intent);
        }
	}

	/**
	 * Notifies the main activity about the unsaved count.
	 * 
	 */
	public void notifyMain() {
		Main act = (Main) getActivity();
		try {
			act.setCounter(1, MyApp.getInstance().getAgent()
					.getUnsavedNewsCount());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}
}