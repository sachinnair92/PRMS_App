package com.voodoo.GadgetBridgeFiles.database.schema;

import android.database.sqlite.SQLiteDatabase;

import com.voodoo.GadgetBridgeFiles.database.DBHelper;
import com.voodoo.GadgetBridgeFiles.database.DBUpdateScript;

import static com.voodoo.GadgetBridgeFiles.database.DBConstants.KEY_PROVIDER;
import static com.voodoo.GadgetBridgeFiles.database.DBConstants.KEY_STEPS;
import static com.voodoo.GadgetBridgeFiles.database.DBConstants.KEY_TIMESTAMP;
import static com.voodoo.GadgetBridgeFiles.database.DBConstants.TABLE_STEPS_PER_DAY;

/**
 * Adds a table "STEPS_PER_DAY".
 */
public class ActivityDBUpdate_X implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        String CREATE_STEPS_PER_DAY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_STEPS_PER_DAY + " ("
                + KEY_TIMESTAMP + " INT,"
                + KEY_PROVIDER + " TINYINT,"
                + KEY_STEPS + " MEDIUMINT,"
                + " PRIMARY KEY (" + KEY_TIMESTAMP + "," + KEY_PROVIDER + ") ON CONFLICT REPLACE)" + DBHelper.getWithoutRowId();
        db.execSQL(CREATE_STEPS_PER_DAY_TABLE);
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
        DBHelper.dropTable(TABLE_STEPS_PER_DAY, db);
    }
}
