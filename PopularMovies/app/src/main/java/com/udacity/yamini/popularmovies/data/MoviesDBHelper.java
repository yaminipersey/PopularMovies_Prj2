package com.udacity.yamini.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MoviesDBHelper extends SQLiteOpenHelper {
	public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

	//name & version
	private static final String DATABASE_NAME = "movies.db";
	private static final int DATABASE_VERSION = 12;

	public MoviesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create the database
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

		try {

			final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
					MoviesContract.MoviesEntry.TABLE_MOVIES + "(" + MoviesContract.MoviesEntry._ID +
					" INTEGER PRIMARY KEY AUTOINCREMENT, " +
					MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

					MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
					MoviesContract.MoviesEntry.COLUMN_POSTERPATH +
					" TEXT NOT NULL, " +
					MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE +
					" TEXT NOT NULL," +
					MoviesContract.MoviesEntry.COLUMN_RATING +
					" TEXT NOT NULL," +
					MoviesContract.MoviesEntry.COLUMN_PLOT +
					" TEXT NOT NULL,"
					+ " UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


			sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
		}catch(Exception ex) {
			Log.v("Database",ex.getMessage());
		}

	}

	// Upgrade database when version is changed.
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ". OLD DATA WILL BE DESTROYED");
		// Drop the table
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
				MoviesContract.MoviesEntry.TABLE_MOVIES + "'");

		// re-create database
		onCreate(sqLiteDatabase);
	}
}
