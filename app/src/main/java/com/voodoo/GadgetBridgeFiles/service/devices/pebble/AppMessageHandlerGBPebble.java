package com.voodoo.GadgetBridgeFiles.service.devices.pebble;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.GBException;
import com.voodoo.GadgetBridgeFiles.database.DBHandler;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEvent;
import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEventSendBytes;
import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;

public class AppMessageHandlerGBPebble extends AppMessageHandler {

    public static final int KEY_TIMESTAMP = 1;
    public static final int KEY_SAMPLES = 2;

    private static final Logger LOG = LoggerFactory.getLogger(AppMessageHandlerGBPebble.class);

    AppMessageHandlerGBPebble(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    @Override
    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        int timestamp = 0;
        for (Pair<Integer, Object> pair : pairs) {
            switch (pair.first) {
                case KEY_TIMESTAMP:
                    TimeZone tz = SimpleTimeZone.getDefault();
                    timestamp = (int) pair.second - (tz.getOffset(System.currentTimeMillis())) / 1000;
                    LOG.info("got timestamp " + timestamp);
                    break;
                case KEY_SAMPLES:
                    byte[] samples = (byte[]) pair.second;
                    ByteBuffer samplesBuffer = ByteBuffer.wrap(samples);
                    samplesBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    int samples_remaining = samples.length / 2;
                    LOG.info("got " + samples_remaining + " samples");
                    int offset_seconds = 0;
                    DBHandler db = null;
                    try {
                        db = GBApplication.acquireDB();
                        while (samples_remaining-- > 0) {
                            short sample = samplesBuffer.getShort();
                            int type = ((sample & 0xe000) >>> 13);
                            int intensity = ((sample & 0x1f80) >>> 7);
                            int steps = (sample & 0x007f);
                            db.addGBActivitySample(timestamp + offset_seconds, SampleProvider.PROVIDER_PEBBLE_GADGETBRIDGE, intensity, steps, type, 0);
                            offset_seconds += 60;
                        }
                    } catch (GBException e) {
                        LOG.error("Error acquiring database", e);
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
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = mPebbleProtocol.encodeApplicationMessageAck(mUUID, mPebbleProtocol.last_id);
        return new GBDeviceEvent[]{sendBytes};
    }
}
