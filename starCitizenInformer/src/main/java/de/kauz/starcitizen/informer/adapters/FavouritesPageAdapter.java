package de.kauz.starcitizen.informer.adapters;

import de.kauz.starcitizen.informer.fragments.FanSites;
import de.kauz.starcitizen.informer.fragments.FavouritesNewsFragment;
import de.kauz.starcitizen.informer.fragments.FavouritesOrgsFragment;
import de.kauz.starcitizen.informer.fragments.FavouritesPlayerFragment;
import de.kauz.starcitizen.informer.fragments.SearchPageFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A ViewPager adapter for viewing different tabs containung news, players,
 * organizations and fansites.
 * 
 * @author MadKauz
 * 
 */
public class FavouritesPageAdapter extends FragmentStatePagerAdapter {

	private static final String[] titles = { "News", "Players", "Orgs",
			"Fan Sites" };

	public FavouritesPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new SearchPageFragment();
		switch (i) {
		case 0:
			fragment = new FavouritesNewsFragment();
			break;
		case 1:
			fragment = new FavouritesPlayerFragment();
			break;
		case 2:
			fragment = new FavouritesOrgsFragment();
			break;
		case 3:
			fragment = new FanSites();
			break;
		default:
			break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

}
