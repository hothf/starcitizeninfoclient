package de.kauz.starcitizen.informer.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import de.kauz.starcitizen.informer.model.RssItem;
import de.kauz.starcitizen.informer.utils.ForumRssParser;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.NewsRssParser;
import de.kauz.starcitizen.informer.utils.VideoRssParser;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Custom Service to provide rss feeds.
 * 
 * @author MadKauz
 * 
 */
public class RssService extends IntentService {
	
	public static final String RSSLINKNEWS = "https://robertsspaceindustries.com/comm-link/rss";
	
	public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";
	public static final String LINK = "rssLink";
	public static final String PARSERTYPE = "parserType";

	public static final String TYPE_VIDEO = "videoType";
	public static final String TYPE_FORUM = "forumType";
	public static final String TYPE_NEWS = "newsType";
	
	public static final int ERROR_CODE = -404;

	public RssService() {
		super("RssService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		List<RssItem> rssItems = null;
		String link = intent.getStringExtra(LINK);
		String parserType = intent.getStringExtra(PARSERTYPE);
		ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
		try {
			InputStream stream = getInputStream(link, receiver);
			if (stream != null) {
				if (parserType.equals(TYPE_VIDEO)) {
					VideoRssParser parser = new VideoRssParser();
					rssItems = parser.parse(stream);
				} else if (parserType.equals(TYPE_FORUM)) {
					ForumRssParser parser = new ForumRssParser();
					rssItems = parser.parse(stream);
				} else if (parserType.equals(TYPE_NEWS)) {
					NewsRssParser parser = new NewsRssParser();
					rssItems = parser.parse(stream);
				}
			} else {
				receiver.send(ERROR_CODE, null);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			receiver.send(ERROR_CODE, null);
		} catch (IOException e) {
			e.printStackTrace();
			receiver.send(ERROR_CODE, null);
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) rssItems);
	
		receiver.send(0, bundle);
	}

	public InputStream getInputStream(String link, ResultReceiver receiver) {
		if (MyApp.getInstance().isOnline(getBaseContext())) {
			try {
				URL url = new URL(link);
				URLConnection connection = url.openConnection();
				connection
						.setConnectTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.setReadTimeout(InformerConstants.TIMEOUT_CONNECTION);
				return connection.getInputStream();
			} catch (IOException e) {
				return null;
			}
		} else {
			receiver.send(ERROR_CODE, null);
		}
		return null;
	}
}
