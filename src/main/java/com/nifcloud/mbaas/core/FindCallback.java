/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifcloud.mbaas.core;

import java.util.List;

/**
 * Interface for callback after call search method in the entity class
 */
public interface FindCallback<T extends NCMBBase> {

    /**
     * Override this method with the code you want to run after find object
     * @param results list of search result
     * @param e NCMBException from NIFCLOUD mobile backend
     */
    void done(List<T> results, NCMBException e);
}
