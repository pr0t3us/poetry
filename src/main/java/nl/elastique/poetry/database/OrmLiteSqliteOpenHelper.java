package nl.elastique.poetry.database;

import com.tylersuehr.sql.SQLiteDatabase;
import com.tylersuehr.sql.SQLiteOpenHelper;

public abstract class OrmLiteSqliteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "johnny_vaughn_db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public OrmLiteSqliteOpenHelper(String dbPath, String dbName, int version) {
        super(dbPath, dbName, version);
        init();
    }

    public OrmLiteSqliteOpenHelper() {
        super("", DB_NAME, DB_VERSION);
        init();
    }

    private void init() {
        this.db = getWritableInstance();
    }

    @Override
    protected abstract void onCreate(SQLiteDatabase sqLiteDatabase);

    @Override
    protected abstract void onUpdate(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}
