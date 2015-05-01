package de.kauz.starcitizen.informer.fragments;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.adapters.ForumListViewAdapter;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.RssFeedFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A forum view fragment.
 * 
 * @author MadKauz
 * 
 */
public class ForumsPageFragment extends RssFeedFragment {

	private PullToRefreshListView forumList;

	private TextView forumsPageLoad;
	private Spinner forumsSelectionSpinner;

	private boolean isPullToRefreshTriggered = false;

	private int selectedIndex = 0;

	private Typeface font;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.fragment_forums_page, container, false);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.forumsPageLoad = (TextView) getView().findViewById(
				R.id.forumsPageLoading);
		this.forumsSelectionSpinner = (Spinner) getView().findViewById(
				R.id.forumsPageSelectionSpinner);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.forumList = (PullToRefreshListView) getView().findViewById(
				R.id.SocialForumList);
		this.forumsPageLoad.setTypeface(font);

		this.forumList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				isPullToRefreshTriggered = true;
				Intent intent = new Intent(getActivity(), RssService.class);
				intent.putExtra(RssService.LINK,
						InformerConstants.FORUMURLS[selectedIndex]);
				intent.putExtra(RssService.PARSERTYPE,
						RssService.TYPE_FORUM);
				onStartRSSDownload(isPullToRefreshTriggered, forumsPageLoad,
						forumList, intent);
			}
		});

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.forumsSpinnerArray,
						R.layout.spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.forumsSelectionSpinner.setAdapter(spinnerAdapter);

		this.forumsSelectionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						isPullToRefreshTriggered = false;
						selectedIndex = position;
						Intent intent = new Intent(getActivity(),
								RssService.class);
						intent.putExtra(RssService.LINK,
								InformerConstants.FORUMURLS[selectedIndex]);
						intent.putExtra(RssService.PARSERTYPE,
								RssService.TYPE_FORUM);
						onStartRSSDownload(isPullToRefreshTriggered,
								forumsPageLoad, forumList, intent);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						Intent intent = new Intent(getActivity(),
								RssService.class);
						intent.putExtra(RssService.LINK,
								InformerConstants.FORUMURLS[selectedIndex]);
						intent.putExtra(RssService.PARSERTYPE,
								RssService.TYPE_FORUM);
						onStartRSSDownload(isPullToRefreshTriggered,
								forumsPageLoad, forumList, intent);
					}
				});
	}

	@Override
	public void onReceived(ArrayList<RssItem> items) {
		forumList.onRefreshComplete();
		ForumListViewAdapter adapter = new ForumListViewAdapter(getActivity(),
				items, font);
		forumList.setAdapter(adapter);
		super.onReceived(items);
	}

}
