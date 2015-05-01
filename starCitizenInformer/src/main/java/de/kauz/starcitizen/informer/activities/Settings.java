package de.kauz.starcitizen.informer.activities;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.utils.InformerConstants;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Settings page. Uses deprecated apis for support of very old android versions.
 * 
 * @author MadKauz
 * 
 */
public class Settings extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(InformerConstants.MENU_ITEMS[9]);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
            if (getActionBar() != null) {
                getActionBar().setIcon(InformerConstants.MENU_ICONS[9]);
            }
		}
		addPreferencesFromResource(R.xml.settings);

	}
}