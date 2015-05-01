package de.kauz.starcitizen.informer.fragments;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.adapters.SectionsListViewAdapter;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.JsoupDownloadFragment;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
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
 * Section View containing additional News items; select one through a spinner.
 * 
 * @author MadKauz
 * 
 */
public class Sections extends JsoupDownloadFragment {

	private TextView moreLoading;
	private Spinner infoSelectionSpinner;

	private Typeface font;
	private SectionsListViewAdapter moreListViewAdapter;
	private ListView moreListView;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sections, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.moreLoading = (TextView) getView().findViewById(R.id.MoreLoading);
		this.infoSelectionSpinner = (Spinner) getView().findViewById(
				R.id.MoreInfoSelectionSpinner);
		this.moreListView = (ListView) getView()
				.findViewById(R.id.MoreListView);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.moreLoading.setTypeface(font);

		this.moreListViewAdapter = new SectionsListViewAdapter(getActivity(),
				font);
		this.moreListView.setAdapter(moreListViewAdapter);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.infoSpinnerArray,
						R.layout.spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.infoSelectionSpinner.setAdapter(spinnerAdapter);

		this.infoSelectionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {

						String download = null;

						switch (position) {
						case 0:
							download = InformerConstants.URL_NEWS_TRANSMISSIONS;
							break;
						case 1:
							download = InformerConstants.URL_NEWS_CITIZENS;
							break;
						case 2:
							download = InformerConstants.URL_NEWS_ENGINEERING;
							break;
						case 3:
							download = InformerConstants.URL_NEWS_SPECTRUM;
							break;

						default:
							break;
						}
						if (MyApp.getInstance().isOnline(getActivity())
								&& download != null) {
							onStartDownloading(getActivity(), moreLoading,
									download);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					};
				});

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[2]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[2]);
	}

	@Override
	public void onDownloadComplete(Document doc) {
		if (doc != null) {
			moreListViewAdapter.clearList();
			try {
				Elements newsElements = doc.select("a.content-block2");

				for (Element newsElement : newsElements) {

					String url = newsElement.attr("abs:href");
					String type = newsElement.child(0).text();
					String title = newsElement.child(2).text();
					int commentsCount = Integer.valueOf(newsElement.child(3)
							.child(0).text());
					String postingTime = newsElement.child(3).child(1).text();
					String additionalInfo = newsElement.child(4).text();

					News news = new News(url, title, type, commentsCount,
							postingTime, additionalInfo);
					moreListViewAdapter.addOrRefreshNews(news);
					ViewHelper.fadeIn(moreListView);
				}
			} catch (NullPointerException e) {
				MyApp.getInstance().showError(getActivity(),
						getResources().getString(R.string.errorParseFault));
			} catch (IndexOutOfBoundsException e) {
				MyApp.getInstance().showError(getActivity(),
						getResources().getString(R.string.errorParseFault));
			}
		}
		super.onDownloadComplete(doc);
	}

}
