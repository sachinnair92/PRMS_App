package com.voodoo.GadgetBridgeFiles.impl;

import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;
import com.voodoo.GadgetBridgeFiles.model.ActivitySample;
import com.voodoo.GadgetBridgeFiles.util.DateTimeUtils;

public class GBActivitySample implements ActivitySample {
    private final int timestamp;
    private final SampleProvider provider;
    private final int intensity;
    private final int steps;
    private final int type;
    private final int customValue;

    public GBActivitySample(SampleProvider provider, int timestamp, int intensity, int steps, int type) {
        this(provider, timestamp, intensity, steps, type, 0);
    }

    public GBActivitySample(SampleProvider provider, int timestamp, int intensity, int steps, int type, int customValue) {
        this.timestamp = timestamp;
        this.provider = provider;
        this.intensity = intensity;
        this.steps = steps;
        this.customValue = customValue;
        this.type = type;
        validate();
    }

    private void validate() {
        if (steps < 0) {
            throw new IllegalArgumentException("steps must be >= 0");
        }
        if (intensity < 0) {
            throw new IllegalArgumentException("intensity must be >= 0");
        }
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp must be >= 0");
        }
        if (customValue < 0) {
            throw new IllegalArgumentException("customValue must be >= 0");
        }
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public SampleProvider getProvider() {
        return provider;
    }

    @Override
    public int getRawIntensity() {
        return intensity;
    }

    @Override
    public float getIntensity() {
        return getProvider().normalizeIntensity(getRawIntensity());
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public int getRawKind() {
        return type;
    }

    @Override
    public int getKind() {
        return getProvider().normalizeType(getRawKind());
    }

    @Override
    public int getCustomValue() {
        return customValue;
    }

    @Override
    public String toString() {
        return "GBActivitySample{" +
                "timestamp=" + DateTimeUtils.formatDateTime(DateTimeUtils.parseTimeStamp(timestamp)) +
                ", intensity=" + getIntensity() +
                ", steps=" + getSteps() +
                ", customValue=" + getCustomValue() +
                ", type=" + getKind() +
                '}';
    }
}
