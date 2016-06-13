package com.voodoo.GadgetBridgeFiles.database.schema;

import android.database.sqlite.SQLiteDatabase;

import com.voodoo.GadgetBridgeFiles.database.DBHelper;
import com.voodoo.GadgetBridgeFiles.database.DBUpdateScript;

import static com.voodoo.GadgetBridgeFiles.database.DBConstants.TABLE_GBACTIVITYSAMPLES;

/**
 * Upgrade and downgrade with DB versions <= 5 is not supported.
 * Just recreates the default schema. Those GB versions may or may not
 * work with that, but this code will probably not create a DB for them
 * anyway.
 */
public class ActivityDBUpdate_4 extends ActivityDBCreationScript implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        recreateSchema(db);
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
        recreateSchema(db);
    }

    private void recreateSchema(SQLiteDatabase db) {
        DBHelper.dropTable(TABLE_GBACTIVITYSAMPLES, db);
        createSchema(db);
    }
}
