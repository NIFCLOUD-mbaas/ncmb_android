package com.nifty.cloud.mb.core;

/**
 * Interface for callback after call entity class method in background thread
 */
public interface DoneCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after process completed
     * @param e exception from NIFTY Cloud mobile backend
     */
    void done(NCMBException e);
}
