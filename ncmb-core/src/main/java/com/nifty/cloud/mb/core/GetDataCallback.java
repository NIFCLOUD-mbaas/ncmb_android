package com.nifty.cloud.mb.core;

/**
 * Interface for callback after file GET
 */
public interface GetDataCallback extends CallbackBase{
    /**
     * Override this method with the code you want to run after process completed
     * @param data get file data
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(byte[] data, NCMBException e);
}
