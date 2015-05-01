package agent;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import de.kauz.starcitizen.informer.databases.FavouritesDBContentprovider;
import de.kauz.starcitizen.informer.databases.FavouritesDBContract;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.utils.MyApp;

/**
 * Manages favourites in a SQLite db.
 * 
 * @author MadKauz
 * 
 */
public class FavouritesDBManager {

	/**
	 * Fetches favourite items from the db.
	 * 
	 * @param context
	 * @param which
	 *            items to fetch
	 * @return the items
	 */
	public ArrayList<Favourite> fetchItemsFromFavouritesTable(Context context,
			String which) {

		ArrayList<Favourite> favourites = new ArrayList<Favourite>();

		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = FavouritesDBContentprovider.CONTENT_URI;
		String[] projection = FavouritesDBContract.DataBaseEntry.available;
		String sortOrder = "_id" + " DESC ";
		String selection = "type = " + "'" + which + "'";
		Cursor cursor = resolver.query(contentUri, projection, selection, null,
				sortOrder);
		int size = cursor.getCount();

		if (size > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < size; i++) {
				Favourite fav = new Favourite(cursor.getString(3),
						cursor.getString(2), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7));

				favourites.add(fav);
				cursor.moveToNext();
			}
			cursor.close();
			return favourites;
		} else {
			// if not one entry exists, create the table
			cursor.close();
			return favourites;
		}
	}

	/**
	 * Inserts a favourite into the db.
	 * 
	 * @param fav
	 *            the favourite to save
	 */
	public void insertFavourite(Context context, Favourite fav) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = FavouritesDBContentprovider.CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_TYPE,
				fav.getType());
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_TITLE,
				fav.getTitle());
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_URL, fav.getUrl());
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_DESCRIPTION,
				fav.getDescription());
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_DATE, MyApp
				.getInstance().retrieveCurrentDate());
		values.put(FavouritesDBContract.DataBaseEntry.COLUMN_OPTIONAL1,
				fav.getOptional1());
		resolver.insert(contentUri, values);
	}

	/**
	 * Deletes a favourite from the db.
	 * 
	 * @param context
	 * @param fav
	 *            the favourite to delete
	 */
	public void deleteFavourite(Context context, Favourite fav) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = FavouritesDBContentprovider.CONTENT_URI;
		String where = FavouritesDBContract.DataBaseEntry.COLUMN_TITLE + "="
				+ "'" + fav.getUrl() + "'";
		resolver.delete(contentUri, where, null);
	}

	/**
	 * Checks if a favourite is already stored in the db.
	 * 
	 * @param context
	 * @param fav
	 *            the favourite to check
	 * @return true if already saved, false otherwise
	 */
	public boolean isFavAlreadySaved(Context context, Favourite fav) {
		ContentResolver resolver = context.getContentResolver();
		Uri contentUri = FavouritesDBContentprovider.CONTENT_URI;
		String[] projection = FavouritesDBContract.DataBaseEntry.available;
		String selection = FavouritesDBContract.DataBaseEntry.COLUMN_TITLE
				+ " =  " + "'" + fav.getUrl() + "'";
		Cursor cursor = resolver.query(contentUri, projection, selection, null,
				null);
		int count = cursor.getCount();
		cursor.close();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

}
