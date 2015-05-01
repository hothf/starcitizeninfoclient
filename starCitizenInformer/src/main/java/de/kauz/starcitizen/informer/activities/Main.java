package de.kauz.starcitizen.informer.activities;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.adapters.DrawerListAdapter;
import de.kauz.starcitizen.informer.fragments.About;
import de.kauz.starcitizen.informer.fragments.Citizens;
import de.kauz.starcitizen.informer.fragments.FanSites;
import de.kauz.starcitizen.informer.fragments.Favourites;
import de.kauz.starcitizen.informer.fragments.NewsFeed;
import de.kauz.starcitizen.informer.fragments.Ships;
import de.kauz.starcitizen.informer.fragments.VideoFeed;
import de.kauz.starcitizen.informer.fragments.Sections;
import de.kauz.starcitizen.informer.fragments.TwitterFeed;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Main view containing a list of recent news. Also offers a navigation drawer.
 * 
 * @author MadKauz
 * 
 */
@SuppressWarnings("deprecation")
public class Main extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private DrawerListAdapter drawerAdapter;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		Typeface font = Typeface.createFromAsset(getAssets(),
				"Electrolize-Regular.ttf");

		drawerAdapter = new DrawerListAdapter(this,
				R.layout.drawer_list_item_layout, InformerConstants.MENU_ICONS,
				InformerConstants.MENU_ITEMS, InformerConstants.MENU_HEADERS,
				font);
		mDrawerList.setAdapter(drawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.drawer, R.string.appName, R.string.appName) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				drawerAdapter
						.setSelector(getCurrentFragmentMenuPositionForSelector());
				mDrawerList.invalidateViews();
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setIcon(R.drawable.ic_fansites);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.getBoolean(InformerConstants.EXTRAS_PUSH_HAS_NEW_NEWS)) {
				NewsFeed fragment = new NewsFeed();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
		} else {
			Fragment fragment = new NewsFeed();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Prepares the options menu.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		searchView = (android.support.v7.widget.SearchView) MenuItemCompat
				.getActionView(menu.findItem(R.id.action_search));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				NewsFeed newsFeed = (NewsFeed) MyApp.getInstance()
						.getCurrentFragment();
				newsFeed.queryArgument(arg0);
				searchView.onActionViewCollapsed();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});

		if (MyApp.getInstance().getCurrentFragment() != null) {
			boolean isOnNewsFeed = MyApp.getInstance().getCurrentFragment()
					.getClass().equals(NewsFeed.class);
			boolean isOnTwitterFeed = MyApp.getInstance().getCurrentFragment()
					.getClass().equals(TwitterFeed.class);
			boolean isOnVideos = MyApp.getInstance().getCurrentFragment()
					.getClass().equals(VideoFeed.class);
			boolean isOnFansite = MyApp.getInstance().getCurrentFragment()
					.getClass().equals(FanSites.class);
			boolean isOnShips = MyApp.getInstance().getCurrentFragment()
					.getClass().equals(Ships.class);
			menu.findItem(R.id.action_refresh).setVisible(isOnNewsFeed);
			menu.findItem(R.id.action_select_all_as_read).setVisible(
					isOnNewsFeed);
			// The search function is currently disabled:
			menu.findItem(R.id.action_search).setVisible(false);
			menu.findItem(R.id.action_refresh_twitter).setVisible(
					isOnTwitterFeed);
			menu.findItem(R.id.action_refresh_videos).setVisible(isOnVideos);
			menu.findItem(R.id.action_addFansite).setVisible(isOnFansite);
			menu.findItem(R.id.action_pledge).setVisible(isOnShips);
			menu.findItem(R.id.action_voyager).setVisible(isOnShips);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_refresh:
			NewsFeed fragment1 = (NewsFeed) MyApp.getInstance()
					.getCurrentFragment();
			if (fragment1 != null) {
				fragment1.fetchInfo();
			}
			return true;
		case R.id.action_select_all_as_read:
			NewsFeed fragment2 = (NewsFeed) MyApp.getInstance()
					.getCurrentFragment();
			if (fragment2 != null) {
				fragment2.markAllRead();
			}
			return true;
		case R.id.action_refresh_twitter:
			TwitterFeed fragment3 = (TwitterFeed) MyApp.getInstance()
					.getCurrentFragment();
			if (fragment3 != null) {
				fragment3.fetchTweets();
			}
			return true;
		case R.id.action_refresh_videos:
			VideoFeed fragment4 = (VideoFeed) MyApp.getInstance()
					.getCurrentFragment();
			if (fragment4 != null) {
				fragment4.refreshManually();
			}
			return true;
		case R.id.action_addFansite:
			FanSites fragment5 = (FanSites) MyApp.getInstance()
					.getCurrentFragment();
			if (fragment5 != null) {
				fragment5.showAddSite();
			}
			return true;
		case R.id.action_voyager:
			Intent voyagerIntent = new Intent(this, BrowserContainer.class);
			voyagerIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
					getResources().getString(R.string.voyager));
			voyagerIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
					InformerConstants.URL_VOYAGER_DIRECT_SORE);
			startActivity(voyagerIntent);
			return true;
		case R.id.action_pledge:
			Intent pledgeIntent = new Intent(this, BrowserContainer.class);
			pledgeIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
					getResources().getString(R.string.pledge));
			pledgeIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
					InformerConstants.URL_PLEDGE_STORE);
			startActivity(pledgeIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		supportInvalidateOptionsMenu();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/**
	 * Selects a item from the drawer.
	 * 
	 * @param position
	 *            of the selection
	 */
	private void selectItem(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment desiredFragment = null;
		switch (position) {
		case 0:
			// header 1
			return;
		case 1:
			// newsfeed
			desiredFragment = new NewsFeed();
			break;
		case 2:
			// last sections
			desiredFragment = new Sections();
			break;
		case 3:
			// videos
			desiredFragment = new VideoFeed();
			break;
		case 4:
			// tweets
			desiredFragment = new TwitterFeed();
			break;
		case 5:
			// header 2
			return;
		case 6:
			// citizens
			desiredFragment = new Citizens();
			break;
		case 7:
			// ships
			desiredFragment = new Ships();
			break;
		case 8:
			// fan sites
			desiredFragment = new Favourites();
			break;
		case 9:
			// settings
			Intent intentSettings = new Intent(Main.this, Settings.class);
			startActivity(intentSettings);
			break;
		case 10:
			// header 3
			return;
		case 11:
			// About
			desiredFragment = new About();
			break;
		case 12:
			// RSI
			Intent browserIntent = new Intent(this, BrowserContainer.class);
			browserIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME,
					InformerConstants.MENU_ITEMS[12]);
			browserIntent.putExtra(
					InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL,
					InformerConstants.URL_MAIN_HOMEPAGE);
			startActivity(browserIntent);
			break;

		default:
			break;
		}
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		if (desiredFragment != null) {
			if (MyApp.getInstance().getCurrentFragment() != null) {
				Fragment currentFragment = MyApp.getInstance()
						.getCurrentFragment();
				if (currentFragment.getClass() != desiredFragment.getClass()) {
					if (desiredFragment instanceof NewsFeed) {
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, desiredFragment)
								.commit();
					} else {
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, desiredFragment)
								.addToBackStack("" + position).commit();
					}

				}
			} else {
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, desiredFragment)
						.addToBackStack("" + position).commit();
			}
		}
	}

	/**
	 * Retrieve the position of the current fragment in the menu.
	 * 
	 * @return the position
	 */
	private int getCurrentFragmentMenuPositionForSelector() {

		if (MyApp.getInstance().getCurrentFragment() != null) {

			if (MyApp.getInstance().getCurrentFragment() instanceof NewsFeed) {
				return 1;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof Sections) {
				return 2;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof VideoFeed) {
				return 3;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof TwitterFeed) {
				return 4;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof Citizens) {
				return 6;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof Ships) {
				return 7;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof Favourites) {
				return 8;
			} else if (MyApp.getInstance().getCurrentFragment() instanceof About) {
				return 11;
			}
		}

		return 1;
	}

	/**
	 * Sets the counter of notifications for a menu item.
	 * 
	 * @param pos
	 *            of the menu
	 * @param val
	 *            to set
	 */
	public void setCounter(int pos, int val) {
		drawerAdapter.setCounter(pos, val);
		drawerAdapter.notifyDataSetChanged();
	}

}
