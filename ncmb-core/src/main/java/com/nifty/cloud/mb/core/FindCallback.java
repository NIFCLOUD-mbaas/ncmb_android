package com.nifty.cloud.mb.core;

import java.util.List;

/**
 * Interface for callback after call search method in the entity class
 */
public interface FindCallback<T extends NCMBBase> {

    /**
     * Override this method with the code you want to run after find object
     * @param results list of search result
     * @param e NCMBException from NIFTY Cloud mobile backend
     */
    void done(List<T> results, NCMBException e);
}
