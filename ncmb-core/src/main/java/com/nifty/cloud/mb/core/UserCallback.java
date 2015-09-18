package com.nifty.cloud.mb.core;

/**
 * Interface for callback after push notification retrieve
 */
public interface UserCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after getting user
     * @param user result of get NCMBUser
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(NCMBUser user, NCMBException e);
}
