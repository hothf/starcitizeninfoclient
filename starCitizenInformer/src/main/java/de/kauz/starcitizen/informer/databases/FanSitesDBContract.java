package de.kauz.starcitizen.informer.databases;

import android.provider.BaseColumns;

/**
 * Contract Class for DB fan site entries.
 * 
 * @author MadKauz
 * 
 */
public class FanSitesDBContract {
	
	public static final String DATABASE_NAME = "fansites.db";

	/**
	 * Empty constructor.
	 */
	private FanSitesDBContract() {
	}

	/**
	 * Abstract class used as entries for a DB.
	 * 
	 * @author MadKauz
	 * 
	 */
	public static abstract class DataBaseEntry implements BaseColumns {
		public static final String TABLE_NAME = "fansites";
		public static final String COLUMN_NAME_ENTRY_ID = "entryid";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_URL = "url";
		public static final String COLUMN_NAME_CATEGORY = "optional1";
		public static final String COLUMN_NAME_IMAGE_RES_ID = "optional2";
		public static final String COLUMN_NAME_OPTIONAL3 = "optional3";

		public static final String[] available = { BaseColumns._ID,
				COLUMN_NAME_ENTRY_ID, COLUMN_NAME_NAME, COLUMN_NAME_URL,
				COLUMN_NAME_CATEGORY, COLUMN_NAME_IMAGE_RES_ID,
				COLUMN_NAME_OPTIONAL3 };
	}

}
