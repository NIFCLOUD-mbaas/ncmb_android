package com.nifty.cloud.mb.core;

/**
 * Interface for callback after user login
 */
public interface LoginCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after logged-in complete
     * @param user logined NCMBUser
     * @param e NCMBException from NIFTY Cloud mobile backend
     */
    void done(NCMBUser user, NCMBException e);
}
