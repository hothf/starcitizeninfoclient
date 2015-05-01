package agent;

import java.util.ArrayList;
import java.util.Collections;

import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.model.News;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * This mobile agent is used to mask the loss of client side connection to
 * webservices etc.
 * 
 * The agent mainly focuses on granting access to local databases to emulate
 * connection.
 * 
 * @author MadKauz
 * 
 */
public class Agent {

	private NewsDBManager newsDBManager;
	private FavouritesDBManager favouritesDBManager;

	private Context context;

	/**
	 * Creates a new agent for masquerading connection loss.
	 * 
	 * @param context
	 */
	public Agent(Context context) {
		this.context = context;

		this.newsDBManager = new NewsDBManager();
		this.favouritesDBManager = new FavouritesDBManager();
	}

	/**
	 * Fetches all news for local use. The count of items can be determined.
	 * Returns 0 elements if table does not exist. Returns fewer elements if
	 * wanted count exceeds available count.
	 * 
	 * @param fromHowMany
	 * @param toHowMany
	 *            specifiy how many items to be fetched
	 */
	public ArrayList<News> fetchNewsTable(int fromHowMany, int toHowMany) {
		return this.newsDBManager.fetchNewsTable(context, fromHowMany,
				toHowMany);
	}

	/**
	 * Saves items for local use in db.
	 * 
	 * @param news
	 *            to be saved
	 */
	public void persistAllNews(ArrayList<News> news) {
		Collections.reverse(news);
		this.newsDBManager.insertOnlyNew(context, news);
	}

	/**
	 * Updates the specified new in the db.
	 * 
	 * @param news
	 *            to update in db
	 */
	public void updateNews(News news) {
		this.newsDBManager.updateNews(context, news);
	}

	/**
	 * Updates all news as read.
	 */
	public void setAllNewsAsRead() {
		this.newsDBManager.setAllNewsAsRead(context);
	}

	/**
	 * Retrieves the count of unsaved news items.
	 * 
	 * @return the count
	 */
	public int getUnsavedNewsCount() {
		return this.newsDBManager.getUnsavedCount(context);
	}

	/**
	 * Retrieves the status of a news and reports it
	 * 
	 * @param news
	 *            to be reviewed
	 * @return true if status is set as read, false otherwise
	 */
	public boolean isNewsAlreadyRead(News news) {
		return this.newsDBManager.isNewsAlreadyRead(context, news);
	}

	/**
	 * Removes a favourite item from the db. Toasts about the event.
	 * 
	 * @param favourite
	 *            to remove from db
	 */
	public void deleteFavourite(Favourite favourite) {
		this.favouritesDBManager.deleteFavourite(context, favourite);
		AgentUtils.toastAddOrRemoveFavourite(context, true);
	}

	/**
	 * Removes a favourite item from the db, when the user accepts a dialog.
	 * Otherwise nothing happens and the dialog is dismissed.
	 * 
	 * @param ctx
	 *            the context to run the dialog on
	 * @param favourite
	 *            to remove from db if user decides to remove it
	 */
	public void deleteFavouriteWithDialog(Context ctx, Favourite favourite,
			OnClickListener positiveListerner, OnClickListener negativeListener) {
		AgentUtils.showDeleteFavouriteDialog(ctx, this, favourite,
				positiveListerner, negativeListener);
	}

	/**
	 * Fetches all specified favourites from a type.
	 * 
	 * @param which
	 *            the type to specify, see favourites constants
	 * @return the items
	 */
	public ArrayList<Favourite> fetchFavouritesTable(String which) {
		return this.favouritesDBManager.fetchItemsFromFavouritesTable(context,
				which);
	}

	/**
	 * Stores a specific favourite item in the db. Toasts about the event.
	 * 
	 * @param favourite
	 *            to persist
	 */
	public void persistFavourite(Favourite favourite) {
		this.favouritesDBManager.insertFavourite(context, favourite);
		AgentUtils.toastAddOrRemoveFavourite(context, false);
	}

	/**
	 * Retrieves the status of a favourite and reports it.
	 * 
	 * @param fav
	 *            to be reviewed
	 * @returntrue if status is set as read, false otherwise
	 */
	public boolean isFavAlreadySaved(Favourite fav) {
		return this.favouritesDBManager.isFavAlreadySaved(context, fav);
	}

}
