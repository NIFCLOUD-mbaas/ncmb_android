package com.nifty.cloud.mb.core;

/**
 * Interface for callback after call count search results method in the entity class
 */
public interface CountCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after count object
     * @param result number of search result
     * @param e NCMBException from NIFTY Cloud mobile backend
     */
    void done(int result, NCMBException e);
}
