package agent;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.databases.NewsDBContentprovider;
import de.kauz.starcitizen.informer.databases.NewsDBContract;
import de.kauz.starcitizen.informer.model.News;
import de.kauz.starcitizen.informer.utils.MyApp;

/**
 * Manages news in a SQLite db.
 * 
 * @author MadKauz
 * 
 */
public class NewsDBManager {

	/**
	 * Fetches news items from the db from a specific range.
	 * 
	 * @param context
	 * @param toHowMany
	 *            last news to fetch
	 * @param fromHowMany
	 *            fist news to fetch
	 * @return the news
	 */
	public ArrayList<News> fetchNewsTable(Context context, int fromHowMany,
			int toHowMany) {

		ArrayList<News> news = new ArrayList<News>();

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;
		String[] projection = NewsDBContract.DataBaseEntry.available;

		Cursor entryCursor = resolver.query(contentUri, projection, null, null,
				null);
		int entryCount = entryCursor.getCount();
		entryCursor.close();

		String sortOrder = "_id" + " DESC LIMIT " + fromHowMany + ","
				+ toHowMany;
		Cursor cursor = resolver.query(contentUri, projection, null, null,
				sortOrder);
		int size = cursor.getCount();

		if (size > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < size; i++) {
				News n = new News(cursor.getString(4), cursor.getString(3),
						cursor.getString(2), Integer.parseInt(cursor
								.getString(8)), cursor.getString(7),
						cursor.getString(5));
				n.setStatus(cursor.getString(6));

				news.add(n);
				cursor.moveToNext();
			}
			cursor.close();

			if (entryCount > size) {
				news.add(new News("", News.TYPE_LOAD_MORE, "", 0, "", ""));
			}

			return news;
		} else {
			// if not one entry exists, create the table
			cursor.close();
			return news;
		}
	}

	/**
	 * Inserts only new news into the db.
	 * 
	 * @param context
	 * @param newsToTryToPersist
	 *            the news to insert
	 */
	public void insertOnlyNew(Context context,
			ArrayList<News> newsToTryToPersist) {

		ArrayList<News> filteredNews = new ArrayList<News>();

		for (News ns : newsToTryToPersist) {
			if (!isAlreadyStoredInDB(context, ns, newsToTryToPersist.size())) {
				filteredNews.add(ns);
			}
		}

		if (filteredNews.size() > 0) {
			bulkInsert(context, filteredNews);
		}

	}

	/**
	 * Checks if a news is already stored in the db.
	 * 
	 * @param context
	 * @param news
	 *            the news to check
	 * @param howMany
	 *            how many news shall be checked
	 * @return
	 */
	public boolean isAlreadyStoredInDB(Context context, News news, int howMany) {

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;
		String[] projection = NewsDBContract.DataBaseEntry.available;
		String selection = NewsDBContract.DataBaseEntry.COLUMN_URL + " =  '"
				+ news.getUrl() + "'";
		String sortOrder = "_id" + " DESC LIMIT " + howMany;
		Cursor cursor = resolver.query(contentUri, projection, selection, null,
				sortOrder);
		int count = cursor.getCount();
		cursor.close();
		if (count > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Inserts a collection of news into the db.
	 * 
	 * @param newsToPersist
	 */
	public void bulkInsert(Context context, ArrayList<News> newsToPersist) {

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;

		ContentValues[] vals = new ContentValues[newsToPersist.size()];
		int i = 0;
		for (News n : newsToPersist) {
			ContentValues values = new ContentValues();
			values.put(NewsDBContract.DataBaseEntry.COLUMN_TYPE, n.getType());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_TITLE, n.getTitle());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_URL, n.getUrl());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_STATUS,
					n.getStatus());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_DESCRIPTION,
					n.getAdditionalInfo());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_DATE, context
					.getResources().getString(R.string.mainNewsFound)
					+ " "
					+ MyApp.getInstance().retrievePostingTime());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_COMMENTS,
					n.getCommentCount());
			values.put(NewsDBContract.DataBaseEntry.COLUMN_HASH, n.getHash());
			vals[i] = values;
			i++;
		}
		resolver.bulkInsert(contentUri, vals);

	}

	/**
	 * Retrieve the unsaved news count.
	 * 
	 * @param context
	 */
	public int getUnsavedCount(Context context) {

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;
		String[] projection = NewsDBContract.DataBaseEntry.available;
		String selection = NewsDBContract.DataBaseEntry.COLUMN_STATUS + " = 1";
		Cursor cursor = resolver.query(contentUri, projection, selection, null,
				null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Updates a news.
	 * 
	 * @param context
	 * @param news
	 *            news to update
	 */
	public void updateNews(Context context, News news) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;

		String selection = NewsDBContract.DataBaseEntry.COLUMN_URL + " =  '"
				+ news.getUrl() + "'";

		ContentValues values = new ContentValues();
		values.put(NewsDBContract.DataBaseEntry.COLUMN_TYPE, news.getType());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_TITLE, news.getTitle());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_URL, news.getUrl());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_STATUS, news.getStatus());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_DESCRIPTION,
				news.getAdditionalInfo());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_DATE, context
				.getResources().getString(R.string.mainNewsFound)
				+ " "
				+ MyApp.getInstance().retrievePostingTime());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_COMMENTS,
				news.getCommentCount());
		values.put(NewsDBContract.DataBaseEntry.COLUMN_HASH, news.getHash());

		resolver.update(contentUri, values, selection, null);
	}

	/**
	 * Checks if a news is already read.
	 * 
	 * @param context
	 * @param news
	 *            the news to check
	 * @return true if read, false otherwise
	 */
	public boolean isNewsAlreadyRead(Context context, News news) {

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;
		String[] projection = NewsDBContract.DataBaseEntry.available;
		String selection = NewsDBContract.DataBaseEntry.COLUMN_URL + " =  '"
				+ news.getUrl() + "'";
		Cursor cursor = resolver.query(contentUri, projection, selection, null,
				null);
		int count = cursor.getCount();
		if (count > 0) {

			cursor.moveToFirst();
			News n = new News(cursor.getString(4), cursor.getString(3),
					cursor.getString(2), Integer.parseInt(cursor.getString(8)),
					cursor.getString(7), cursor.getString(5));
			n.setStatus(cursor.getString(6));
			cursor.close();

			if (n.getStatus().equals("1")) {
				return false;
			} else {
				return true;
			}
		} else {
			cursor.close();
			return false;
		}
	}

	/**
	 * Sets all news as read within the db.
	 * @param context
	 */
	public void setAllNewsAsRead(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = NewsDBContentprovider.CONTENT_URI;

		ContentValues values = new ContentValues();
		values.put(NewsDBContract.DataBaseEntry.COLUMN_STATUS, "2");
		resolver.update(contentUri, values, null, null);

	}

}
