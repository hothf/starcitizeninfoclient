package de.kauz.starcitizen.informer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.fragments.NewsFeed;
import agent.Agent;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.widget.Toast;

/**
 * Singleton class to hold an instance of the application.
 * 
 * @author MadKauz
 * 
 */
public class MyApp extends Application {

	private static MyApp instance;
	private Fragment currentFragment;
	private SharedPreferences preferences;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm;
	private String regid;
	private Agent agent;

    //TODO place your sender id here
	private String SENDER_ID = "00000000000";

	/**
	 * LifeCycle onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		MyApp.instance = this;

		this.agent = new Agent(this);

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (isPushEnabled()) {
			if (checkPlayServices()) {
				this.gcm = GoogleCloudMessaging.getInstance(this);
				this.regid = getRegistrationId(this);
				if (regid.equals("")) {
					registerInBackground();
				}
			} else {
				// Log.i(TAG, "No valid Google Play Services APK found.");
			}
		}
	}

	/**
	 * Checks for legacy versions of Android. Versions prior to ICECREAMS. (4.0)
	 * will return false.
	 * 
	 * @return true if legacy, false otherwise
	 */
	public boolean isLegacyAndroidVersion() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * Retrieve the status of push notification enable. Defaults to true.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isPushEnabled() {
		return this.preferences.getBoolean(InformerConstants.PREFERENCES_PUSH,
				true);
	}

	/**
	 * Gets the current Application Instance.
	 * 
	 * @return the Application instance
	 */
	public static MyApp getInstance() {
		return instance;
	}

	/**
	 * Checks the online availability of the device. If no connection is
	 * available, a Toast is shown.
	 * 
	 * @param context
	 *            of the current activity
	 * @return true if connected to the internet
	 */
	public boolean isOnline(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

		if (activeNetwork == null) {
			Toast.makeText(context,
					getResources().getString(R.string.errorNoConnection),
					Toast.LENGTH_SHORT).show();
			return false;

		} else {

			return true;
		}

	}

	/**
	 * Sets the Fragment which should currently be actively shown.
	 * 
	 * @param fragment
	 *            the Fragment to be shown
	 */
	public void setCurrentFragment(Fragment fragment) {
		if (fragment != null) {
			this.currentFragment = fragment;
		} else {
			this.currentFragment = new NewsFeed();
		}

	}

	/**
	 * Getting the current shown Fragment.
	 * 
	 * @return the current fragment
	 */
	public Fragment getCurrentFragment() {
		if (this.currentFragment != null) {
			return currentFragment;
		} else {
			return new NewsFeed();
		}

	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(String regId) {
		int appVersion = getAppVersion(this);
		// Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Returns the stored registration ID. Returns empty String if no Reg is
	 * available.
	 * 
	 * @param context
	 * @return the ID
	 */
	private String getRegistrationId(Context context) {

		String registrationId = preferences.getString(PROPERTY_REG_ID, "");
		if (registrationId.equals("")) {
			// Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = preferences.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			// Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			try {
				if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					GooglePlayServicesUtil.getErrorDialog(resultCode,
							getCurrentFragment().getActivity(),
							PLAY_SERVICES_RESOLUTION_REQUEST).show();
				} else {
					// Log.i(TAG, "This device is not supported.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}
		return true;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

                    // TODO implement with your own server
					//sendRegistrationIdToBackend();

					storeRegistrationId(regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Log.i(TAG, regid);
			}
		}.execute(null, null, null);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app.
	 */
	private void sendRegistrationIdToBackend() {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(
					"http://your.page/user");

			StringEntity input = new StringEntity("{\"regid\":\"" + this.regid
					+ "\"}");
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			if (response.getStatusLine().getStatusCode() == 201) {
			} else {
				// Log.e(TAG, "Was not able to register regid");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scales down a Bitmap
	 * 
	 * @param photo
	 * @return
	 */
	public Bitmap scaleDownBitmap(Bitmap photo) {

		int newHeight = 100;
		final float densityMultiplier = getResources().getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}

	/**
	 * Decodes an image and scales it to reduce memory consumption.
	 * 
	 * @param stream
	 *            of the image
	 * @param url
	 *            of the image
	 * @return the scaled and decoded image
	 */
	public static Bitmap decodeImageStream(InputStream stream, URL url) {
		try {
			Rect rect = new Rect();
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, rect, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 150;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			stream.close();
			stream = url.openConnection().getInputStream();
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(stream, rect, o2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Decodes image in hi res.
	 * 
	 * @param stream
	 *            to decode
	 * @return the image
	 */
	public static Bitmap decodeHiResImageStream(InputStream stream) {
		try {
			Rect rect = new Rect();
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			return BitmapFactory.decodeStream(stream, rect, o);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves the currently used API version.
	 * 
	 * @return the version number
	 */
	public int getCurrentApiVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * Shows an error message.
	 * 
	 * @param msg
	 *            message to be shown
	 */
	public void showError(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Shows an error message.
	 * 
	 * @param context
	 *            containing the context of the msg
	 * @param msg
	 *            message to be shown
	 */
	public void showError(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Insert an image and create a thumbnail for it.
	 *
	 */
	public static final String insertImage(Context context, Bitmap source,
			String title, String description) {

		ContentValues values = new ContentValues();
		ContentResolver cr = context.getContentResolver();

		values.put(MediaColumns.TITLE, title);
		values.put(ImageColumns.DESCRIPTION, description);
		values.put(MediaColumns.MIME_TYPE, "image/jpeg");
		values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());

		Uri url = null;
		String stringUrl = null; /* value to be returned */

		try {
			url = cr.insert(Media.EXTERNAL_CONTENT_URI, values);

			if (source != null) {
				OutputStream imageOut = cr.openOutputStream(url);
				try {
					source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
				} finally {
					imageOut.close();
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.saveImageInfo)
									+ ": " + title, Toast.LENGTH_SHORT).show();
				}

			} else {
			}
		} catch (Exception e) {
			Toast.makeText(
					context,
					context.getResources().getString(R.string.errorSavingImage),
					Toast.LENGTH_SHORT).show();

			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}

		if (url != null) {
			stringUrl = url.toString();
		}

		return stringUrl;
	}

	/**
	 * Retrieves the current, formatted date.
	 * 
	 * @return the date
	 */
	public String retrieveCurrentDate() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();

		String theDate = "" + (today.month + 1) + "/" + today.monthDay + "/"
				+ today.year;

		return theDate;
	}

	/**
	 * Retrieves the posting time.
	 * 
	 * @return the posting time
	 */
	public String retrievePostingTime() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();

		String hour = "";
		String ampm = "";
		if (today.hour > 12) {
			hour = "" + (today.hour - 12);
			ampm = "pm";
		} else {
			hour = "" + (today.hour);
			;
			ampm = "am";
		}

		String theDate = "" + hour + ":" + today.minute + " " + ampm + ", "
				+ (today.month + 1) + "/" + today.monthDay + "/" + today.year;
		return theDate;
	}
}
