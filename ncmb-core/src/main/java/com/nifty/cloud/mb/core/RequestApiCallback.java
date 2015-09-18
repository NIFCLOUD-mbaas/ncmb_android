package com.nifty.cloud.mb.core;

/**
 * Callback class for after request api
 */
public interface RequestApiCallback extends CallbackBase {

    /**
     * execute after api request
     * @param res response data from api server
     * @param e error data from api server
     */
    public void done (NCMBResponse res, NCMBException e);
}
