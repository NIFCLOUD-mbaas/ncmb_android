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

import java.util.ArrayList;

/**
 * Interface for callback after user search
 */
public interface SearchUserCallback extends CallbackBase {
    /**
     * Override this method with the code you want to run after getting user
     * @param users found users
     * @param e exception sdk internal or NIFCLOUD mobile backend
     */
    void done(ArrayList<NCMBUser> users, NCMBException e);
}
