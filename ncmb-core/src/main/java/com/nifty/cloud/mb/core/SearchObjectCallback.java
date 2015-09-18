package com.nifty.cloud.mb.core;

import java.util.List;

/**
 * SearchObjectCallback interface
 */
public interface SearchObjectCallback extends CallbackBase {
        /**
         * Override this method with the code you want to run after searching objects
         * @param objects found objects
         * @param e exception sdk internal or NIFTY Cloud mobile backend
         */
        void done(List<NCMBObject> objects, NCMBException e);
}
