package com.nifty.cloud.mb.core;

import java.util.List;

/**
 * Interface for callback after files search
 */
public interface SearchFileCallback extends CallbackBase {
        /**
         * Override this method with the code you want to run after searching files
         * @param files found files
         * @param e exception sdk internal or NIFTY Cloud mobile backend
         */
        void done(List<NCMBFile> files, NCMBException e);
}
