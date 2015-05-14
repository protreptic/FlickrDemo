package skbkonturcontest.flickr.storage;

import java.io.File;
import java.sql.SQLException;

import org.javaprotrepticon.android.androidutils.Apps;

import skbkonturcontest.flickr.storage.model.Photo;
import skbkonturcontest.flickr.storage.model.Place;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Storage {
	
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private JdbcPooledConnectionSource mConnection;
	
	public static void dropStorage(Context context) {
		File file = new File(context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + context.getPackageName() + "-" + Apps.getVersionName(context) + ".h2.db");
		
		file.delete();
	}
	
	public static void initialize(Context context) {
		Storage storage = new Storage(context);
		
		try {
			TableUtils.createTableIfNotExists(storage.getConnection(), Place.class);
			TableUtils.createTableIfNotExists(storage.getConnection(), Photo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		storage.closeConnection();
	}
	
	public Storage(Context context) {
		String storageFolder = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + context.getPackageName() + "-" + Apps.getVersionName(context);
		
		try {
			mConnection = new JdbcPooledConnectionSource("jdbc:h2:" + storageFolder + ";AUTO_SERVER=TRUE;IGNORECASE=TRUE;"); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public JdbcPooledConnectionSource getConnection() {
		return mConnection;
	}
	
	public void closeConnection() {
		try {
			mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Dao<?, String> createDao(Class<?> type) {
		Dao<?, String> result = null;
		
		try {
			result = (Dao<?, String>) DaoManager.createDao(mConnection, type);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
