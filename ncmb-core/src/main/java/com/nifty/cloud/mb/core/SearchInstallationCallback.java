package com.nifty.cloud.mb.core;

import java.util.ArrayList;

/**
 * SearchPushCallback interface
 */
public interface SearchInstallationCallback extends CallbackBase{
    /**
     * Override this method with the code you want to run after getting installations
     * @param installations found installations
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(ArrayList<NCMBInstallation> installations, NCMBException e);
}
