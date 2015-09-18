package com.nifty.cloud.mb.core;

/**
 * Interface for callback after api request
 */
public interface RequestApiCallback extends CallbackBase {

    /**
     * execute after api request
     * @param res response data from api server
     * @param e error data from api server
     */
    public void done (NCMBResponse res, NCMBException e);
}
