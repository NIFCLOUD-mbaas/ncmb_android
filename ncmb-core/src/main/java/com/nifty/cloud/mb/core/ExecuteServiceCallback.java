package com.nifty.cloud.mb.core;

import org.json.JSONObject;

/**
 * Interface for callback after call service class method
 */
public interface ExecuteServiceCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after executing service
     * @param json response from NIFTYCloud mobile backend
     * @param e exception from NFITYCloud mobile backend
     */
    void done(JSONObject json, NCMBException e);
}
