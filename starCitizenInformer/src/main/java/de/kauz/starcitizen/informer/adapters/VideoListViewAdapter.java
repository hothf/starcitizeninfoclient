package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.ImageDownload.ImageDownloadListener;

/**
 * Custom Adapter for displaying RSS Items. In this case the rss data contains
 * an image and a description.
 * 
 * @author MadKauz
 * 
 */
public class VideoListViewAdapter extends BaseAdapter {

	private ArrayList<RssItem> items = new ArrayList<RssItem>();
	private Context context;
	private Typeface font;

	public VideoListViewAdapter(Context context, ArrayList<RssItem> items,
			Typeface font) {
		this.items = items;
		this.context = context;
		this.font = font;
	}

	@Override
	public int getCount() {
		return items.size() - 1;
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.list_video_item, null);
			holder = new ViewHolder();
			holder.itemTitle = (TextView) convertView
					.findViewById(R.id.rssItemTitle);
			holder.itemImage = (ImageView) convertView
					.findViewById(R.id.rssItemImage);
			holder.clickView = convertView.findViewById(R.id.rssClickView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemTitle.setTypeface(font);
		holder.itemTitle.setText(items.get(position).getTitle());

		if (items.get(position).getImg() == null) {
			if (items.get(position).getImgUrl() != null) {
				if (!items.get(position).isAlreadyDownloading()) {
					items.get(position).setAlreadyDownloading(true);
					ImageDownload download = new ImageDownload(context, null,
							holder.itemImage);
					download.setOnImageDownloadListener(new ImageDownloadListener() {

						@Override
						public void onImageDownloadComplete(Bitmap result) {
							items.get(position).setImg(result);
						}
					});
					download.execute(items.get(position).getImgUrl());
				}
			}
		} else {
			holder.itemImage.setImageBitmap(items.get(position).getImg());
		}

		holder.clickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Uri uri = Uri.parse(items.get(position + 1).getLink());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intent);
			}
		});

		return convertView;

	}

	/**
	 * Viewholder pattern.
	 * 
	 * @author MadKauz
	 * 
	 */
	static class ViewHolder {
		TextView itemTitle;
		ImageView itemImage;
		View clickView;
	}
}
