package com.eebbk.bfc.uploadsdk.repo.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.SQLException;
import android.support.annotation.VisibleForTesting;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

@Database(entities = {NewBfcUploads.class}, version = 111)
public abstract class UploadSdkDatabase extends RoomDatabase{
    private static volatile UploadSdkDatabase INSTANCE;
    private static final String DB_NAME = "newbfcupload.db";
    private static final String UPLOAD_TABLE_NAME = "newbfcuploads";

    public abstract NewBfcUploadsDao newBfcUploadsDao();
    @VisibleForTesting
    private static final Migration MIGRATION_110_111 = new Migration(110, 111) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            LogUtils.i("migrate:");
            if(!database.isOpen()){
                return;
            }
            //overWrite， deleted 在110版本的表中是BOOLEAN
            //对象中的字段类型无法映射至表中列类型为BOOLEAN
            //对象字段类型为BOOLEAN，经过room的映射对应的表中列类型为INTEGER
            String temp_table = "temp_table";
            createUploadsTable(database,temp_table);
            database.execSQL(
                    "INSERT INTO "+ temp_table +" SELECT " +
                            Impl._ID + "," +
                            Impl.COLUMN_URL + "," +
                            Impl.COLUMN_TASK_TYPE + "," +
                            Impl.COLUMN_FILE_NAME + "," +
                            Impl.COLUMN_FILE_PATH + "," +
                            Impl.COLUMN_PRIORITY + "," +
                            Impl.COLUMN_STATUS + "," +
                            Impl.COLUMN_VISIBILITY + "," +
                            Impl.COLUMN_CONTROL + "," +
                            Impl.COLUMN_LAST_MODIFICATION + "," +
                            Impl.COLUMN_EXTRAS + "," +
                            Impl.COLUMN_CURRENT_SPEED + "," +
                            Impl.COLUMN_ALLOW_METERED + "," +
                            Impl.COLUMN_ERROR_MSG + "," +
                            Impl.COLUMN_DELETED + "," +
                            Impl.COLUMN_ALLOW_ROAMING + "," +
                            Impl.COLUMN_ALLOWED_NETWORK_TYPES + "," +
                            Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT + "," +
                            Impl.COLUMN_CURRENT_BYTES + "," +
                            Constants.FAILED_CONNECTIONS + "," +
                            Impl.COLUMN_FILE_SIZE + "," +
                            Impl.COLUMN_EXTRA_FILES + "," +
                            Impl.COLUMN_OUTPUT + "," +
                            Impl.COLUMN_NOTIFICATION_PACKAGE + "," +
                            Impl.COLUMN_NOTIFICATION_CLASS + "," +
                            Impl.COLUMN_NOTIFICATION_EXTRAS + "," +
                            Impl.COLUMN_OVERWRITE+
                            " FROM "+ UploadSdkDatabase.UPLOAD_TABLE_NAME);
            database.execSQL("DROP TABLE "+UploadSdkDatabase.UPLOAD_TABLE_NAME);
            database.execSQL("ALTER TABLE "+ temp_table +" RENAME TO "+UploadSdkDatabase.UPLOAD_TABLE_NAME);
            addColumn(database,UploadSdkDatabase.UPLOAD_TABLE_NAME,Impl.JOB_ID,"INTEGER NOT NULL DEFAULT -1");

        }
    };

    public static UploadSdkDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UploadSdkDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UploadSdkDatabase.class, UploadSdkDatabase.DB_NAME)
                            .addMigrations(MIGRATION_110_111)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void createUploadsTable(SupportSQLiteDatabase db, String tableName) {
        try {
            db.execSQL("CREATE TABLE " +tableName + " (" +
                    Impl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Impl.COLUMN_URL + " TEXT, " +
                    Impl.COLUMN_TASK_TYPE + " INTEGER, " +
                    Impl.COLUMN_FILE_NAME + " TEXT, " +
                    Impl.COLUMN_FILE_PATH + " TEXT, " +
                    Impl.COLUMN_PRIORITY + " INTEGER, " +
                    Impl.COLUMN_STATUS + " INTEGER, " +
                    Impl.COLUMN_VISIBILITY + " INTEGER, " +
                    Impl.COLUMN_CONTROL + " INTEGER, " +
                    Impl.COLUMN_LAST_MODIFICATION + " INTEGER, " +
                    Impl.COLUMN_EXTRAS + " TEXT, " +
                    Impl.COLUMN_CURRENT_SPEED + " INTEGER DEFAULT 0, " +
                    Impl.COLUMN_ALLOW_METERED + " INTEGER NOT NULL DEFAULT 1, " +
                    Impl.COLUMN_ERROR_MSG + " TEXT, " +
                    Impl.COLUMN_DELETED + " INTEGER NOT NULL DEFAULT 0, " +
                    Impl.COLUMN_ALLOW_ROAMING + " INTEGER NOT NULL DEFAULT 0, " +
                    Impl.COLUMN_ALLOWED_NETWORK_TYPES + " INTEGER NOT NULL DEFAULT 0, " +
                    Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT + " INTEGER NOT NULL DEFAULT 0, " +
                    Impl.COLUMN_CURRENT_BYTES + " INTEGER, " +
                    Constants.FAILED_CONNECTIONS + " INTEGER, " +
                    Impl.COLUMN_FILE_SIZE + " INTEGER, " +
                    Impl.COLUMN_EXTRA_FILES + " TEXT, " +
                    Impl.COLUMN_OUTPUT + " TEXT, " +
                    Impl.COLUMN_NOTIFICATION_PACKAGE + " TEXT, " +
                    Impl.COLUMN_NOTIFICATION_CLASS + " TEXT, " +
                    Impl.COLUMN_NOTIFICATION_EXTRAS + " TEXT, " +
                    Impl.COLUMN_OVERWRITE+ " INTEGER NOT NULL DEFAULT 0);");
        } catch (SQLException ex) {
            LogUtils.e(ex, "couldn't create table in uploads database");
            throw ex;
        }
    }

    private static void addColumn(SupportSQLiteDatabase db, String dbTable, String columnName,
                           String columnDefinition) {
        db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
                + columnDefinition);
    }
}
