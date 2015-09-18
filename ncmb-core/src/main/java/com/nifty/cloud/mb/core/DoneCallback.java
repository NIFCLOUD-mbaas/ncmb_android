package com.nifty.cloud.mb.core;

/**
 * Interface for generic action callback
 */
public interface DoneCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after process completed
     * @param e exception from NIFTY Cloud mobile backend
     */
    void done(NCMBException e);
}
