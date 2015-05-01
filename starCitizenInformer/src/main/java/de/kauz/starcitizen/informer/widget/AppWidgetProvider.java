package de.kauz.starcitizen.informer.widget;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Detail;
import de.kauz.starcitizen.informer.activities.Goals;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.services.RssService;
import de.kauz.starcitizen.informer.utils.ApiDownload;
import de.kauz.starcitizen.informer.utils.CrowdFundingParser;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.RssDownload;
import de.kauz.starcitizen.informer.utils.ApiDownload.ApiDownloadListener;
import de.kauz.starcitizen.informer.utils.RssDownload.RssDownloadListener;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Provider for the Star Citizen Informer App widget. This comes with a
 * colorizable list but can also be colorized buttons with fewer info for
 * pre-sandwhich devices which do not show lists. Contains a crowd funding info
 * window also. There are buttons to refresh the widget and entry points to
 * clicked news. A button on top can lead to the main news feed of the app.
 * 
 * @author MadKauz
 * 
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

	public static final String EXTRAS_NEWS_TITLES_ARRAY = "_#news_array_titles#_";
	public static final String EXTRAS_NEWS_LINKS_ARRAY = "_#news_array_links#_";
	public static final String EXTRAS_NEWS_DESCRIPTIONS_ARRAY = "_#news_array_descriptions#_";
	public static final String EXTRAS_NEWS_LIST_COLOR = "_#new_list_color#_";

	public static final String ACTION_SHOW_DETAILS = "_#action_details#_";

	public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "_#row_number#_";

	private RssDownload download;

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals("update_widget_from_activity")) {

			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(
					context.getPackageName(), AppWidgetProvider.class.getName());
			int[] appWidgetIds = appWidgetManager
					.getAppWidgetIds(thisAppWidget);

			onUpdate(context, appWidgetManager, appWidgetIds);
		}
		if (intent.getAction().equals(EXTRA_LIST_VIEW_ROW_NUMBER)) {

			int viewIndex = intent.getIntExtra(EXTRA_LIST_VIEW_ROW_NUMBER, 0);
			String title = intent.getStringArrayListExtra(
					EXTRAS_NEWS_TITLES_ARRAY).get(viewIndex);
			String description = intent.getStringArrayListExtra(
					EXTRAS_NEWS_DESCRIPTIONS_ARRAY).get(viewIndex);
			String url = intent
					.getStringArrayListExtra(EXTRAS_NEWS_LINKS_ARRAY).get(
							viewIndex);

			Intent detailIntent = new Intent(context, Detail.class);
			detailIntent.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE, title);
			detailIntent.putExtra(InformerConstants.DETAIL_EXTRAS_LINK, url);
			detailIntent.putExtra(InformerConstants.DETAIL_EXTRAS_INFO,
					description);

			detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(detailIntent);

		}
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(
				context.getPackageName(), AppWidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onUpdate(context, appWidgetManager, appWidgetIds);
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			RemoteViews views;

			if (MyApp.getInstance().isLegacyAndroidVersion()) {
				views = new RemoteViews(context.getPackageName(),
						R.layout.widget_legacy_layout);
			} else {
				views = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout);
			}

			Intent Mainintent = new Intent(context, Main.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					Mainintent, 0);
			views.setOnClickPendingIntent(R.id.widget_informer_button,
					pendingIntent);

			Intent updateIntent = new Intent(context, AppWidgetProvider.class);
			updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					appWidgetIds);
			PendingIntent updatePendingIntent = PendingIntent
					.getBroadcast(context, 0, updateIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.widget_refresh_button,
					updatePendingIntent);

			if (MyApp.getInstance().isLegacyAndroidVersion()) {
				updateLegacy(context, views, appWidgetManager, appWidgetId);
			} else {
				updateList(context, views, appWidgetManager, appWidgetId);

			}
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	/**
	 * Updates the news feed widget.
	 * 
	 * @param context
	 *            of the widget
	 * @param views
	 *            to update
	 * @param appWidgetManager
	 *            manager to use
	 * @param appWidgetId
	 *            id of the widget to update
	 */
	private void updateList(final Context context, final RemoteViews views,
			final AppWidgetManager appWidgetManager, final int appWidgetId) {
		views.setViewVisibility(R.id.widgetProgress, View.VISIBLE);
		views.setTextViewText(R.id.widget_citizen_val, "");
		views.setTextViewText(R.id.widget_funds_val, "");
		views.setViewVisibility(R.id.widget_no_connection_text, View.GONE);
		views.setViewVisibility(R.id.widget_list_view, View.INVISIBLE);
		download = new RssDownload(context);

		RssDownloadListener listener = new RssDownloadListener() {

			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			public void onRssDownloadComplete(ArrayList<RssItem> items) {
				views.setViewVisibility(R.id.widgetProgress, View.GONE);

				if (items.size() >= 2) {
					Intent intent = new Intent(context,
							AppWidgetListService.class);
					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
							appWidgetId);
					ArrayList<String> newsTitles = new ArrayList<String>();
					ArrayList<String> newsLinks = new ArrayList<String>();
					ArrayList<String> newsDescriptions = new ArrayList<String>();

					for (RssItem item : items) {
						if (!item.getTitle().equals("RSI Comm-Link")) {
							newsTitles.add(item.getTitle());
							newsLinks.add(item.getLink());
							newsDescriptions.add(item.getDescription());
						}
					}
					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(context);
					String colorString = sharedPrefs.getString(
							"prefWidgetColor", context.getResources()
									.getString(R.string.settingsWidgetDefault));
					setColors(colorString, views, context, true);
					intent.putExtra(EXTRAS_NEWS_LIST_COLOR, colorString);
					intent.putExtra(EXTRAS_NEWS_TITLES_ARRAY, newsTitles);
					intent.putExtra(EXTRAS_NEWS_LINKS_ARRAY, newsLinks);
					intent.putExtra(EXTRAS_NEWS_DESCRIPTIONS_ARRAY,
							newsDescriptions);
					views.setViewVisibility(R.id.widget_list_view, View.VISIBLE);
					views.setRemoteAdapter(R.id.widget_list_view, intent);

					Intent pressIntent = new Intent(context,
							AppWidgetProvider.class);
					pressIntent.setAction(EXTRA_LIST_VIEW_ROW_NUMBER);
					pressIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
							appWidgetId);
					pressIntent.putExtra(EXTRAS_NEWS_TITLES_ARRAY, newsTitles);
					pressIntent.putExtra(EXTRAS_NEWS_LINKS_ARRAY, newsLinks);
					pressIntent.putExtra(EXTRAS_NEWS_DESCRIPTIONS_ARRAY,
							newsDescriptions);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							context, 0, pressIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setPendingIntentTemplate(R.id.widget_list_view,
							pendingIntent);

					appWidgetManager.updateAppWidget(appWidgetId, views);
				} else {
					views.setViewVisibility(R.id.widgetProgress, View.GONE);
					views.setViewVisibility(R.id.widget_no_connection_text,
							View.VISIBLE);
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}

			@Override
			public void onRSSDownloadError() {
				views.setViewVisibility(R.id.widgetProgress, View.GONE);
				views.setViewVisibility(R.id.widget_no_connection_text,
						View.VISIBLE);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		};
		download.setRssDownloadListener(listener);
		Intent intent = new Intent(context, RssService.class);
		intent.putExtra(RssService.LINK, RssService.RSSLINKNEWS);
		intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_NEWS);
		download.onStartRSSDownload(intent);

		showFundsAndCitizens(views, context);

	}

	/**
	 * Updates on pre-sandwhich devices which do not come with a list.
	 * 
	 * @param context
	 *            of the widget
	 * @param views
	 *            to update
	 * @param appWidgetManager
	 *            the manager
	 * @param appWidgetId
	 *            the id of the widget to update
	 */
	private void updateLegacy(final Context context, final RemoteViews views,
			final AppWidgetManager appWidgetManager, final int appWidgetId) {

		views.setViewVisibility(R.id.widgetProgress, View.VISIBLE);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		String colorString = sharedPrefs.getString("prefWidgetColor", context
				.getResources().getString(R.string.settingsWidgetDefault));

		setColors(colorString, views, context, true);

		views.setTextViewText(R.id.widget_citizen_val, "");
		views.setTextViewText(R.id.widget_funds_val, "");
		views.setViewVisibility(R.id.widget_no_connection_text, View.GONE);
		views.setViewVisibility(R.id.widget_button_1, View.GONE);
		views.setViewVisibility(R.id.widget_button_2, View.GONE);
		views.setViewVisibility(R.id.widget_button_3, View.GONE);
		views.setViewVisibility(R.id.widget_button_4, View.GONE);
		views.setViewVisibility(R.id.widget_button_5, View.GONE);
		download = new RssDownload(context);

		RssDownloadListener listener = new RssDownloadListener() {

			@Override
			public void onRssDownloadComplete(ArrayList<RssItem> items) {
				views.setViewVisibility(R.id.widgetProgress, View.GONE);

				if (items.size() >= 6) {

					views.setTextViewText(R.id.widget_button_1, items.get(1)
							.getTitle());
					views.setTextViewText(R.id.widget_button_2, items.get(2)
							.getTitle());
					views.setTextViewText(R.id.widget_button_3, items.get(3)
							.getTitle());
					views.setTextViewText(R.id.widget_button_4, items.get(4)
							.getTitle());
					views.setTextViewText(R.id.widget_button_5, items.get(5)
							.getTitle());
					views.setViewVisibility(R.id.widget_button_1, View.VISIBLE);
					views.setViewVisibility(R.id.widget_button_2, View.VISIBLE);
					views.setViewVisibility(R.id.widget_button_3, View.VISIBLE);
					views.setViewVisibility(R.id.widget_button_4, View.VISIBLE);
					views.setViewVisibility(R.id.widget_button_5, View.VISIBLE);

					Intent intent1 = new Intent(context, Detail.class);
					intent1.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							items.get(1).getTitle());
					intent1.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							items.get(1).getLink());
					PendingIntent pendingIntent1 = PendingIntent.getActivity(
							context, (int) System.currentTimeMillis() + 0,
							intent1, 0);
					views.setOnClickPendingIntent(R.id.widget_button_1,
							pendingIntent1);

					Intent intent2 = new Intent(context, Detail.class);
					intent2.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							items.get(2).getTitle());
					intent2.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							items.get(2).getLink());
					PendingIntent pendingIntent2 = PendingIntent.getActivity(
							context, (int) System.currentTimeMillis() + 22,
							intent2, 0);
					views.setOnClickPendingIntent(R.id.widget_button_2,
							pendingIntent2);

					Intent intent3 = new Intent(context, Detail.class);
					intent3.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							items.get(3).getTitle());
					intent3.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							items.get(3).getLink());
					PendingIntent pendingIntent3 = PendingIntent.getActivity(
							context, (int) System.currentTimeMillis() + 44,
							intent3, 0);
					views.setOnClickPendingIntent(R.id.widget_button_3,
							pendingIntent3);

					Intent intent4 = new Intent(context, Detail.class);
					intent4.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							items.get(4).getTitle());
					intent4.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							items.get(4).getLink());
					PendingIntent pendingIntent4 = PendingIntent.getActivity(
							context, (int) System.currentTimeMillis() + 66,
							intent4, 0);
					views.setOnClickPendingIntent(R.id.widget_button_4,
							pendingIntent4);

					Intent intent5 = new Intent(context, Detail.class);
					intent5.putExtra(InformerConstants.DETAIL_EXTRAS_TITLE,
							items.get(5).getTitle());
					intent5.putExtra(InformerConstants.DETAIL_EXTRAS_LINK,
							items.get(5).getLink());
					PendingIntent pendingIntent5 = PendingIntent.getActivity(
							context, (int) System.currentTimeMillis() + 88,
							intent5, 0);
					views.setOnClickPendingIntent(R.id.widget_button_5,
							pendingIntent5);

					appWidgetManager.updateAppWidget(appWidgetId, views);
				} else {
					views.setViewVisibility(R.id.widgetProgress, View.GONE);
					views.setViewVisibility(R.id.widget_no_connection_text,
							View.VISIBLE);
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}

			@Override
			public void onRSSDownloadError() {
				views.setViewVisibility(R.id.widgetProgress, View.GONE);
				views.setViewVisibility(R.id.widget_no_connection_text,
						View.VISIBLE);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		};

		download.setRssDownloadListener(listener);

		Intent intent = new Intent(context, RssService.class);
		intent.putExtra(RssService.LINK, InformerConstants.RSSLINKNEWS);
		intent.putExtra(RssService.PARSERTYPE, RssService.TYPE_NEWS);
		download.onStartRSSDownload(intent);

		showFundsAndCitizens(views, context);

	}

	/**
	 * Sets the colors of the widget.
	 * 
	 * @param colorString
	 *            the color to set
	 * @param views
	 *            to colorize
	 * @param isLegacy
	 *            set to true if colors should apply to pre-sandwhich devices
	 *            which do not come with list widgets.
	 */
	private void setColors(final String colorString, final RemoteViews views,
			Context context, boolean isLegacy) {

		int backgroundStyle = R.drawable.widget_theme_default;
		int buttonstyleless = R.drawable.widget_theme_default_button_styleless;
		int buttonstyle = R.drawable.widget_theme_default_button_style;
		int fontColorStyle = R.color.widgetThemeDefault;

		if (colorString.equals(context.getResources().getStringArray(
				R.array.settingsWidgetColors)[0])) {

		} else if (colorString.equals(context.getResources().getStringArray(
				R.array.settingsWidgetColors)[1])) {

			backgroundStyle = R.drawable.widget_theme_light;
			buttonstyleless = R.drawable.widget_theme_light_button_styleless;
			buttonstyle = R.drawable.widget_theme_light_button_style;
			fontColorStyle = R.color.widgetThemeLight;

		} else if (colorString.equals(context.getResources().getStringArray(
				R.array.settingsWidgetColors)[2])) {

			backgroundStyle = R.drawable.widget_theme_red;
			buttonstyleless = R.drawable.widget_theme_red_button_styleless;
			buttonstyle = R.drawable.widget_theme_red_button_style;
			fontColorStyle = R.color.widgetThemeRed;

		} else if (colorString.equals(context.getResources().getStringArray(
				R.array.settingsWidgetColors)[3])) {

			backgroundStyle = R.drawable.widget_theme_orange;
			buttonstyleless = R.drawable.widget_theme_orange_button_styleless;
			buttonstyle = R.drawable.widget_theme_orange_button_style;
			fontColorStyle = R.color.widgetThemeOrange;

		} else if (colorString.equals(context.getResources().getStringArray(
				R.array.settingsWidgetColors)[4])) {

			backgroundStyle = R.drawable.widget_theme_green;
			buttonstyleless = R.drawable.widget_theme_green_button_styleless;
			buttonstyle = R.drawable.widget_theme_green_button_style;
			fontColorStyle = R.color.widgetThemeGreen;
		}

		views.setInt(R.id.widgetBackground, "setBackgroundResource",
				backgroundStyle);
		views.setInt(R.id.widget_informer_button, "setBackgroundResource",
				buttonstyleless);
		views.setInt(R.id.widget_refresh_button, "setBackgroundResource",
				buttonstyleless);
		views.setTextColor(R.id.widget_citizen_val, context.getResources()
				.getColor(fontColorStyle));
		views.setTextColor(R.id.widget_funds_val, context.getResources()
				.getColor(fontColorStyle));
		views.setInt(R.id.widget_divider_top, "setBackgroundResource",
				fontColorStyle);
		if (isLegacy) {
			views.setInt(R.id.widget_button_1, "setBackgroundResource",
					buttonstyle);
			views.setInt(R.id.widget_button_2, "setBackgroundResource",
					buttonstyle);
			views.setInt(R.id.widget_button_3, "setBackgroundResource",
					buttonstyle);
			views.setInt(R.id.widget_button_4, "setBackgroundResource",
					buttonstyle);
			views.setInt(R.id.widget_button_5, "setBackgroundResource",
					buttonstyle);
		}
	}

	/**
	 * Shows the parsed crowd funding info.
	 * 
	 * @param views
	 *            to show the info
	 * @param context
	 *            of the views
	 */
	private void showFundsAndCitizens(final RemoteViews views,
			final Context context) {
		if (MyApp.getInstance().isOnline(context)) {
			ApiDownload apiDownload = new ApiDownload(
					"https://robertsspaceindustries.com/api/stats/getCrowdfundStats",
					"{\"alpha_slots\": true,\"chart\": \"day\",\"fans\": true,\"funds\": true}");
			apiDownload.setApiDownloadListener(new ApiDownloadListener() {

				@Override
				public void onApiDownloadComplete(String result) {
					if (result != null) {
						if (result.length() > 4) {
							CrowdFundingParser parser = new CrowdFundingParser(
									result);

							views.setTextViewText(R.id.widget_citizen_val,
									parser.getFansRaised());
							views.setTextViewText(R.id.widget_funds_val,
									parser.getFundsRaised());

							Intent goalsIntent = new Intent(context,
									Goals.class);
							goalsIntent.putExtra(Goals.EXTRA_FUNDS,
									parser.getFundsRaised());
							goalsIntent
									.putExtra(Goals.EXTRA_PERCENTAGE, "100%");
							goalsIntent.putExtra(Goals.EXTRA_GOAL_FUNDS,
									"REDACTED");
							goalsIntent.putExtra(Goals.EXTRA_PERCENTAGE_INT,
									100);
							PendingIntent pendingIntent6 = PendingIntent.getActivity(
									context,
									(int) System.currentTimeMillis() + 111,
									goalsIntent, 0);
							views.setOnClickPendingIntent(R.id.widget_button_5,
									pendingIntent6);
						}
					}
				}
			});
		}
	}
}
