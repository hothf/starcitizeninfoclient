package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Detail;
import de.kauz.starcitizen.informer.fragments.NewsFeed;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom Adapter for a ListView with saving technology. Intended to display
 * News Objects.
 * 
 * @author MadKauz
 * 
 */
public class NewsFeedListViewAdapter extends BaseAdapter {

	private ArrayList<News> news = new ArrayList<News>();
	private LayoutInflater inflater;
	private Context context;
	private Typeface font;
	private NewsFeed newsFeed;

	/**
	 * Custom list-adapter to provide saving functionality and the unique
	 * rendering of certain news elements.
	 * 
	 * @param context
	 *            of the list
	 * @param font
	 *            of the text elements
	 */
	public NewsFeedListViewAdapter(Context context, Typeface font,
			NewsFeed newsFeed) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.font = font;
		this.newsFeed = newsFeed;
	}

	/**
	 * Adds news items to the news list.
	 * 
	 * @param ns
	 *            the news to add
	 */
	public void addNews(ArrayList<News> ns) {
		this.news.addAll(ns);
	}

	@Override
	public int getCount() {
		return this.news.size();
	}

	@Override
	public Object getItem(int index) {
		return this.news.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Retrieves the news list.
	 * 
	 * @return the news list
	 */
	public ArrayList<News> getNews() {
		return this.news;
	}

	/**
	 * Sets the news items for this adapter.
	 * 
	 * @param news
	 *            to set
	 */
	public void setNews(ArrayList<News> news) {
		this.news = news;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		int type = getItemViewType(position);

		switch (type) {
		case 0:
			// already read
			holder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.list_news_item_layout, null);
			holder.clickView = convertView
					.findViewById(R.id.newsListItemClickView);
			holder.type = (TextView) convertView
					.findViewById(R.id.newsListItemType);
			holder.title = (TextView) convertView
					.findViewById(R.id.newsListItemTitle);
			holder.typeImage = (ImageView) convertView
					.findViewById(R.id.newsListItemTypeImage);
			holder.favButton = (ImageButton) convertView
					.findViewById(R.id.newsListItemFavImageButton);
			holder.posted = (TextView) convertView
					.findViewById(R.id.newsListItemPosted);
			convertView.setTag(holder);

			final Favourite fav1 = new Favourite(news.get(position).getTitle(),
					Favourite.TYPE_NEWS, news.get(position).getUrl(), news.get(
							position).getAdditionalInfo(), news.get(position)
							.getPostingTime(), "");
			showRightFavImage(fav1, holder.favButton, false);
			holder.type.setTypeface(font);
			holder.title.setTypeface(font);
			holder.posted.setTypeface(font);
			holder.posted.setText(news.get(position).getPostingTime());
			holder.type.setText(news.get(position).getType());
			holder.title.setText(news.get(position).getTitle());
			if (holder.type.getText().equals("Transmission")) {
				holder.typeImage.setImageResource(R.drawable.rsi_circle_blue);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.blueCircleColor));
			} else if (holder.type.getText().equals("Slideshow")) {
				holder.typeImage.setImageResource(R.drawable.rsi_circle_green);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.greenCircleColor));
			} else {
				holder.type.setText(News.TYPE_NEWS);
				holder.typeImage
						.setImageResource(R.drawable.rsi_circle_yelloworange);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.yellowOrange));
			}
			holder.clickView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// start the detailed activity!
					Intent intent = new Intent(context, Detail.class);
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE, news
							.get(position).getTitle());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_LINK, news
							.get(position).getUrl());
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_COMMENTS_COUNT, ""
									+ news.get(position).getCommentCount());
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_POSTED_TIME, news
									.get(position).getPostingTime());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_INFO, news
							.get(position).getAdditionalInfo());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_TYPE, news
							.get(position).getType());
					context.startActivity(intent);
				}
			});

			holder.clickView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					newsFeed.openContext(position);
					return true;
				}
			});

			if (news.size() < 1) {
				holder.title.setText("Empty");
			}

			holder.favButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					ImageButton view = (ImageButton) v;
					showRightFavImage(fav1, view, true);
				}
			});

			return convertView;

		case 1:
			// new item
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.list_news_item_layout_new_item, null);
				holder.clickView = convertView
						.findViewById(R.id.newsListItemClickView);
				holder.type = (TextView) convertView
						.findViewById(R.id.newsListItemType);
				holder.title = (TextView) convertView
						.findViewById(R.id.newsListItemTitle);
				holder.typeImage = (ImageView) convertView
						.findViewById(R.id.newsListItemTypeImage);
				holder.favButton = (ImageButton) convertView
						.findViewById(R.id.newsListItemFavImageButton);
				holder.posted = (TextView) convertView
						.findViewById(R.id.newsListItemPosted);
				convertView.setTag(holder);
				// Log.e("TAG","HolderNEW: "+holder);
			} else {
				// Log.e("TAG","GET NEW: "+convertView.getTag());
				holder = (ViewHolder) convertView.getTag();
			}

			final Favourite fav = new Favourite(news.get(position).getTitle(),
					Favourite.TYPE_NEWS, news.get(position).getUrl(), news.get(
							position).getAdditionalInfo(), news.get(position)
							.getPostingTime(), "");
			showRightFavImage(fav, holder.favButton, false);
			holder.type.setTypeface(font);
			holder.title.setTypeface(font);
			holder.posted.setTypeface(font);
			holder.posted.setText(news.get(position).getPostingTime());
			holder.type.setText(news.get(position).getType());
			holder.title.setText(news.get(position).getTitle());
			if (holder.type.getText().equals("Transmission")) {
				holder.typeImage.setImageResource(R.drawable.rsi_circle_blue);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.blueCircleColor));
			} else if (holder.type.getText().equals("Slideshow")) {
				holder.typeImage.setImageResource(R.drawable.rsi_circle_green);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.greenCircleColor));
			} else {
				holder.type.setText(News.TYPE_NEWS);
				holder.typeImage
						.setImageResource(R.drawable.rsi_circle_yelloworange);
				holder.type.setTextColor(context.getResources().getColor(
						R.color.yellowOrange));
			}
			holder.clickView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// start the detailed activity!
					Intent intent = new Intent(context, Detail.class);
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE, news
							.get(position).getTitle());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_LINK, news
							.get(position).getUrl());
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_COMMENTS_COUNT, ""
									+ news.get(position).getCommentCount());
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_POSTED_TIME, news
									.get(position).getPostingTime());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_INFO, news
							.get(position).getAdditionalInfo());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_TYPE, news
							.get(position).getType());
					context.startActivity(intent);
				}
			});

			holder.clickView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					newsFeed.openContext(position);
					return true;
				}
			});

			if (news.size() < 1) {
				holder.title.setText("Empty");
			}

			holder.favButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					ImageButton view = (ImageButton) v;
					showRightFavImage(fav, view, true);
				}
			});

			return convertView;

		case 2:
			// load more items
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.list_load_more_item_layout, null);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;

		default:
			break;
		}
		return parent;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (news.get(position).getTitle().equals(News.TYPE_LOAD_MORE)) {
			return 2;
		} else {
			if (MyApp.getInstance().getAgent()
					.isNewsAlreadyRead(news.get(position))) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	/**
	 * Shows the intended image on a button for a favourite -> if already marked
	 * or not.
	 * 
	 * @param fav
	 *            the favourite
	 * @param button
	 *            to change the image for
	 */
	private void showRightFavImage(Favourite fav, ImageButton button,
			boolean canManipulate) {
		if (MyApp.getInstance().getAgent().isFavAlreadySaved(fav)) {
			if (canManipulate) {
				MyApp.getInstance().getAgent().deleteFavourite(fav);
				button.setImageResource(R.drawable.ic_fav_normal);
			} else {
				button.setImageResource(R.drawable.ic_fav_pressed);
			}

		} else {
			if (canManipulate) {
				MyApp.getInstance().getAgent().persistFavourite(fav);
				button.setImageResource(R.drawable.ic_fav_pressed);
			} else {
				button.setImageResource(R.drawable.ic_fav_normal);
			}
		}
	}

	/**
	 * ViewHolder pattern class, containing several views.
	 * 
	 * @author MadKauz
	 * 
	 */
	static class ViewHolder {
		View clickView;
		TextView title, type, posted;
		ImageView typeImage;
		ImageButton favButton;
	}

	/**
	 * Marks all news as read. In combination with an adapters invalidation of
	 * all views, this should lead to a different rendering style.
	 */
	public void markAllRead() {

		for (News n : news) {
			n.setStatus("2");
		}
		MyApp.getInstance().getAgent().setAllNewsAsRead();

		newsFeed.notifyMain();
	}

	/**
	 * Mark specified item as read.
	 * 
	 * @param requestedItemIndex
	 *            of the specified item in the list
	 */
	public void markItemAsRead(int requestedItemIndex) {
		News item = news.get(requestedItemIndex);
		item.setStatus("2");
		MyApp.getInstance().getAgent().updateNews(item);
		notifyDataSetChanged();
		newsFeed.notifyMain();
	}

	/**
	 * Clears the list.
	 */
	public void clearNews() {
		this.news.clear();
	}

	/**
	 * Removes the specified news item from the list.
	 * 
	 * @param n
	 *            the news to be removed
	 */
	public void removeLoadingItem(News n) {
		if (n.getTitle().equals(News.TYPE_LOAD_MORE)) {
			this.news.remove(n);
		}
	}

}
