package de.kauz.starcitizen.informer.widget;

import java.util.ArrayList;

import de.kauz.starcitizen.informer.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Custom Adapter for a ListView. Intended to display News Objects.
 * 
 * @author MadKauz
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AppWidgetListAdapter implements
		RemoteViewsService.RemoteViewsFactory {

	private Context context = null;

	private ArrayList<String> titles = new ArrayList<String>();

	public AppWidgetListAdapter(Context context, Intent intent,
			ArrayList<String> titles) {

		this.context = context;
		this.titles = titles;
	}

	public int getCount() {
		return titles.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public RemoteViews getLoadingView() {
		return null;
	}

	public RemoteViews getViewAt(int position) {
		RemoteViews row = new RemoteViews(context.getPackageName(),
				R.layout.list_widget_layout_item);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String colorString = sharedPrefs.getString("prefWidgetColor", context
				.getResources().getString(R.string.settingsWidgetDefault));

		int buttonstyle = R.drawable.widget_theme_default_button_style;

		if (colorString != "") {
			if (colorString.equals(context.getResources().getStringArray(
					R.array.settingsWidgetColors)[0])) {

			} else if (colorString.equals(context.getResources()
					.getStringArray(R.array.settingsWidgetColors)[1])) {

				buttonstyle = R.drawable.widget_theme_light_button_style;

			} else if (colorString.equals(context.getResources()
					.getStringArray(R.array.settingsWidgetColors)[2])) {

				buttonstyle = R.drawable.widget_theme_red_button_style;

			} else if (colorString.equals(context.getResources()
					.getStringArray(R.array.settingsWidgetColors)[3])) {

				buttonstyle = R.drawable.widget_theme_orange_button_style;

			} else if (colorString.equals(context.getResources()
					.getStringArray(R.array.settingsWidgetColors)[4])) {

				buttonstyle = R.drawable.widget_theme_green_button_style;
			}

			row.setInt(R.id.widget_button_1, "setBackgroundResource",
					buttonstyle);
		}
		row.setTextViewText(R.id.widget_button_1, titles.get(position));

		Intent fillInIntent = new Intent();
		fillInIntent.setAction(AppWidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER);
		fillInIntent.putExtra(AppWidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER,
				position);
		row.setOnClickFillInIntent(R.id.widgetBackground, fillInIntent);

		return (row);
	}

	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

}
