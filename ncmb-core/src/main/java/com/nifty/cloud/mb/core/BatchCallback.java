package com.nifty.cloud.mb.core;

import org.json.JSONArray;

/**
 * Interface for  callback after batch API
 */
public interface BatchCallback extends CallbackBase {
        /**
         * Override this method with the code you want to run after batch API
         * @param responseArray objects data
         * @param e exception sdk internal or NIFTY Cloud mobile backend
         */
        void done(JSONArray responseArray, NCMBException e);
}
