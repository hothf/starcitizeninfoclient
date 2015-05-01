package de.kauz.starcitizen.informer.utils;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.fragments.FanSites;
import de.kauz.starcitizen.informer.model.FanSite;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Views a single image in an overlay.
 * 
 * @author MadKauz
 * 
 */
public class DialogPopup implements OnClickListener {

	private Dialog overlay;
	private FanSites fragment;
	private FanSite site;

	/**
	 * Creates an overlay with a zoomed image.
	 * 
	 * @param context
	 * @param site
	 * @param font
	 * @param fragment
	 */
	public DialogPopup(Context context, FanSite site, Typeface font,
			FanSites fragment) {
		this.fragment = fragment;
		this.site = site;

		overlay = new Dialog(context);
		overlay.requestWindowFeature(Window.FEATURE_NO_TITLE);

		overlay.setContentView(R.layout.popup_dialog);

		TextView text = (TextView) overlay.findViewById(R.id.popUpDialogText);

		Button ok = (Button) overlay.findViewById(R.id.popUpDialogOKButton);
		Button cancel = (Button) overlay
				.findViewById(R.id.popUpDialogCancelButton);

		text.setTypeface(font);
		ok.setTypeface(font);
		cancel.setTypeface(font);

		text.setText(context.getResources().getString(
				R.string.deleteQuestionPrefix)
				+" "+ site.getName()
				+" "+ context.getResources().getString(
						R.string.deleteQuestionSuffix));

		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	/**
	 * Shows the overlay with the image.
	 */
	public void open() {
		overlay.show();
	}

	/**
	 * Closes the overlay.
	 */
	public void close() {
		overlay.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popUpDialogOKButton:
			fragment.removeFanSite(this.site);
			close();
			break;
		case R.id.popUpDialogCancelButton:
			close();
			break;

		default:
			break;
		}

	}

}
