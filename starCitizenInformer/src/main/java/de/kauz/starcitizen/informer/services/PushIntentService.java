package de.kauz.starcitizen.informer.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;

/**
 * Push service for the informer application. Can send a notification which
 * opens the news feed on click.
 * 
 * @author MadKauz
 * 
 */
public class PushIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;

	public PushIntentService() {
		super("PushIntentService");
	}

	public static final String TAG = "Star Citizen Informer Service";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				if (MyApp.getInstance().isPushEnabled()) {
					sendNotification("Received: " + extras.toString());
				}
			}
		}
		WakefulBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent newsIntent = new Intent(this, Main.class);
		newsIntent.putExtra(InformerConstants.EXTRAS_PUSH_HAS_NEW_NEWS, true);
		newsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				newsIntent, 0);

		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_push_large);
		long[] vibratePattern = { 300, 500, 300 };
		String led = "cyan";

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_push_small)
				.setContentTitle(getResources().getString(R.string.appName))
				.setContentText(getResources().getString(R.string.pushNotify))
				.setLargeIcon(icon).setVibrate(vibratePattern)
				.setLights(Color.parseColor(led), 5000, 1000)
				.setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
