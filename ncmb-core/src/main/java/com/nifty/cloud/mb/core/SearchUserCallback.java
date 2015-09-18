package com.nifty.cloud.mb.core;

import java.util.ArrayList;

/**
 * Interface for callback to search users
 */
public interface SearchUserCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after getting user
     * @param users found users
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(ArrayList<NCMBUser> users, NCMBException e);
}
