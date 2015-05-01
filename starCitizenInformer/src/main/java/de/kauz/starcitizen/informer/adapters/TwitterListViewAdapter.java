package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.BrowserContainer;
import de.kauz.starcitizen.informer.model.Tweet;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Custom List adapter for displaying twitter feeds.
 * 
 * @author MadKauz
 * 
 */
public class TwitterListViewAdapter extends BaseAdapter {

	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	private LayoutInflater inflater;
	private Context context;
	private Typeface font;

	public TwitterListViewAdapter(Context context, Typeface font) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.font = font;
	}

	@Override
	public int getCount() {
		return this.tweets.size();
	}

	@Override
	public Object getItem(int index) {
		return this.tweets.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_tweet_item_layout,
					null);
			holder.clickView = convertView
					.findViewById(R.id.twitterListItemClickView);
			holder.title = (TextView) convertView
					.findViewById(R.id.twitterListItemTitle);
			holder.comments = (TextView) convertView
					.findViewById(R.id.twitterListItemComments);
			holder.posted = (TextView) convertView
					.findViewById(R.id.twitterListItemPosted);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setTypeface(font);
		holder.comments.setTypeface(font);
		holder.posted.setTypeface(font);
		holder.comments.setText("" + tweets.get(position).getTweetcount());
		holder.posted.setText(tweets.get(position).getData());
		holder.title.setText(tweets.get(position).getMessage());

		holder.clickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(context, BrowserContainer.class);
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
							"Tweet " + (position + 1));
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
							tweets.get(position).getUrl());
					context.startActivity(intent);

				} catch (NullPointerException e) {
					MyApp.getInstance().showError(
							context,
							context.getResources().getString(
									R.string.errorBadLink));
				}
			}
		});

		return convertView;
	}

	/**
	 * ViewHolder pattern class.
	 * 
	 * @author MadKauz
	 * 
	 */
	static class ViewHolder {
		View clickView;
		TextView title, posted, comments;
	}

	/**
	 * Clears the list.
	 */
	public void clearList() {
		this.tweets.clear();
	}

	/**
	 * Adds tweets to the list.
	 * 
	 * @param tweets
	 *            the tweets to add
	 */
	public void addTweets(ArrayList<Tweet> tweets) {
		this.tweets = tweets;

	}

}
