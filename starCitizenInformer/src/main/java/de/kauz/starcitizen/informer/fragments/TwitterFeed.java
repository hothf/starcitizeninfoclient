package de.kauz.starcitizen.informer.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.TwitterListViewAdapter;
import de.kauz.starcitizen.informer.model.Tweet;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import de.kauz.starcitizen.informer.utils.ZoomableOverlayImage;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Twitter feed reader of the twitter channel from RSI. A clicked feed leads to
 * the attached url of the feed. This implementation is using twitter API v1.1
 * with oAuth.
 * 
 * @author MadKauz
 * 
 */
public class TwitterFeed extends Fragment {

	private static final String URL_BASE = "https://api.twitter.com";
	private static final String URL_SEARCH = URL_BASE
			+ "/1.1/statuses/user_timeline.json?screen_name=";
	private static final String URL_AUTH = URL_BASE + "/oauth2/token";

    //TODO add your info here
	private static final String CONSUMER_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXX";
	private static final String CONSUMER_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	private Typeface font;
	private PullToRefreshListView twitterFeedList;
	private TwitterListViewAdapter twitterFeedListAdapter;
	private TextView informerLoading;
	private ZoomableOverlayImage twitterLogo;
	private ProgressBar logoProgress;

	private boolean isPullToRefreshTriggered = false;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.fragment_twitterfeed, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.twitterLogo = (ZoomableOverlayImage) getView().findViewById(
				R.id.TwitterUserLogo);
		this.logoProgress = (ProgressBar) getView().findViewById(
				R.id.TwitterLogoProgress);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");

		this.informerLoading = (TextView) getView().findViewById(
				R.id.MoreLoading);
		this.informerLoading.setTypeface(font);

		this.twitterFeedListAdapter = new TwitterListViewAdapter(getActivity(),
				font);

		this.twitterFeedList = (PullToRefreshListView) getView().findViewById(
				R.id.TwitterListView);
		this.twitterFeedList.setAdapter(twitterFeedListAdapter);

		this.twitterFeedList
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						isPullToRefreshTriggered = true;
						fetchTweets();
					}

				});

		ImageDownload download = new ImageDownload(getActivity(), logoProgress,
				twitterLogo);
		download.execute(InformerConstants.URL_TWITTER_IMAGE);
		this.fetchTweets();

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[4]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[4]);
	}

	/**
	 * Fetches all tweets from robert space industriees account
	 */
	public void fetchTweets() {
		if (MyApp.getInstance().isOnline(getActivity())) {
			DownloadTweets download = new DownloadTweets();
			download.execute("RobertsSpaceInd");
		}

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
					MyApp.getInstance().showError(getActivity(),
							getResources().getString(R.string.errorNotSure));
				}
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts downloading of the contents of the RSI website. When finished
	 * downloading, all info is returned.
	 * 
	 * @author MadKauz
	 * 
	 */
	private class DownloadTweets extends
			AsyncTask<String, Integer, ArrayList<Tweet>> {

		/**
		 * Requires the auth. of the app.
		 */
		private String authenticateApp() {

			HttpURLConnection connection = null;
			OutputStream os = null;
			BufferedReader br = null;
			StringBuilder response = null;

			try {
				URL url = new URL(URL_AUTH);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setDoInput(true);

				String credetials = CONSUMER_KEY + ":" + CONSUMER_SECRET;
				String authentication = "Basic "
						+ Base64.encodeToString(credetials.getBytes(),
								Base64.NO_WRAP);
				String params = "grant_type=client_credentials";

				connection.addRequestProperty("Authorization", authentication);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				connection.connect();

				os = connection.getOutputStream();
				os.write(params.getBytes());
				os.flush();
				os.close();

				br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line;
				response = new StringBuilder();

				while ((line = br.readLine()) != null) {
					response.append(line);
				}
			} catch (Exception e) {
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
			return response.toString();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isPullToRefreshTriggered) {
				ViewHelper.animateLoading(informerLoading);
			}
		}

		@Override
		protected ArrayList<Tweet> doInBackground(String... param) {

			String term = param[0];
			ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			HttpURLConnection connection = null;
			BufferedReader br = null;

			try {
				URL url = new URL(URL_SEARCH + term);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection
						.setConnectTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.setReadTimeout(InformerConstants.TIMEOUT_CONNECTION);

				String jsonString = authenticateApp();
				JSONObject jsonAccess = new JSONObject(jsonString);
				String token = jsonAccess.getString("token_type") + " "
						+ jsonAccess.getString("access_token");

				connection.setRequestProperty("Authorization", token);
				connection.setRequestProperty("Content-Type",
						"application/json");
				connection.connect();

				br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));

				String line;
				StringBuilder response = new StringBuilder();

				while ((line = br.readLine()) != null) {
					response.append(line);
				}

				JSONArray jsonArray = new JSONArray(response.toString());
				JSONObject jsonObject;

				for (int i = 0; i < jsonArray.length(); i++) {

					jsonObject = (JSONObject) jsonArray.get(i);
					Tweet tweet = new Tweet();

					JSONObject entities = jsonObject.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject urlObj = (JSONObject) urls.get(0);
						tweet.setUrl(urlObj.getString("url"));
					}

					tweet.setTweetcount(jsonObject.getString("retweet_count"));

					tweet.setName(jsonObject.getJSONObject("user").getString(
							"name"));
					tweet.setUserName(jsonObject.getJSONObject("user")
							.getString("screen_name"));
					tweet.setUrlImage(jsonObject.getJSONObject("user")
							.getString("profile_image_url"));
					tweet.setMessage(jsonObject.getString("text"));
					tweet.setData(jsonObject.getString("created_at"));

					tweets.add(i, tweet);
				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
			return tweets;
		}

		@Override
		protected void onPostExecute(ArrayList<Tweet> tweets) {
			if (!isPullToRefreshTriggered) {
				ViewHelper.stopAnimatingLoading(informerLoading);
			}
			twitterFeedList.onRefreshComplete();
			isPullToRefreshTriggered = false;
			if (tweets != null) {
				if (tweets.isEmpty()) {
				} else {
					twitterFeedListAdapter.addTweets(tweets);
					twitterFeedListAdapter.notifyDataSetChanged();
					ViewHelper.fadeIn(twitterFeedList);
				}
			} else {
				if (tweets == null) {
					MyApp.getInstance().showError(getActivity(),
							getResources().getString(R.string.errorNotSure));
				}
			}
		}

	}
}
