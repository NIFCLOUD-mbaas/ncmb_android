package com.nifty.cloud.mb.core;

/**
 * Interface for getting role information
 */
public interface RoleCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after getting role
     * @param role result of get NCMBRole
     * @param e exception sdk internal or NIFTY Cloud mobile backend
     */
    void done(NCMBRole role, NCMBException e);
}
