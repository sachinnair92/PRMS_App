package com.voodoo.GadgetBridgeFiles.database;

/**
 * Contains the configuration used for particular activity samples.
 */
public class UsedConfiguration {
    String fwVersion;
    String userName;
    short userWeight;
    short userSize;
    // ...
    int usedFrom; // timestamp
    int usedUntil; // timestamp
    short sleepGoal; // minutes
    short stepsGoal;
}
