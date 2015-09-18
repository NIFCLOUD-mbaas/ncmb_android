package com.nifty.cloud.mb.core;

import java.util.ArrayList;

/**
 * Interface for callback after role search
 */
public interface SearchRoleCallback extends CallbackBase{
    /**
     * Override this method with the code you want to run after getting role
     * @param roles found roles
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(ArrayList<NCMBRole> roles, NCMBException e);
}
