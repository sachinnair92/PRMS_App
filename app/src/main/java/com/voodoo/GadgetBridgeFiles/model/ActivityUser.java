package com.voodoo.GadgetBridgeFiles.model;

import java.util.Calendar;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.util.Prefs;

/**
 * Class holding the common user information needed by most activity trackers
 */
public class ActivityUser {

    private Integer activityUserGender;
    private Integer activityUserYearOfBirth;
    private Integer activityUserHeightCm;
    private Integer activityUserWeightKg;
    private Integer activityUserSleepDuration;

    public static final int defaultUserGender = 0;
    public static final int defaultUserYearOfBirth = 0;
    public static final int defaultUserAge = 0;
    public static final int defaultUserHeightCm = 175;
    public static final int defaultUserWeightKg = 70;
    public static final int defaultUserSleepDuration = 7;

    public static final String PREF_USER_YEAR_OF_BIRTH = "activity_user_year_of_birth";
    public static final String PREF_USER_GENDER = "activity_user_gender";
    public static final String PREF_USER_HEIGHT_CM = "activity_user_height_cm";
    public static final String PREF_USER_WEIGHT_KG = "activity_user_weight_kg";
    public static final String PREF_USER_SLEEP_DURATION = "activity_user_sleep_duration";

    public int getActivityUserWeightKg() {
        if (activityUserWeightKg == null) {
            fetchPreferences();
        }
        return activityUserWeightKg;
    }

    public int getActivityUserGender() {
        if (activityUserGender == null) {
            fetchPreferences();
        }
        return activityUserGender;
    }

    public int getActivityUserYearOfBirth() {
        if (activityUserYearOfBirth == null) {
            fetchPreferences();
        }
        return activityUserYearOfBirth;
    }

    public int getActivityUserHeightCm() {
        if (activityUserHeightCm == null) {
            fetchPreferences();
        }
        return activityUserHeightCm;
    }

    /**
     * @return the user defined sleep duration or the default value when none is set or the stored
     * value is out of any logical bounds.
     */
    public int getActivityUserSleepDuration() {
        if (activityUserSleepDuration == null) {
            fetchPreferences();
        }
        if (activityUserSleepDuration < 1 || activityUserSleepDuration > 24) {
            activityUserSleepDuration = defaultUserSleepDuration;
        }
        return activityUserSleepDuration;
    }

    public int getActivityUserAge() {
        int userYear = getActivityUserYearOfBirth();
        int age = 25;
        if (userYear > 1900) {
            age = Calendar.getInstance().get(Calendar.YEAR) - userYear;
            if (age <= 0) {
                age = 25;
            }
        }
        return age;
    }

    private void fetchPreferences() {
        Prefs prefs = GBApplication.getPrefs();
        activityUserGender = prefs.getInt(PREF_USER_GENDER, defaultUserGender);
        activityUserHeightCm = prefs.getInt(PREF_USER_HEIGHT_CM, defaultUserHeightCm);
        activityUserWeightKg = prefs.getInt(PREF_USER_WEIGHT_KG, defaultUserWeightKg);
        activityUserYearOfBirth = prefs.getInt(PREF_USER_YEAR_OF_BIRTH, defaultUserYearOfBirth);
        activityUserSleepDuration = prefs.getInt(PREF_USER_SLEEP_DURATION, defaultUserSleepDuration);
    }
}
