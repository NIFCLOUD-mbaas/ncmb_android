package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

/**
 * Parameters Object for login to NIFTY Cloud mobile backend with Anonymous Account
 */
class NCMBAnonymousParameters {

    /**
     * User id of Anonymous account
     */
    protected String userId;

    /**
     * Constructor
     *
     * @param aUserId User id of Facebook account
     */
    NCMBAnonymousParameters(@NonNull String aUserId) {
        if (aUserId == null) {
            throw new IllegalArgumentException("aUserId must not be null.");
        }
        userId = aUserId;
    }
}
