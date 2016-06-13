package com.voodoo.GadgetBridgeFiles.activities;

import com.voodoo.GadgetBridgeFiles.model.ItemWithDetails;

public interface InstallActivity {
    void setInfoText(String text);

    void setInstallEnabled(boolean enable);

    void clearInstallItems();

    void setInstallItem(ItemWithDetails item);
}
