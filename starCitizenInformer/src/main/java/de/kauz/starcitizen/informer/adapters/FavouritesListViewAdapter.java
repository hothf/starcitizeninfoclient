package de.kauz.starcitizen.informer.adapters;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Detail;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.activities.OrgaInspect;
import de.kauz.starcitizen.informer.activities.PlayerInspect;
import de.kauz.starcitizen.informer.fragments.FavouritesNewsFragment;
import de.kauz.starcitizen.informer.fragments.FavouritesOrgsFragment;
import de.kauz.starcitizen.informer.fragments.FavouritesPlayerFragment;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
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
 * Custom Adapter for a ListView. Intended to display Favourites objects.
 * 
 * @author MadKauz
 * 
 */
public class FavouritesListViewAdapter extends BaseAdapter {

	private ArrayList<Favourite> favs = new ArrayList<Favourite>();
	private LayoutInflater inflater;
	private Context context;
	private Typeface font;
	private Fragment fragment;

	/**
	 * Custom list-adapter to provide rendering of certain favourites elements.
	 * 
	 * @param context
	 *            of the list
	 * @param font
	 *            of the textelements
	 */
	public FavouritesListViewAdapter(Main context, Typeface font,
			ArrayList<Favourite> favourites, Fragment fragment) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.font = font;
		this.favs = favourites;
		this.fragment = fragment;
	}

	/**
	 * Adds news items to the list.
	 * 
	 * @param fs
	 *            the item to add
	 */
	public void addFavourites(ArrayList<Favourite> fs) {
		this.favs.addAll(fs);
	}

	@Override
	public int getCount() {
		return this.favs.size();
	}

	@Override
	public Object getItem(int index) {
		return this.favs.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Retrieves the Favourites list.
	 * 
	 * @return the Favourites list
	 */
	public ArrayList<Favourite> getFavourites() {
		return this.favs;
	}

	/**
	 * Sets the Favourites items for this adapter.
	 * 
	 * @param Favourites
	 *            to set
	 */
	public void setFavourites(ArrayList<Favourite> favourites) {
		this.favs = favourites;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();

			convertView = inflater.inflate(
					R.layout.list_favourites_item_layout, null);

			holder.clickView = convertView
					.findViewById(R.id.favouritesItemClickView);
			holder.type = (TextView) convertView
					.findViewById(R.id.favouritesItemType);
			holder.title = (TextView) convertView
					.findViewById(R.id.favouritesItemTitle);
			holder.posted = (TextView) convertView
					.findViewById(R.id.favouritesItemPosted);
			holder.postedImg = (ImageView) convertView
					.findViewById(R.id.favouritesItemPostedImage);
			holder.removeButton = (ImageButton) convertView
					.findViewById(R.id.favouritesItemClose);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.type.setTypeface(font);
		holder.title.setTypeface(font);
		holder.posted.setTypeface(font);
		holder.title.setText(favs.get(position).getTitle());

		final String type = favs.get(position).getType();

		if (type.equals("news")) {
			holder.type.setText("NEWS");
			holder.type.setTextColor(context.getResources().getColor(
					R.color.blueCircleColor));
			holder.posted.setText(context.getResources().getString(
					R.string.favouriteSavedPrefix)
					+ " " + favs.get(position).getDate());
			holder.postedImg.setVisibility(View.VISIBLE);
		} else if (type.equals("players")) {
			holder.type.setText("PLAYER");
			holder.type.setTextColor(context.getResources().getColor(
					R.color.greenCircleColor));
			holder.posted.setText(context.getResources().getString(
					R.string.favouriteSavedPrefix)
					+ " " + favs.get(position).getDate());
			holder.postedImg.setVisibility(View.VISIBLE);
		} else if (type.equals("orgs")) {
			holder.type.setText("ORGANIZATION");
			holder.type.setTextColor(context.getResources().getColor(
					R.color.yellowOrange));
			holder.posted.setText(context.getResources().getString(
					R.string.favouriteSavedPrefix)
					+ " " + favs.get(position).getDate());
			holder.postedImg.setVisibility(View.VISIBLE);
		}

		holder.clickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (type.equals("news")) {
					// open news site
					News news = new News(favs.get(position).getUrl(), favs.get(
							position).getTitle(), "", 0, favs.get(position)
							.getDate(), favs.get(position).getDescription());

					Intent intent = new Intent(context, Detail.class);
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							news.getTitle());
					intent.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							news.getUrl());
					context.startActivity(intent);
				} else if (type.equals("players")) {
					// open players site
					Intent intent = new Intent(context, PlayerInspect.class);
					String url = InformerConstants.URL_CITIZENS
							+ favs.get(position).getTitle();
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_PLAYER_SEARCH_LINK,
							url);
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_PLAYER_HANDLE, favs
									.get(position).getTitle());
					context.startActivity(intent);
				} else if (type.equals("orgs")) {
					// open orgs site
					Intent intent = new Intent(context, OrgaInspect.class);
					String url = InformerConstants.URL_ORGS
							+ favs.get(position).getTitle();
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_ORGA_SEARCH_LINK,
							url);
					intent.putExtra(
							InformerConstants.DETAIL_EXTRAS_ORGA_HANLDE, favs
									.get(position).getTitle());
					context.startActivity(intent);
				}

			}
		});

		holder.removeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApp.getInstance()
						.getAgent()
						.deleteFavouriteWithDialog(
								context,
								favs.get(position),
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										MyApp.getInstance()
												.getAgent()
												.deleteFavourite(
														favs.get(position));
										favs.remove(position);
										notifyDataSetChanged();
										dialog.dismiss();
									}
								},
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

				if (type.equals("news")) {
					FavouritesNewsFragment frag = (FavouritesNewsFragment) fragment;
					frag.showIfEmpty();
				} else if (type.equals("players")) {
					FavouritesPlayerFragment frag = (FavouritesPlayerFragment) fragment;
					frag.showIfEmpty();
				} else if (type.equals("orgs")) {
					FavouritesOrgsFragment frag = (FavouritesOrgsFragment) fragment;
					frag.showIfEmpty();
				}

			}
		});

		holder.clickView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});

		if (favs.size() < 1) {
			holder.title.setText("Empty");
		}

		return convertView;
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
		ImageView postedImg;
		ImageButton removeButton;
	}

	/**
	 * Clears the list.
	 */
	public void clearFavourites() {
		this.favs.clear();
	}

}
