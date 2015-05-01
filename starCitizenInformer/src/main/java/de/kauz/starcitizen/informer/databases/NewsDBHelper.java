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
public class NewsDBHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ NewsDBContract.DataBaseEntry.TABLE_NAME + " ("
			+ NewsDBContract.DataBaseEntry._ID + " INTEGER PRIMARY KEY,"
			+ NewsDBContract.DataBaseEntry.COLUMN_ENTRY_ID + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_TYPE + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_TITLE + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_URL + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_DESCRIPTION
			+ TEXT_TYPE + COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_STATUS + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_DATE + TEXT_TYPE
			+ COMMA_SEP + NewsDBContract.DataBaseEntry.COLUMN_COMMENTS
			+ TEXT_TYPE + COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_HASH
			+ TEXT_TYPE + COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_OPTIONAL1 + TEXT_TYPE + ")";

	private static final String TABLE_NAME_ENTRIES = NewsDBContract.DataBaseEntry.COLUMN_ENTRY_ID
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_TYPE
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_TITLE
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_URL
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_DESCRIPTION
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_STATUS
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_DATE
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_COMMENTS
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_HASH
			+ COMMA_SEP
			+ NewsDBContract.DataBaseEntry.COLUMN_OPTIONAL1 + COMMA_SEP;

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
	public NewsDBHelper(Context context, int version) {
		super(context, NewsDBContract.DATABASE_NAME, null, version);
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
