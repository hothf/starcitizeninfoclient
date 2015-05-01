package de.kauz.starcitizen.informer.fragments;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.VideoListViewAdapter;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.RssFeedFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Rss-feed reader for youtube videos from the RSI channel. The downloading
 * process is done via a service.
 * 
 * @author MadKauz
 * 
 */
public class VideoFeed extends RssFeedFragment {

	private PullToRefreshListView listView;
	private View view;
	private Typeface font;
	private TextView rssLoading;

	private boolean isPullToRefreshTriggered = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[3]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[3]);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_video, container, false);
			rssLoading = (TextView) view.findViewById(R.id.rssLoading);
			rssLoading.setTypeface(font);
			listView = (PullToRefreshListView) view.findViewById(R.id.listView);

			Intent intent = new Intent(getActivity(), RssService.class);
			intent.putExtra(RssService.LINK, InformerConstants.RSSLINKVIDEOS);
			intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_VIDEO);
			onStartRSSDownload(isPullToRefreshTriggered, rssLoading, listView,
					intent);
		} else {
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}

		this.listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				isPullToRefreshTriggered = true;
				Intent intent = new Intent(getActivity(), RssService.class);
				intent.putExtra(RssService.LINK,
						InformerConstants.RSSLINKVIDEOS);
				intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_VIDEO);
				onStartRSSDownload(isPullToRefreshTriggered, rssLoading,
						listView, intent);
			}
		});
		return view;

	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
	}

	/**
	 * Refreshes data manually and not from the pull-to-refresh mechanism.
	 */
	public void refreshManually() {
		isPullToRefreshTriggered = false;
		Intent intent = new Intent(getActivity(), RssService.class);
		intent.putExtra(RssService.LINK, InformerConstants.RSSLINKVIDEOS);
		intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_VIDEO);
		onStartRSSDownload(isPullToRefreshTriggered, rssLoading, listView,
				intent);
	}

	@Override
	public void onReceived(ArrayList<RssItem> items) {
		listView.onRefreshComplete();
		VideoListViewAdapter adapter = new VideoListViewAdapter(getActivity(),
				items, font);
		listView.setAdapter(adapter);
		super.onReceived(items);
	}
}
