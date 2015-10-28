package com.nifty.cloud.mb.core;

/**
 * Interface for callback after call fetch method in entity class
 */
public interface FetchCallback<T extends NCMBBase> extends CallbackBase {
    /**
     * Override this method with the code you want to run after process completed
     * @param object object of fetch result
     * @param e exception from NIFTY Cloud mobile backend
     */
    void done(T object, NCMBException e);
}
