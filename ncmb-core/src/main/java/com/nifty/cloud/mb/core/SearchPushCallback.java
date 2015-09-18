package com.nifty.cloud.mb.core;

import java.util.ArrayList;

/**
 * Interface for callback after push notification search
 */
public interface SearchPushCallback extends CallbackBase{
    /**
     * Override this method with the code you want to run after getting push
     * @param push found push
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(ArrayList<NCMBPush> push, NCMBException e);
}
