package com.voodoo.GadgetBridgeFiles.service.devices.pebble;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.GBException;
import com.voodoo.GadgetBridgeFiles.database.DBHandler;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEvent;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEventSendBytes;
import com.voodoo.GadgetBridgeFiles.devices.pebble.MisfitSampleProvider;
import com.voodoo.GadgetBridgeFiles.impl.GBActivitySample;
import com.voodoo.GadgetBridgeFiles.model.ActivityKind;

public class AppMessageHandlerMisfit extends AppMessageHandler {

    public static final int KEY_SLEEPGOAL = 1;
    public static final int KEY_STEP_ROGRESS = 2;
    public static final int KEY_SLEEP_PROGRESS = 3;
    public static final int KEY_VERSION = 4;
    public static final int KEY_SYNC = 5;
    public static final int KEY_INCOMING_DATA_BEGIN = 6;
    public static final int KEY_INCOMING_DATA = 7;
    public static final int KEY_INCOMING_DATA_END = 8;
    public static final int KEY_SYNC_RESULT = 9;

    private static final Logger LOG = LoggerFactory.getLogger(AppMessageHandlerMisfit.class);

    public AppMessageHandlerMisfit(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private final MisfitSampleProvider sampleProvider = new MisfitSampleProvider();

    @Override
    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        for (Pair<Integer, Object> pair : pairs) {
            switch (pair.first) {
                case KEY_INCOMING_DATA_BEGIN:
                    LOG.info("incoming data start");
                    break;
                case KEY_INCOMING_DATA_END:
                    LOG.info("incoming data end");
                    break;
                case KEY_INCOMING_DATA:
                    byte[] data = (byte[]) pair.second;
                    ByteBuffer buf = ByteBuffer.wrap(data);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    int timestamp = buf.getInt();
                    int key = buf.getInt();
                    int samples = (data.length - 8) / 2;
                    if (samples <= 0) {
                        break;
                    }

                    if (!mPebbleProtocol.isFw3x) {
                        timestamp -= SimpleTimeZone.getDefault().getOffset(timestamp * 1000L) / 1000;
                    }
                    Date startDate = new Date((long) timestamp * 1000L);
                    Date endDate = new Date((long) (timestamp + samples * 60) * 1000L);
                    LOG.info("got data from " + startDate + " to " + endDate);

                    int totalSteps = 0;
                    GBActivitySample[] activitySamples = new GBActivitySample[samples];
                    for (int i = 0; i < samples; i++) {
                        short sample = buf.getShort();
                        int steps = 0;
                        int intensity = 0;
                        int activityKind = ActivityKind.TYPE_UNKNOWN;

                        if (((sample & 0x83ff) == 0x0001) && ((sample & 0xff00) <= 0x4800)) {
                            // sleep seems to be from 0x2401 to 0x4801  (0b0IIIII0000000001) where I = intensity ?
                            intensity = (sample & 0x7c00) >>> 10;
                            // 9-18 decimal after shift
                            if (intensity <= 13) {
                                activityKind = ActivityKind.TYPE_DEEP_SLEEP;
                            } else {
                                // FIXME: this leads to too much false positives, ignore for now
                                //activityKind = ActivityKind.TYPE_LIGHT_SLEEP;
                                //intensity *= 2; // better visual distinction
                            }
                        } else {
                            if ((sample & 0x0001) == 0) { // 16-??? steps encoded in bits 1-7
                                steps = (sample & 0x00fe);
                            } else { // 0-14 steps encoded in bits 1-3, most of the time fc71 bits are set in that case
                                steps = (sample & 0x000e);
                            }
                            intensity = steps;
                            activityKind = ActivityKind.TYPE_ACTIVITY;
                        }

                        totalSteps += steps;
                        LOG.info("got steps for sample " + i + " : " + steps + "(" + Integer.toHexString(sample & 0xffff) + ")");

                        activitySamples[i] = new GBActivitySample(sampleProvider, timestamp + i * 60, intensity, steps, activityKind);
                    }
                    LOG.info("total steps for above period: " + totalSteps);

                    DBHandler db = null;
                    try {
                        db = GBApplication.acquireDB();
                        db.addGBActivitySamples(activitySamples);
                    } catch (GBException e) {
                        LOG.error("Error acquiring database", e);
                        return null;
                    } finally {
                        if (db != null) {
                            db.release();
                        }
                    }
                    break;
                default:
                    LOG.info("unhandled key: " + pair.first);
                    break;
            }
        }

        // always ack
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = mPebbleProtocol.encodeApplicationMessageAck(mUUID, mPebbleProtocol.last_id);

        return new GBDeviceEvent[]{sendBytesAck};
    }
}
