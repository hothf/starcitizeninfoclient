package de.kauz.starcitizen.informer.utils;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.widget.TextView;

/**
 * Fragment for downloading RSS feeds.
 * 
 * @author MadKauz
 * 
 */
public class RssFeedFragment extends Fragment {

	private PullToRefreshListView listView;
	private TextView loadingView;

	private boolean shouldShowLoadingAnimation = false;

	public void onStartRSSDownload(boolean shouldShowLoadingAnimation,
			TextView loadingView, PullToRefreshListView listView,
			Intent serviceIntent) {
		if (!shouldShowLoadingAnimation) {
			ViewHelper.animateLoading(loadingView);
		}
		this.shouldShowLoadingAnimation = shouldShowLoadingAnimation;
		this.loadingView = loadingView;
		this.listView = listView;
		serviceIntent.putExtra(RssService.RECEIVER, resultReceiver);
		getActivity().startService(serviceIntent);
	}

	/**
	 * Once the {@link RssService} finishes its task, the result is sent to this
	 * ResultReceiver.
	 */
	private final ResultReceiver resultReceiver = new ResultReceiver(
			new Handler()) {
		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			if (!shouldShowLoadingAnimation) {
				ViewHelper.stopAnimatingLoading(loadingView);
			}
			
			try{
				ArrayList<RssItem> items = (ArrayList<RssItem>) resultData
						.getSerializable(RssService.ITEMS);
				if (items != null) {
					items.remove(0);
					onReceived(items);
					ViewHelper.fadeIn(listView);
				} else {
					 MyApp.getInstance().showError(getActivity(), getResources().getString(
							 R.string.errorRssDownload));
				}
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				 MyApp.getInstance().showError(getActivity(), getResources().getString(
						 R.string.errorRssDownload));
			}
		
		};
	};

	/**
	 * Called when the RSS feed is completely downloaded and transformed into
	 * RSSItems.
	 * 
	 * @param items
	 */
	public void onReceived(ArrayList<RssItem> items) {

	}

}
