package de.kauz.starcitizen.informer.fragments;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.FavouritesListViewAdapter;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * View for showing favourites stored in a db.
 * 
 * @author MadKauz
 * 
 */
public class FavouritesOrgsFragment extends Fragment {
	private ListView list;
	private FavouritesListViewAdapter adapter;
	private Typeface font;
	private TextView emptyText;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_favs_orgs, container, false);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		this.list = (ListView) getView().findViewById(R.id.favouritesListView);
		this.emptyText = (TextView) getView().findViewById(R.id.favouritesEmptyText);
		
		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		
		this.emptyText.setTypeface(font);

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[8]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[8]);

	}
	
	@Override
	public void onResume() {
		this.adapter = new FavouritesListViewAdapter((Main)getActivity(), font, MyApp
				.getInstance().getAgent()
				.fetchFavouritesTable(Favourite.TYPE_ORGS), this);
		this.list.setAdapter(adapter);
		showIfEmpty();
		super.onResume();
	}
	
	/**
	 * Shows an empty text.
	 */
	public void showIfEmpty() {
		if (adapter.getCount() < 1) {
			emptyText.setText(getResources().getString(
					R.string.favouriteEmptyTextOrgs));
			emptyText.setVisibility(View.VISIBLE);
		}
	}

}
