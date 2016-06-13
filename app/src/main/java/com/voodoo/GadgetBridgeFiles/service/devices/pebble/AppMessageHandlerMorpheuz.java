package com.voodoo.GadgetBridgeFiles.service.devices.pebble;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.GBException;
import com.voodoo.GadgetBridgeFiles.database.DBHandler;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEvent;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEventSendBytes;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEventSleepMonitorResult;
import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;
import com.voodoo.GadgetBridgeFiles.devices.pebble.MorpheuzSampleProvider;

public class AppMessageHandlerMorpheuz extends AppMessageHandler {

    public static final int KEY_POINT = 1;
    public static final int KEY_CTRL = 2;
    public static final int KEY_FROM = 3;
    public static final int KEY_TO = 4;
    public static final int KEY_BASE = 5;
    public static final int KEY_VERSION = 6;
    public static final int KEY_GONEOFF = 7;
    public static final int KEY_TRANSMIT = 8;
    public static final int KEY_AUTO_RESET = 9;

    public static final int CTRL_TRANSMIT_DONE = 1;
    public static final int CTRL_VERSION_DONE = 2;
    public static final int CTRL_GONEOFF_DONE = 4;
    public static final int CTRL_DO_NEXT = 8;
    public static final int CTRL_SET_LAST_SENT = 16;

    // data received from Morpheuz in native format
    private int smartalarm_from = -1; // time in minutes relative from 0:00 for smart alarm (earliest)
    private int smartalarm_to = -1;// time in minutes relative from 0:00 for smart alarm (latest)
    private int recording_base_timestamp = -1; // timestamp for the first "point", all folowing are +10 minutes offset each
    private int alarm_gone_off = -1; // time in minutes relative from 0:00 when alarm gone off

    private static final Logger LOG = LoggerFactory.getLogger(AppMessageHandlerMorpheuz.class);

    public AppMessageHandlerMorpheuz(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private byte[] encodeMorpheuzMessage(int key, int value) {
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        pairs.add(new Pair<Integer, Object>(key, value));

        return mPebbleProtocol.encodeApplicationMessagePush(PebbleProtocol.ENDPOINT_APPLICATIONMESSAGE, mUUID, pairs);
    }

    @Override
    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        int ctrl_message = 0;
        GBDeviceEventSleepMonitorResult sleepMonitorResult = null;

        for (Pair<Integer, Object> pair : pairs) {
            switch (pair.first) {
                case KEY_TRANSMIT:
                case KEY_GONEOFF:
                    if (pair.first == KEY_GONEOFF) {
                        alarm_gone_off = (int) pair.second;
                        LOG.info("got gone off: " + alarm_gone_off / 60 + ":" + alarm_gone_off % 60);
                    }
                    sleepMonitorResult = new GBDeviceEventSleepMonitorResult();
                    sleepMonitorResult.smartalarm_from = smartalarm_from;
                    sleepMonitorResult.smartalarm_to = smartalarm_to;
                    sleepMonitorResult.alarm_gone_off = alarm_gone_off;
                    sleepMonitorResult.recording_base_timestamp = recording_base_timestamp;
                    break;
                case KEY_POINT:
                    if (recording_base_timestamp == -1) {
                        // we have no base timestamp but received points, stop this
                        ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_GONEOFF_DONE | AppMessageHandlerMorpheuz.CTRL_TRANSMIT_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT;
                    } else {
                        int index = ((int) pair.second >> 16);
                        int intensity = ((int) pair.second & 0xffff);
                        LOG.info("got point:" + index + " " + intensity);
                        int type = MorpheuzSampleProvider.TYPE_UNKNOWN;
                        if (intensity <= 120) {
                            type = MorpheuzSampleProvider.TYPE_DEEP_SLEEP;
                        } else if (intensity <= 1000) {
                            type = MorpheuzSampleProvider.TYPE_LIGHT_SLEEP;
                        }
                        if (index >= 0) {
                            DBHandler db = null;
                            try {
                                db = GBApplication.acquireDB();
                                db.addGBActivitySample(recording_base_timestamp + index * 600, SampleProvider.PROVIDER_PEBBLE_MORPHEUZ, intensity, 0, type, 0);
                            } catch (GBException e) {
                                LOG.error("Error acquiring database", e);
                            } finally {
                                if (db != null) {
                                    db.release();
                                }
                            }
                        }

                        ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT | AppMessageHandlerMorpheuz.CTRL_DO_NEXT;
                    }
                    break;
                case KEY_FROM:
                    smartalarm_from = (int) pair.second;
                    LOG.info("got from: " + smartalarm_from / 60 + ":" + smartalarm_from % 60);
                    ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT | AppMessageHandlerMorpheuz.CTRL_DO_NEXT;
                    break;
                case KEY_TO:
                    smartalarm_to = (int) pair.second;
                    LOG.info("got to: " + smartalarm_to / 60 + ":" + smartalarm_to % 60);
                    ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT | AppMessageHandlerMorpheuz.CTRL_DO_NEXT;
                    break;
                case KEY_VERSION:
                    LOG.info("got version: " + ((float) ((int) pair.second) / 10.0f));
                    ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE;
                    break;
                case KEY_BASE:
                    // fix timestamp
                    TimeZone tz = SimpleTimeZone.getDefault();
                    recording_base_timestamp = (int) pair.second - (tz.getOffset(System.currentTimeMillis())) / 1000;
                    LOG.info("got base: " + recording_base_timestamp);
                    ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT | AppMessageHandlerMorpheuz.CTRL_DO_NEXT;
                    break;
                case KEY_AUTO_RESET:
                    ctrl_message = AppMessageHandlerMorpheuz.CTRL_VERSION_DONE | AppMessageHandlerMorpheuz.CTRL_SET_LAST_SENT | AppMessageHandlerMorpheuz.CTRL_DO_NEXT;
                    break;
                default:
                    LOG.info("unhandled key: " + pair.first);
                    break;
            }
        }

        // always ack
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = mPebbleProtocol.encodeApplicationMessageAck(mUUID, mPebbleProtocol.last_id);

        // sometimes send control message
        GBDeviceEventSendBytes sendBytesCtrl = null;
        if (ctrl_message > 0) {
            sendBytesCtrl = new GBDeviceEventSendBytes();
            sendBytesCtrl.encodedBytes = encodeMorpheuzMessage(AppMessageHandlerMorpheuz.KEY_CTRL, ctrl_message);
        }

        // ctrl and sleep monitor might be null, thats okay
        return new GBDeviceEvent[]{sendBytesAck, sendBytesCtrl, sleepMonitorResult};
    }
}
