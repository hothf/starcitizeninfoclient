package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.BrowserContainer;
import de.kauz.starcitizen.informer.fragments.FanSites;
import de.kauz.starcitizen.informer.model.FanSite;
import de.kauz.starcitizen.informer.utils.DialogPopup;
import de.kauz.starcitizen.informer.utils.FansiteEditAddPopup;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom Adapter for a ListView with saving technology. Intended to display
 * FanSite objects.
 * 
 * @author MadKauz
 * 
 */
public class FanSitesListViewAdapter extends BaseAdapter {

	private ArrayList<FanSite> fanSites = new ArrayList<FanSite>();
	private LayoutInflater inflater;
	private Typeface font;
	private FanSites fragment;
	private Context context;

	/**
	 * Custom list-adapter for the rendering of certain fansite elements.
	 * 
	 * @param context
	 *            of the list
	 * @param font
	 *            of the text elements
	 * @param sites
	 *            the elements to render
	 * @param fanSites
	 *            the fragment this list is based on
	 */
	public FanSitesListViewAdapter(Context context, Typeface font,
			ArrayList<FanSite> sites, FanSites fanSites) {
		this.inflater = LayoutInflater.from(context);
		this.font = font;
		this.fanSites = sites;
		this.fragment = fanSites;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.fanSites.size();
	}

	@Override
	public FanSite getItem(int index) {
		return this.fanSites.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		convertView = null;

		if (convertView == null) {
			holder = new ViewHolder();
			if (fanSites.get(position).getSiteType() == FanSite.SITETYPE.TYPE_HEADER) {
				convertView = inflater.inflate(R.layout.list_fansite_header,
						null);
			} else {
				convertView = inflater
						.inflate(R.layout.list_fansite_item, null);
				holder.editSite = (ImageButton) convertView
						.findViewById(R.id.fansiteEdit);
			}
			holder.clickView = convertView.findViewById(R.id.fansiteClickView);
			holder.title = (TextView) convertView
					.findViewById(R.id.fansiteTitle);
			holder.image = (ImageView) convertView
					.findViewById(R.id.fansiteItemImage);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		if (fanSites.get(position).getSiteType() == FanSite.SITETYPE.TYPE_CONTENT) {
			holder.image.setImageResource(fanSites.get(position)
					.getImageResId());

			holder.clickView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(fragment.getActivity(),
							BrowserContainer.class);
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
							fanSites.get(position).getName());
					intent.putExtra(
							InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
							fanSites.get(position).getUrl());
					fragment.getActivity().startActivity(intent);
				}
			});

			holder.editSite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					PopupMenu popup = new PopupMenu(context, v);
					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
							case R.id.action_remove:
								DialogPopup dialog = new DialogPopup(context,
										fanSites.get(position), font, fragment);
								dialog.open();
								return true;
							case R.id.action_edit:
								FansiteEditAddPopup popup = new FansiteEditAddPopup(
										context, fragment,
										FansiteEditAddPopup.TYPE.TYPE_EDIT,
										font, fanSites.get(position));
								popup.open();
								return true;
							default:
								return false;
							}
						}
					});
					MenuInflater inflater = popup.getMenuInflater();
					inflater.inflate(R.menu.fansitepopmenu, popup.getMenu());
					popup.show();
				}
			});

		} else {
		}
		holder.title.setTypeface(font);
		holder.title.setText(fanSites.get(position).getName());
		return convertView;
	}

	/**
	 * ViewHolder pattern class, containing several views.
	 * 
	 * @author MadKauz
	 * 
	 */
	static class ViewHolder {
		TextView title;
		ImageView image;
		View clickView;
		ImageButton editSite;
	}

	/**
	 * Retrieves the news list.
	 * 
	 * @return the list
	 */
	public ArrayList<FanSite> getFanSites() {
		return this.fanSites;
	}

	/**
	 * Clears the list.
	 */
	public void clearFansites() {
		this.fanSites.clear();
	}

	/**
	 * Removes a specific FanSite from the list.
	 * 
	 * @param site
	 *            the site to be deleted
	 */
	public void removeFanSite(FanSite site) {
		this.fanSites.remove(site);
	}

	/**
	 * Adds a FanSite item to the list.
	 * 
	 * @param site
	 *            to add
	 */
	public void addFanSite(int position, FanSite site) {
		this.fanSites.add(position, site);
	}

	/**
	 * Sets the list items.
	 * 
	 * @param sites
	 *            to be set for the list.
	 */
	public void setFanSites(ArrayList<FanSite> sites) {
		this.fanSites = sites;
	}

}
