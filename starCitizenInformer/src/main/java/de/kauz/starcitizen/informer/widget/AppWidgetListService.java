package de.kauz.starcitizen.informer.widget;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AppWidgetListService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		ArrayList<String> titles = new ArrayList<String>();
		if (intent.getExtras() != null) {
			titles = intent.getExtras().getStringArrayList(
					AppWidgetProvider.EXTRAS_NEWS_TITLES_ARRAY);
		}
		return (new AppWidgetListAdapter(this.getApplicationContext(), intent,
				titles));
	}

}
