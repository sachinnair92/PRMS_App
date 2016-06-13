package com.voodoo.GadgetBridgeFiles.model;

public interface SummaryOfDay {
    byte getProvider();

    int getSteps();

    int getDayStartWakeupTime();

    int getDayEndFallAsleepTime();

}
