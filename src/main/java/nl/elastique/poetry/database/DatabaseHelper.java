package nl.elastique.poetry.database;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.tylersuehr.sql.SQLiteDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final Logger sLogger = LoggerFactory.getLogger(DatabaseHelper.class);

    private static DatabaseConfiguration sConfiguration;

    protected static final HashMap<Class<?>, Dao<?, ?>> sCachedDaos = new HashMap<>();

    public DatabaseHelper() {
        super(null, sConfiguration.getDatabaseName(), sConfiguration.getModelVersion());
    }

    public DatabaseHelper(DatabaseConfiguration configuration) {
        super(null, configuration.getDatabaseName(), configuration.getModelVersion());

        sConfiguration = configuration;
    }

    public static void setConfiguration(DatabaseConfiguration configuration) {
        sConfiguration = configuration;
    }

    public static DatabaseHelper getHelper() {
        return OpenHelperManager.getHelper(DatabaseHelper.class);
    }

    public static <T extends DatabaseHelper> T getHelper(Class<T> classObject) {
        return OpenHelperManager.getHelper(classObject);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
    }

    public void createTable(Class<?> classObject) {
        try {
            TableUtils.createTable(getConnectionSource(), classObject);
        } catch (SQLException e) {
            sLogger.error(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    public <T> void dropTable(Class<T> classObject) {
        try {
            TableUtils.dropTable(getConnectionSource(), classObject, true);

            if (sCachedDaos.containsKey(classObject)) {
                sCachedDaos.remove(classObject);
            }
        } catch (SQLException e) {
            sLogger.error("can't drop table", e);
        }
    }

    public void recreateDatabase() {
        dropDatabase();
        createDatabase();
    }

    /**
     * Drops all tables.
     */
    public void dropDatabase() {
        for (Class<?> classObject : sConfiguration.getModelClasses()) {
            dropTable(classObject);
        }
    }

    private void createDatabase() {
        for (Class<?> classObject : sConfiguration.getModelClasses()) {
            createTable(classObject);
        }
    }

    @Override
    public <D extends Dao<T, ?>, T> D getDao(java.lang.Class<T> clazz) throws java.sql.SQLException {
        @SuppressWarnings("unchecked")
        D dao = (D) sCachedDaos.get(clazz);

        if (dao == null) {
            dao = super.getDao(clazz);
            sCachedDaos.put(clazz, dao);
        }
        return dao;
    }

    @Override
    protected void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase();
    }

    @Override
    protected void onUpdate(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        recreateDatabase();
    }
}
