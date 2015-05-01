package de.kauz.starcitizen.informer.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper Class for easy access to the application DB.
 * 
 * @author MadKauz
 * 
 */
public class FanSitesDBHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ FanSitesDBContract.DataBaseEntry.TABLE_NAME + " ("
			+ FanSitesDBContract.DataBaseEntry._ID + " INTEGER PRIMARY KEY,"
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE
			+ COMMA_SEP + FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME
			+ TEXT_TYPE + COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_URL + TEXT_TYPE
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_CATEGORY
			+ TEXT_TYPE + COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_IMAGE_RES_ID
			+ TEXT_TYPE + COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_OPTIONAL3
			+ TEXT_TYPE + ")";

	private static final String TABLE_NAME_ENTRIES = FanSitesDBContract.DataBaseEntry.COLUMN_NAME_ENTRY_ID
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_NAME
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_URL
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_CATEGORY
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_IMAGE_RES_ID
			+ COMMA_SEP
			+ FanSitesDBContract.DataBaseEntry.COLUMN_NAME_OPTIONAL3
			+ COMMA_SEP;

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME_ENTRIES;

	/**
	 * Creates the helper with a given context and a version.
	 * 
	 * @param context
	 *            the context
	 * @param version
	 *            the version
	 */
	public FanSitesDBHelper(Context context, int version) {
		super(context, FanSitesDBContract.DATABASE_NAME, null, version);
	}

	/**
	 * LifeCycle onCreate()
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	/**
	 * DB onUpgrade()
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	/**
	 * DB onDowngrade
	 */
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
