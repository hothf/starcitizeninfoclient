package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Detail;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom List adapter to display additional news objects
 * 
 * @author MadKauz
 * 
 */
public class SectionsListViewAdapter extends BaseAdapter {

	private ArrayList<News> news = new ArrayList<News>();
	private LayoutInflater inflater;
	private Context context;
	private Typeface font;
	private ViewHolder holder;

	public SectionsListViewAdapter(Context context, Typeface font) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.font = font;
	}

	/**
	 * Adds a new newsTitle to the list. Does not add a news to the list, if the
	 * title of the fetched news is already in the list.
	 * 
	 * @param news
	 *            - the news to be added
	 */
	public void addOrRefreshNews(News news) {
		if (this.news.size() < 1) {
			this.news.add(news);
			notifyDataSetChanged();
			return;
		} else {
			for (News newNews : this.news) {
				if (newNews.getTitle().equals(news.getTitle())) {
					return;
				}
			}
			this.news.add(news);
			notifyDataSetChanged();
		}
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
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
		} else {
			if (convertView.getTag() != null) {
				holder = (ViewHolder) convertView.getTag();
			}
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
			holder.typeImage
					.setImageResource(R.drawable.rsi_circle_yelloworange);
			holder.type.setTextColor(context.getResources().getColor(
					R.color.yellowOrange));
		}
		holder.clickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, Detail.class);
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE, news
						.get(position).getTitle());
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
						news.get(position).getUrl());
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_COMMENTS_COUNT,
						"" + news.get(position).getCommentCount());
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_POSTED_TIME,
						news.get(position).getPostingTime());
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_TYPE,
						news.get(position).getType());
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_INFO,
						news.get(position).getAdditionalInfo());
				context.startActivity(intent);
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
	 * ViewHolder pattern class.
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
	 * Clears the List.
	 */
	public void clearList() {
		this.news.clear();
	}

}
