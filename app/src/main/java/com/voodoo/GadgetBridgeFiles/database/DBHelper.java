package com.voodoo.GadgetBridgeFiles.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.util.FileUtils;

public class DBHelper {
    private final Context context;

    public DBHelper(Context context) {
        this.context = context;
    }

    private String getClosedDBPath(SQLiteOpenHelper dbHandler) throws IllegalStateException {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String path = db.getPath();
        db.close();
        if (db.isOpen()) { // reference counted, so may still be open
            throw new IllegalStateException("Database must be closed");
        }
        return path;
    }

    public File exportDB(SQLiteOpenHelper dbHandler, File toDir) throws IllegalStateException, IOException {
        String dbPath = getClosedDBPath(dbHandler);
        File sourceFile = new File(dbPath);
        File destFile = new File(toDir, sourceFile.getName());
        if (destFile.exists()) {
            File backup = new File(toDir, destFile.getName() + "_" + getDate());
            destFile.renameTo(backup);
        } else if (!toDir.exists()) {
            if (!toDir.mkdirs()) {
                throw new IOException("Unable to create directory: " + toDir.getAbsolutePath());
            }
        }

        FileUtils.copyFile(sourceFile, destFile);
        return destFile;
    }

    private String getDate() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
    }

    public void importDB(SQLiteOpenHelper dbHandler, File fromFile) throws IllegalStateException, IOException {
        String dbPath = getClosedDBPath(dbHandler);
        File toFile = new File(dbPath);
        FileUtils.copyFile(fromFile, toFile);
    }

    public void validateDB(SQLiteOpenHelper dbHandler) throws IOException {
        try (SQLiteDatabase db = dbHandler.getReadableDatabase()) {
            if (!db.isDatabaseIntegrityOk()) {
                throw new IOException("Database integrity is not OK");
            }
        }
    }

    public static void dropTable(String tableName, SQLiteDatabase db) {
        String statement = "DROP TABLE IF EXISTS '" + tableName + "'";
        db.execSQL(statement);
    }

    public static boolean existsColumn(String tableName, String columnName, SQLiteDatabase db) {
        try (Cursor res = db.rawQuery("PRAGMA table_info('" + tableName + "')", null)) {
            int index = res.getColumnIndex("name");
            if (index < 1) {
                return false; // something's really wrong
            }
            while (res.moveToNext()) {
                String cn = res.getString(index);
                if (columnName.equals(cn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * WITHOUT ROWID is only available with sqlite 3.8.2, which is available
     * with Lollipop and later.
     *
     * @return the "WITHOUT ROWID" string or an empty string for pre-Lollipop devices
     */
    public static String getWithoutRowId() {
        if (GBApplication.isRunningLollipopOrLater()) {
            return " WITHOUT ROWID;";
        }
        return "";
    }
}
