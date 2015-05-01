package de.kauz.starcitizen.informer.fragments;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.CitizensPageAdapter;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Social View containing searches for orgas and players.
 * 
 * @author MadKauz
 * 
 */
public class Citizens extends Fragment {

	private ViewPager viewPager;
	private CitizensPageAdapter pageAdapter;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_citizens, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
	}

	/**
	 * LifeCycle Fragment onDestroyView(..)
	 */
	@Override
	public void onDestroyView() {
		removeTabs();
		super.onDestroyView();
	};

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		Main activity = (Main) getActivity();
		final ActionBar actionBar = activity.getSupportActionBar();

		pageAdapter = new CitizensPageAdapter(getChildFragmentManager());

		viewPager = (ViewPager) getView().findViewById(R.id.citizensPager);
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						actionBar.setSelectedNavigationItem(position);
					}
				});

		viewPager.setAdapter(pageAdapter);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}

			@Override
			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}
		};

		// adds the tabs
		for (int i = 0; i < pageAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(pageAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[6]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[6]);
	}

	/**
	 * Removes all tabs and reverts the actionbar to the standard behaviour
	 * without tabs.
	 */
	private void removeTabs() {
		Main activity = (Main) getActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

}
