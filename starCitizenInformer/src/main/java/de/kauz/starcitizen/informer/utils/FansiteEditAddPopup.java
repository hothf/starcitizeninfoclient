package de.kauz.starcitizen.informer.utils;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.fragments.FanSites;
import de.kauz.starcitizen.informer.model.FanSite;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A popup containing features to add a new fansite.
 * 
 * @author MadKauz
 * 
 */
public class FansiteEditAddPopup {

	private Dialog overlay;

	public static enum TYPE {
		TYPE_ADD, TYPE_EDIT
	}

	/**
	 * Creates the popup.
	 * 
	 * @param context
	 *            of the popup
	 * @param parentFragment
	 *            the parent fragment of the popup
	 * @param type
	 *            one of TYPE_ADD or TYPE_EDIT to add or edit a fan site
	 * @param font
	 *            the used font of the popup
	 * @param site
	 *            the site to edit or add
	 */
	public FansiteEditAddPopup(Context context, final FanSites parentFragment,
			TYPE type, Typeface font, final FanSite site) {
		overlay = new Dialog(context);
		overlay.requestWindowFeature(Window.FEATURE_NO_TITLE);

		overlay.setContentView(R.layout.popup_fansite_editadd);

		final TextView infoText = (TextView) overlay
				.findViewById(R.id.popUpFansiteInfo);

		final EditText editTitle = (EditText) overlay
				.findViewById(R.id.popUpFansiteAddTitleEdit);

		final EditText editUrl = (EditText) overlay
				.findViewById(R.id.popUpFansiteAddurlEdit);

		Button addButton = (Button) overlay
				.findViewById(R.id.popUpFansiteAddButton);
		Button cancelButton = (Button) overlay
				.findViewById(R.id.popUpFansiteCancelButton);

		addButton.setTypeface(font);
		cancelButton.setTypeface(font);
		infoText.setTypeface(font);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
			}
		});

		final Spinner catSelectionSpinner = (Spinner) overlay
				.findViewById(R.id.popUpFanSiteChooseCategory);

		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(
				context, R.layout.spinner_item, FanSite.CATEGORIES);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		catSelectionSpinner.setAdapter(spinnerAdapter);

		catSelectionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					};
				});

		if (type == TYPE.TYPE_ADD) {

			addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String selectedCat = FanSite.CATEGORIES[catSelectionSpinner
							.getSelectedItemPosition()];

					FanSite fansite = new FanSite(editTitle.getText()
							.toString(), editUrl.getText().toString(),
							FanSite.SITETYPE.TYPE_CONTENT, selectedCat);
					parentFragment.addFanSite(fansite);
					close();
				}
			});
		} else {

			int index = 0;
			for (int i = 0; i < FanSite.CATEGORIES.length; i++) {
				if (FanSite.CATEGORIES[i].equals(site.getCategory())) {
					index = i;
				}
			}
			catSelectionSpinner.setSelection(index);

			infoText.setText(parentFragment.getResources().getString(
					R.string.popUpFansiteEditInfo));
			addButton.setText(parentFragment.getResources().getString(
					R.string.Edit));
			editTitle.setText(site.getName());
			editUrl.setText(site.getUrl());

			addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String oldName = site.getName();

					site.setName(editTitle.getText().toString());
					site.setUrl(editUrl.getText().toString());
					site.setCategory(FanSite.CATEGORIES[catSelectionSpinner
							.getSelectedItemPosition()]);
					parentFragment.editFanSite(site, oldName);
					close();
				}
			});

		}

	}

	/**
	 * Shows the popup with the image.
	 */
	public void open() {
		overlay.show();
	}

	/**
	 * Closes the popup.
	 */
	public void close() {
		overlay.dismiss();
	}
}
