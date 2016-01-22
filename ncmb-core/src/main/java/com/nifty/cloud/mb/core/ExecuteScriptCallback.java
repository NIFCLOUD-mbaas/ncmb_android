package com.nifty.cloud.mb.core;

/**
 * Interface for callback after call script service class method
 */
public interface ExecuteScriptCallback extends CallbackBase {

    /**
     * Override this method with the code you want to run after executing script service
     *
     * @param data Result to script
     * @param e    NCMBException from NIFTY Cloud mobile backend
     */
    void done(byte[] data, NCMBException e);
}
