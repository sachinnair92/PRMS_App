package com.voodoo.GadgetBridgeFiles.model;

import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;

public interface ActivitySample {
    /**
     * Returns the provider of the data.
     *
     * @return who created the sample data
     */
    SampleProvider getProvider();

    /**
     * Timestamp of the sample, resolution is seconds!
     */
    int getTimestamp();

    /**
     * Returns the raw activity kind value as recorded by the SampleProvider
     */
    int getRawKind();

    /**
     * Returns the activity kind value as recorded by the SampleProvider
     *
     * @see ActivityKind
     */
    int getKind();

    /**
     * Returns the raw intensity value as recorded by the SampleProvider
     */
    int getRawIntensity();

    /**
     * Returns the normalized intensity value between 0 and 1
     */
    float getIntensity();

    /**
     * Returns the number of steps performed during the period of this sample
     */
    int getSteps();

    int getCustomValue();
}
