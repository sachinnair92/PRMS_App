package com.voodoo.GadgetBridgeFiles.activities.charts;

import java.util.List;

import com.voodoo.GadgetBridgeFiles.model.ActivityAmount;
import com.voodoo.GadgetBridgeFiles.model.ActivityAmounts;
import com.voodoo.GadgetBridgeFiles.model.ActivityKind;
import com.voodoo.GadgetBridgeFiles.model.ActivitySample;

public class ActivityAnalysis {
    public ActivityAmounts calculateActivityAmounts(List<ActivitySample> samples) {
        ActivityAmount deepSleep = new ActivityAmount(ActivityKind.TYPE_DEEP_SLEEP);
        ActivityAmount lightSleep = new ActivityAmount(ActivityKind.TYPE_LIGHT_SLEEP);
        ActivityAmount notWorn = new ActivityAmount(ActivityKind.TYPE_NOT_WORN);
        ActivityAmount activity = new ActivityAmount(ActivityKind.TYPE_ACTIVITY);

        ActivityAmount previousAmount = null;
        ActivitySample previousSample = null;
        for (ActivitySample sample : samples) {
            ActivityAmount amount = null;
            switch (sample.getKind()) {
                case ActivityKind.TYPE_DEEP_SLEEP:
                    amount = deepSleep;
                    break;
                case ActivityKind.TYPE_LIGHT_SLEEP:
                    amount = lightSleep;
                    break;
                case ActivityKind.TYPE_NOT_WORN:
                    amount = notWorn;
                    break;
                case ActivityKind.TYPE_ACTIVITY:
                default:
                    amount = activity;
                    break;
            }

            if (previousSample != null) {
                long timeDifference = sample.getTimestamp() - previousSample.getTimestamp();
                if (previousSample.getRawKind() == sample.getRawKind()) {
                    amount.addSeconds(timeDifference);
                } else {
                    long sharedTimeDifference = (long) (timeDifference / 2.0f);
                    previousAmount.addSeconds(sharedTimeDifference);
                    amount.addSeconds(sharedTimeDifference);
                }
            } else {
                // nothing to do, we can only calculate when we have the next sample
            }

            previousAmount = amount;
            previousSample = sample;
        }

        ActivityAmounts result = new ActivityAmounts();
        if (deepSleep.getTotalSeconds() > 0) {
            result.addAmount(deepSleep);
        }
        if (lightSleep.getTotalSeconds() > 0) {
            result.addAmount(lightSleep);
        }
        if (activity.getTotalSeconds() > 0) {
            result.addAmount(activity);
        }
        result.calculatePercentages();

        return result;
    }

    public int calculateTotalSteps(List<ActivitySample> samples) {
        int totalSteps = 0;
        for (ActivitySample sample : samples) {
            totalSteps += sample.getSteps();
        }
        return totalSteps;
    }
}
