package com.voodoo.GadgetBridgeFiles.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;
import com.voodoo.GadgetBridgeFiles.model.ActivitySample;

public interface DBHandler {
    SQLiteOpenHelper getHelper();

    /**
     * Releases the DB handler. No access may be performed after calling this method.
     * Same as calling {@link GBApplication#releaseDB()}
     */
    void release();

    List<ActivitySample> getAllActivitySamples(int tsFrom, int tsTo, SampleProvider provider);

    List<ActivitySample> getActivitySamples(int tsFrom, int tsTo, SampleProvider provider);

    List<ActivitySample> getSleepSamples(int tsFrom, int tsTo, SampleProvider provider);

    void addGBActivitySample(int timestamp, int provider, int intensity, int steps, int kind, int heartrate);

    void addGBActivitySamples(ActivitySample[] activitySamples);

    SQLiteDatabase getWritableDatabase();

    void changeStoredSamplesType(int timestampFrom, int timestampTo, int kind, SampleProvider provider);

    void changeStoredSamplesType(int timestampFrom, int timestampTo, int fromKind, int toKind, SampleProvider provider);

    int fetchLatestTimestamp(SampleProvider provider);

}
