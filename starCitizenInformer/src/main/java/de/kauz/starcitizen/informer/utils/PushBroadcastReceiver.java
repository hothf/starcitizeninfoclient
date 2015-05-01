package de.kauz.starcitizen.informer.utils;

import de.kauz.starcitizen.informer.services.PushIntentService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * The push broadcast receiver for the Star Citizen Informer App.
 * 
 * @author MadKauz
 * 
 */
public class PushBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(),
				PushIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
