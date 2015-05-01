package de.kauz.starcitizen.informer.fragments;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Faqs;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * About View containing copyright info.
 * 
 * @author MadKauz
 * 
 */
public class About extends Fragment {

	private TextView aboutInfoText1, aboutInfoText2, aboutVersion;
	private Button faqsButton;

	private Typeface font;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
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

		this.aboutInfoText1 = (TextView) getView()
				.findViewById(R.id.AboutText1);
		this.aboutInfoText2 = (TextView) getView()
				.findViewById(R.id.AboutText2);
		this.aboutVersion = (TextView) getView().findViewById(
				R.id.AboutVersionInfoText);
		this.faqsButton = (Button) getView().findViewById(R.id.AboutFaqsButton);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.aboutInfoText1.setTypeface(font);
		this.aboutInfoText2.setTypeface(font);
		this.aboutVersion.setTypeface(font);
		this.faqsButton.setTypeface(font);

		this.faqsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), Faqs.class);
				getActivity().startActivity(intent);
			}
		});

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[11]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[11]);

		PackageInfo pInfo;
		String version = "";
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (version.length() > 0) {
			aboutVersion.setText(getResources().getString(R.string.appName)
					+ " V." + version);
		}

	}

}
