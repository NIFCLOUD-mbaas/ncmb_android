package com.nifty.cloud.mb.core;

import org.json.JSONObject;

/**
 * Interface for generic service callback
 */
public interface ExecuteServiceCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after executing service
     * @param json response from NIFTYCloud mobile backend
     * @param e exception from NFITYCloud mobile backend
     */
    void done(JSONObject json, NCMBException e);
}
