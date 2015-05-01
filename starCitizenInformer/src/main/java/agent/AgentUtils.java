package agent;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.model.Favourite;
import de.kauz.starcitizen.informer.utils.MyApp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * Utility class which provides convenience methods for an agent.
 * 
 * @author MadKauz
 * 
 */
public class AgentUtils {

	/**
	 * Shows a dialog for choosing if a favourite shall be removed or not.
	 * 
	 * @param context
	 *            of the dialog
	 * @param agent
	 *            to perform the remove
	 * @param fav
	 *            the favourite to be removed or not
	 */
	public static void showDeleteFavouriteDialog(final Context context,
			final Agent agent, final Favourite fav,
			OnClickListener positiveListener, OnClickListener negativeListener) {

		if (context != null) {

			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			builder.setMessage(context.getResources().getString(
					R.string.agentRemoveFavouriteDialogMessage));
			builder.setTitle(context.getResources().getString(
					R.string.agentRemoveFavouriteDialogTitle));

			builder.setPositiveButton(
					context.getResources().getString(
							R.string.agentRemoveFavouriteDialogPositiveButton),
					positiveListener);

			builder.setNegativeButton(
					context.getResources().getString(R.string.Cancel),
					negativeListener);

			builder.show();
		}
	}

	/**
	 * Shows a toast when a removing or adding of a favourite has taken place.
	 * 
	 * @param context
	 *            of the toast
	 * @param isRemoving
	 *            true if favourite was removed, false otherwise
	 */
	public static void toastAddOrRemoveFavourite(final Context context,
			boolean isRemoving) {

		if (context != null) {

			if (isRemoving) {
				MyApp.getInstance().showError(
						context,
						context.getResources().getString(
								R.string.agentRemovedFav));
			} else {
				MyApp.getInstance().showError(
						context,
						context.getResources()
								.getString(R.string.agentAddedFav));
			}
		}

	}

}
