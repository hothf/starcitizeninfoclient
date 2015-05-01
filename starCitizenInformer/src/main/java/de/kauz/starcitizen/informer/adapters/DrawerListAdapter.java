package de.kauz.starcitizen.informer.adapters;

import de.kauz.starcitizen.informer.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ListAdapter for showing drawer items.
 * 
 * @author MadKauz
 * 
 */
public class DrawerListAdapter extends BaseAdapter {

	private Typeface font;
	private LayoutInflater mInflater;
	private int[] ImageResIds, headerPositions;
	private boolean[] selectors;
	private int[] counters;

	private int layoutResId;
	private String[] titles;

	/**
	 * Creates the adapter upon a context with a layout, images, titles and
	 * headers.
	 * 
	 * @param context
	 *            the context
	 * @param layoutResId
	 *            the layout
	 * @param ImageResIds
	 *            the images
	 * @param titles
	 *            the titles
	 * @param headerPositions
	 *            the positions of the headers
	 */
	public DrawerListAdapter(Context context, int layoutResId,
			int[] ImageResIds, String[] titles, int[] headerPositions,
			Typeface font) {
		mInflater = LayoutInflater.from(context);
		this.layoutResId = layoutResId;
		this.ImageResIds = ImageResIds;
		this.titles = titles;
		this.headerPositions = headerPositions;
		this.font = font;

		this.selectors = new boolean[ImageResIds.length];
		unsetSelectors();

		this.counters = new int[ImageResIds.length];
		unsetCounters();
	}

	/**
	 * Gets the count of elements.
	 */
	@Override
	public int getCount() {
		return titles.length;
	}

	/**
	 * Gets one element.
	 */
	@Override
	public Object getItem(int position) {
		return titles[position];
	}

	/**
	 * Gets the id of an element.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Creates the visual list.
	 */
	@Override
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if (type == 0) {
				// header
				convertView = mInflater.inflate(
						R.layout.drawer_list_header_layout, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.drawerItemTitle);
				holder.selector = convertView
						.findViewById(R.id.drawerItemSelector);
			} else if (type == 1) {
				// normal item
				convertView = mInflater.inflate(layoutResId, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.drawerItemTitle);
				holder.image = (ImageView) convertView
						.findViewById(R.id.drawerItemImage);
				holder.selector = convertView
						.findViewById(R.id.drawerItemSelector);
				holder.counter = (TextView) convertView
						.findViewById(R.id.drawerCounterText);
				holder.counterBack = convertView.findViewById(R.id.drawerCounterBack);
			}
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setTypeface(font);
		holder.title.setText(titles[position]);
		if (selectors[position]) {
			holder.selector.setVisibility(View.VISIBLE);
		} else {
			holder.selector.setVisibility(View.INVISIBLE);
		}
		if (type == 1) {
			holder.image.setImageResource(ImageResIds[position]);
			holder.counter.setTypeface(font);
			if (counters[position] > 0) {
				holder.counterBack.setVisibility(View.VISIBLE);
				holder.counter.setText("" + counters[position]);
			} else {
				holder.counterBack.setVisibility(View.GONE);
				holder.counter.setText("");
			}
		}

		return convertView;
	}

	/**
	 * Gets the ItemViewType.
	 */
	@Override
	public int getItemViewType(int position) {
		int type = 1;
		for (int j = 0; j < headerPositions.length; j++) {
			if (position == headerPositions[j]) {
				return 0;
			} else {
				type = 1;
			}
		}
		return type;
	}

	/**
	 * Gets the ViewTypeCount.
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/**
	 * ViewHolder pattern class.
	 * 
	 * @author MadKauz
	 * 
	 */
	private static class ViewHolder {
		public TextView title, counter;
		public ImageView image;
		public View selector, counterBack;
	}

	/**
	 * Sets the font of the texts in this adapter.
	 * 
	 * @param font
	 */
	public void setAdapterfont(Typeface font) {
		this.font = font;
	}

	/**
	 * sets a single selector to true.
	 * 
	 * @param pos position to set to true
	 */
	public void setSelector(int pos) {
		unsetSelectors();
		this.selectors[pos] = true;
	}

	private void unsetSelectors() {
		for (int i = 0; i < selectors.length; i++) {
			selectors[i] = false;
		}
	}

	/**
	 * Sets a single counter value.
	 * 
	 * @param pos
	 *            the postion to set the value
	 * @param val
	 *            the value to set
	 */
	public void setCounter(int pos, int val) {
		this.counters[pos] = val;
	}

	private void unsetCounters() {
		for (int i = 0; i < counters.length; i++) {
			counters[i] = 0;
		}
	}
}
