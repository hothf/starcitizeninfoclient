package de.kauz.starcitizen.informer.databases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * ContentProvider to access a DB for this application.
 * 
 * @author MadKauz
 * 
 */
public class FanSitesDBContentprovider extends android.content.ContentProvider {

	private FanSitesDBHelper dbHelper;
	private static final String AUTHORITY = "de.kauz.starcitizen.informer.databases.FanSitesDBContentprovider";
	public static final String BASE_PATH = "fansites";

	private static final int CODE = 10;
	private static final int CODE_ID = 20;

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/fansite";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/fansite";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, CODE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CODE_ID);
	}

	/**
	 * Deletes the selection from the DB.
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case CODE:
			rowsDeleted = sqlDB.delete(
					FanSitesDBContract.DataBaseEntry.TABLE_NAME, selection,
					selectionArgs);
			break;
		case CODE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(
						FanSitesDBContract.DataBaseEntry.TABLE_NAME,
						BaseColumns._ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(
						FanSitesDBContract.DataBaseEntry.TABLE_NAME,
						BaseColumns._ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	/**
	 * Getter for a Uri type. RETURNS NULL!
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	/**
	 * Inserts values into the DB.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case CODE:
			id = sqlDB.insert(FanSitesDBContract.DataBaseEntry.TABLE_NAME,
					null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/**
	 * LifeCycle onCreate()
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new FanSitesDBHelper(getContext(), 1);
		return false;
	}

	/**
	 * Queries the DBs projection for a specific selection.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		checkColumns(projection);

		queryBuilder.setTables(FanSitesDBContract.DataBaseEntry.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case CODE:
			break;
		case CODE_ID:
			queryBuilder.appendWhere(BaseColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	/**
	 * Updates the DB with values of a selection.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case CODE:
			rowsUpdated = sqlDB.update(
					FanSitesDBContract.DataBaseEntry.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case CODE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(
						FanSitesDBContract.DataBaseEntry.TABLE_NAME, values,
						BaseColumns._ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(
						FanSitesDBContract.DataBaseEntry.TABLE_NAME, values,
						BaseColumns._ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;

	}

	/**
	 * Checks a projection for existence in the DB.
	 * 
	 * @param projection
	 *            the projection to be checked
	 */
	private void checkColumns(String[] projection) {

		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(FanSitesDBContract.DataBaseEntry.available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

	/**
	 * Applying a whole batch of operations.
	 */
	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		return super.applyBatch(operations);
	}

	/**
	 * Inserts multiple rows.
	 */
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final int match = sURIMatcher.match(uri);
		switch (match) {
		case CODE:
			int numInserted = 0;
			db.beginTransaction();

			try {
				for (ContentValues cv : values) {
					long newID = db.insertOrThrow(
							FanSitesDBContract.DataBaseEntry.TABLE_NAME, null,
							cv);

					if (newID <= 0) {
						throw new SQLException("Failed to insert row into "
								+ uri);
					}
				}
				db.setTransactionSuccessful();
				getContext().getContentResolver().notifyChange(uri, null);
				numInserted = values.length;
			} finally {
				db.endTransaction();
			}
			return numInserted;
		default:
			throw new UnsupportedOperationException("unsupported uri: " + uri);
		}
	}

}
