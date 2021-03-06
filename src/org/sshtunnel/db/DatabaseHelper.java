package org.sshtunnel.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "sshtunnel.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 5;

	// the DAO object we use to access the SimpleData table
	private Dao<Profile, Integer> profileDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		profileDao = null;
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<Profile, Integer> getProfileDao() throws SQLException {
		if (profileDao == null) {
			profileDao = getDao(Profile.class);
		}
		return profileDao;
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Profile.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL("ALTER TABLE Profile ADD COLUMN isDNSProxy BOOLEAN");
			db.execSQL("UPDATE Profile SET isDNSProxy=1");
		case 2:
			db.execSQL("ALTER TABLE Profile ADD COLUMN isActive BOOLEAN");
			db.execSQL("UPDATE Profile SET isActive=0");
		case 3:
			db.execSQL("ALTER TABLE Profile ADD COLUMN isUpstreamProxy BOOLEAN");
			db.execSQL("UPDATE Profile SET isUpstreamProxy=0");
			db.execSQL("ALTER TABLE Profile ADD COLUMN upstreamProxy VARCHAR");
			db.execSQL("UPDATE Profile SET upstreamProxy=''");
		case 4:
			db.execSQL("ALTER TABLE Profile ADD COLUMN fingerPrint VARCHAR");
			db.execSQL("UPDATE Profile SET fingerPrint=''");
			db.execSQL("ALTER TABLE Profile ADD COLUMN fingerPrintType VARCHAR");
			db.execSQL("UPDATE Profile SET fingerPrintType=''");
			break;
		default:
			try {
				Log.i(DatabaseHelper.class.getName(), "onUpgrade");
				TableUtils.dropTable(connectionSource, Profile.class, true);
				// after we drop the old databases, we create the new ones
				onCreate(db, connectionSource);
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
				throw new RuntimeException(e);
			}
		}
	}
}
