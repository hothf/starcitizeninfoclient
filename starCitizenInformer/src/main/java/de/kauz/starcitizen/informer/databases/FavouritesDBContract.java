package de.kauz.starcitizen.informer.databases;

import android.provider.BaseColumns;

/**
 * Contract Class for DB favourite  entries.
 * 
 * @author MadKauz
 * 
 */
public class FavouritesDBContract {

	public static final String DATABASE_NAME = "favourites.db";

	/**
	 * Empty constructor.
	 */
	private FavouritesDBContract() {
	}

	/**
	 * Abstract class used as entries for a DB.
	 * 
	 * @author MadKauz
	 * 
	 */
	public static abstract class DataBaseEntry implements BaseColumns {
		public static final String TABLE_NAME = "favourites";
		public static final String COLUMN_ENTRY_ID = "entryid";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_URL = "URL";
		public static final String COLUMN_DESCRIPTION = "description";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_OPTIONAL1 = "optional1";

		public static final String[] available = { BaseColumns._ID,
				COLUMN_ENTRY_ID, COLUMN_TYPE, COLUMN_TITLE,
				COLUMN_URL, COLUMN_DESCRIPTION, COLUMN_DATE,
				COLUMN_OPTIONAL1 };
	}

}
