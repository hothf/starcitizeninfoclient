package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.BrowserContainer;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.utils.InformerConstants;

/**
 * Custom Adapter for displaying RSS Items. In this case the rss data contains
 * an image and a description.
 * 
 * @author MadKauz
 * 
 */
public class ForumListViewAdapter extends BaseAdapter {

	private ArrayList<RssItem> items = new ArrayList<RssItem>();
	private Context context;
	private Typeface font;

	public ForumListViewAdapter(Context context, ArrayList<RssItem> items,
			Typeface font) {
		this.items = items;
		this.context = context;
		this.font = font;
	}

	@Override
	public int getCount() {
		return items.size();
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
			convertView = View.inflate(context, R.layout.list_forum_item, null);
			holder = new ViewHolder();
			holder.itemTitle = (TextView) convertView
					.findViewById(R.id.rssItemTitle);
			holder.clickView = convertView.findViewById(R.id.rssClickView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemTitle.setTypeface(font);
		holder.itemTitle.setText(items.get(position).getTitle());

		holder.clickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BrowserContainer.class);
				intent.putExtra(InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
						items.get(position).getTitle());
				intent.putExtra(InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
						items.get(position).getLink());
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	/**
	 * Container with a text.
	 * 
	 * @author MadKauz
	 * 
	 */
	static class ViewHolder {
		TextView itemTitle;
		View clickView;
	}

}
