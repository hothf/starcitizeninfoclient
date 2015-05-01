package de.kauz.starcitizen.informer.fragments;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.OrgaInspect;
import de.kauz.starcitizen.informer.activities.PlayerInspect;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A search page fragment containing searches for citizens and organizations.
 * 
 * @author MadKauz
 * 
 */
public class SearchPageFragment extends Fragment {

	private EditText searchPlayers, searchOrgas;
	private TextView headerTopSearchPlayers, headerTopSearchOrgas;
	private Button searchPlayersButton, searchOrgasButton;

	private Typeface font;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_search_page, container, false);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.searchPlayers = (EditText) getView().findViewById(
				R.id.SocialSearchTop);
		this.searchOrgas = (EditText) getView().findViewById(
				R.id.SocialSearchBottom);
		this.headerTopSearchOrgas = (TextView) getView().findViewById(
				R.id.SocialHeaderSearchBottom);
		this.headerTopSearchPlayers = (TextView) getView().findViewById(
				R.id.SocialHeaderSearchTop);
		this.searchPlayersButton = (Button) getView().findViewById(
				R.id.SocialButtonSearchTop);
		this.searchOrgasButton = (Button) getView().findViewById(
				R.id.SocialButtonSearchBottom);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.headerTopSearchOrgas.setTypeface(font);
		this.headerTopSearchPlayers.setTypeface(font);
		this.searchOrgasButton.setTypeface(font);
		this.searchPlayersButton.setTypeface(font);

		// orga search
		this.searchOrgasButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), OrgaInspect.class);
				String url = InformerConstants.URL_ORGS
						+ searchOrgas.getText().toString();
				intent.putExtra(
						InformerConstants.DETAIL_EXTRAS_ORGA_SEARCH_LINK, url);
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_ORGA_HANLDE,
						searchOrgas.getText().toString());
				startActivity(intent);
			}
		});

		// player search
		this.searchPlayersButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PlayerInspect.class);
				String url = InformerConstants.URL_CITIZENS
						+ searchPlayers.getText().toString();
				intent.putExtra(
						InformerConstants.DETAIL_EXTRAS_PLAYER_SEARCH_LINK, url);
				intent.putExtra(InformerConstants.DETAIL_EXTRAS_PLAYER_HANDLE,
						searchPlayers.getText().toString());
				startActivity(intent);
			}
		});
		
	}
}
