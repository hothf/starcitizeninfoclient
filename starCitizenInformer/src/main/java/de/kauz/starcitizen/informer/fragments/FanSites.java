package de.kauz.starcitizen.informer.fragments;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.FanSitesListViewAdapter;
import de.kauz.starcitizen.informer.databases.FanSitesDBContentprovider;
import de.kauz.starcitizen.informer.databases.FanSitesDBContract;
import de.kauz.starcitizen.informer.model.FanSite;
import de.kauz.starcitizen.informer.utils.FansiteEditAddPopup;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * View for showing fan content such as fan sites, wikis and custom made star.
 * maps.
 * 
 * @author MadKauz
 * 
 */
public class FanSites extends Fragment {

	private ListView list;
	private TextView disclaimText;
	private FanSitesListViewAdapter adapter;

	private Typeface font;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_fansites, container, false);
	}
	

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.list = (ListView) getView().findViewById(R.id.fanSitesListView);
		this.disclaimText = (TextView) getView().findViewById(R.id.fansiteDisclaim);
		
		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.disclaimText.setTypeface(font);
		

		this.adapter = new FanSitesListViewAdapter(getActivity(), font,
				fetchAllFanSites(), this);
		this.list.setAdapter(adapter);

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[8]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[8]);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		Fragment frag;
		if (menuVisible){
			frag = FanSites.this;
		} else {
			frag = new Fragment();
		}
		MyApp.getInstance().setCurrentFragment(frag);
		if (getActivity() != null){
			getActivity().supportInvalidateOptionsMenu();
		}
		super.setMenuVisibility(menuVisible);
	}
	
	/**
	 * Creates pre-defined header sections of fansites.
	 * 
	 * @return fansites
	 */
	private ArrayList<FanSite> getHeaders() {
		ArrayList<FanSite> fansites = new ArrayList<FanSite>();
		FanSite fanSiteHeader1 = new FanSite("Wikis", "",
				FanSite.SITETYPE.TYPE_HEADER, FanSite.CATEGORIES[0]);
		FanSite fanSiteHeader2 = new FanSite("Sites", "",
				FanSite.SITETYPE.TYPE_HEADER, FanSite.CATEGORIES[1]);
		FanSite fanSiteHeader3 = new FanSite("Tools", "",
				FanSite.SITETYPE.TYPE_HEADER, FanSite.CATEGORIES[2]);
		FanSite fanSiteHeader4 = new FanSite("Other", "",
				FanSite.SITETYPE.TYPE_HEADER, FanSite.CATEGORIES[3]);
		fansites.add(fanSiteHeader1);
		fansites.add(fanSiteHeader2);
		fansites.add(fanSiteHeader3);
		fansites.add(fanSiteHeader4);
		return fansites;
	}

	/**
	 * Fetches all available static and non-static (pre-created) fansites.
	 * 
	 * @return
	 */
	private ArrayList<FanSite> fetchAllFanSites() {

		ArrayList<FanSite> fansites = getHeaders();

		ContentResolver resolver = getActivity().getContentResolver();
		Uri contentUri = FanSitesDBContentprovider.CONTENT_URI;
		String[] projection = FanSitesDBContract.DataBaseEntry.available;
		Cursor cursor = resolver
				.query(contentUri, projection, null, null, null);
		int size = cursor.getCount();

		// populate arrayList with sites already stored
		if (size > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < size; i++) {
				FanSite fansite = new FanSite(cursor.getString(2),
						cursor.getString(3), FanSite.SITETYPE.TYPE_CONTENT,
						cursor.getString(4));
				fansites.add(fansite);
				cursor.moveToNext();
			}
			cursor.close();
		} else {
			// if not one entry exists, create the table
			fansites.addAll(createNewTable());
		}
		cursor.close();

		return sortList(fansites);
	}

	/**
	 * Creates static content and stores it into the db.
	 */
	private ArrayList<FanSite> createNewTable() {
		ArrayList<FanSite> sites = new ArrayList<FanSite>();

		FanSite fanSite1 = new FanSite("Example Wiki (English Wiki)",
				"http://starcitizen.wikia.com/wiki/Main_Page",
				FanSite.SITETYPE.TYPE_CONTENT, FanSite.CATEGORIES[0]);
		sites.add(fanSite1);
		FanSite fanSite3 = new FanSite("Example Fan Site (SC Base)",
				"http://forums.starcitizenbase.com/",
				FanSite.SITETYPE.TYPE_CONTENT, FanSite.CATEGORIES[1]);
		sites.add(fanSite3);
		FanSite fanSite2 = new FanSite("Example Tool (StarMap)",
				"http://starcitizen.mojoworld.com/StarMap/",
				FanSite.SITETYPE.TYPE_CONTENT, FanSite.CATEGORIES[2]);
		sites.add(fanSite2);
		FanSite fanSite4 = new FanSite("Example Misc Site (HorizonRadio) ",
				"http://www.beyondthehorizonradio.com/",
				FanSite.SITETYPE.TYPE_CONTENT, FanSite.CATEGORIES[3]);
		sites.add(fanSite4);

		ContentResolver resolver = getActivity().getContentResolver();
		Uri contentUri = FanSitesDBContentprovider.CONTENT_URI;

		ContentValues[] vals = new ContentValues[sites.size()];
		int i = 0;
		for (FanSite set : sites) {
			ContentValues values = new ContentValues();
			values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME,
					set.getName());
			values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_URL,
					set.getUrl());
			values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_CATEGORY,
					set.getCategory());
			vals[i] = values;
			i++;
		}
		resolver.bulkInsert(contentUri, vals);

		return sites;
	}

	/**
	 * Stores the specified fansite in the db.
	 * 
	 * @param site
	 */
	private void storeFanSiteInDB(FanSite site) {
		ContentResolver resolver = getActivity().getContentResolver();
		Uri contentUri = FanSitesDBContentprovider.CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME,
				site.getName());
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_URL,
				site.getUrl());
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_CATEGORY, ""
				+ site.getCategory());
		resolver.insert(contentUri, values);
	}

	/**
	 * Removes a Fansite from the db.
	 * 
	 * @param site
	 */
	private void removeFanSiteFromDB(FanSite site) {
		ContentResolver resolver = getActivity().getContentResolver();
		Uri contentUri = FanSitesDBContentprovider.CONTENT_URI;
		String where = FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME + "="
				+ "'" + site.getName() + "'";
		resolver.delete(contentUri, where, null);
	}

	/**
	 * Updates the specified fansite in the db.
	 * 
	 * @param site
	 * @param oldName
	 *            the name to identify the site with in the db
	 */
	private void updateFanSiteInDB(FanSite site, String oldName) {
		ContentResolver resolver = getActivity().getContentResolver();
		Uri contentUri = FanSitesDBContentprovider.CONTENT_URI;
		String where = FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME + "="
				+ "'" + oldName + "'";
		ContentValues values = new ContentValues();
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME,
				site.getName());
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_URL,
				site.getUrl());
		values.put(FanSitesDBContract.DataBaseEntry.COLUMN_NAME_CATEGORY, ""
				+ site.getCategory());
		resolver.update(contentUri, values, where, null);

	}

	/**
	 * Edits the specified fansite in the list
	 * 
	 * @param fansite
	 *            param oldName name of the site prior to editing
	 */
	public void editFanSite(FanSite fansite, String oldName) {
		updateFanSiteInDB(fansite, oldName);

		ArrayList<FanSite> fansites = fetchAllFanSites();
		adapter.clearFansites();
		adapter.setFanSites(sortList(fansites));
		adapter.notifyDataSetChanged();
	}

	/**
	 * Adds a new Fansite to the list and db.
	 * 
	 * @param fansite
	 */
	public void addFanSite(FanSite fansite) {
		storeFanSiteInDB(fansite);

		int firstFreeIndexForSelectedCat = 0;

		for (int j = adapter.getFanSites().size() - 1; j > 0; j--) {
			if (adapter.getFanSites().get(j).getCategory()
					.equals(fansite.getCategory())) {
				firstFreeIndexForSelectedCat = j;
				break;
			}
		}
		adapter.addFanSite(firstFreeIndexForSelectedCat + 1, fansite);
		adapter.notifyDataSetChanged();
	}

	/**
	 * Removes the specified fansite from the list and db.
	 * 
	 * @param fansite
	 */
	public void removeFanSite(FanSite fansite) {
		removeFanSiteFromDB(fansite);
		adapter.removeFanSite(fansite);
		adapter.notifyDataSetChanged();
	}

	/**
	 * Shows the dialog for adding a new fansite
	 */
	public void showAddSite() {
		FansiteEditAddPopup popup = new FansiteEditAddPopup(getActivity(),
				this, FansiteEditAddPopup.TYPE.TYPE_ADD, font, null);
		popup.open();
	}

	/**
	 * Sorts the list in that manner, that all entries containing the same
	 * category are neighbours.
	 * 
	 * @param list
	 *            to sort
	 * @return the sorted list
	 */
	private ArrayList<FanSite> sortList(ArrayList<FanSite> list) {
		ArrayList<FanSite> sortedList = new ArrayList<FanSite>();

		for (int k = 0; k < FanSite.CATEGORIES.length; k++) {
			for (FanSite site : list) {
				if (site.getCategory().equals(FanSite.CATEGORIES[k])) {
					sortedList.add(site);
				}
			}
		}
		return sortedList;
	}

}
