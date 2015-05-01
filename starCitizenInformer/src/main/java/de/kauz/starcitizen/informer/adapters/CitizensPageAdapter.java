package de.kauz.starcitizen.informer.adapters;

import de.kauz.starcitizen.informer.fragments.ForumsPageFragment;
import de.kauz.starcitizen.informer.fragments.SearchPageFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A ViewPager adapter for viewing different tabs: forums and search.
 * 
 * @author MadKauz
 * 
 */
public class CitizensPageAdapter extends FragmentStatePagerAdapter {

	private static final String[] titles = { "Search", "Forums" };

	public CitizensPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new SearchPageFragment();
		switch (i) {
		case 0:
			fragment = new SearchPageFragment();
			break;
		case 1:
			fragment = new ForumsPageFragment();
			break;
		default:
			break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

}
