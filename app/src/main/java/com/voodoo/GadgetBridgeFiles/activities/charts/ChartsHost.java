package com.voodoo.GadgetBridgeFiles.activities.charts;

import android.view.ViewGroup;

import java.util.Date;

import com.voodoo.GadgetBridgeFiles.impl.GBDevice;

public interface ChartsHost {
    String DATE_PREV = ChartsActivity.class.getName().concat(".date_prev");
    String DATE_NEXT = ChartsActivity.class.getName().concat(".date_next");
    String REFRESH = ChartsActivity.class.getName().concat(".refresh");

    GBDevice getDevice();

    void setStartDate(Date startDate);

    void setEndDate(Date endDate);

    Date getStartDate();

    Date getEndDate();

    void setDateInfo(String dateInfo);

    ViewGroup getDateBar();
}
