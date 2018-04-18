/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
package com.nifty.cloud.mb.core;

import org.json.JSONObject;

import java.util.Map;

/**
 * NCMBScript is used to script.
 */
public class NCMBScript {

    protected String mScriptName;
    protected MethodType mMethod;
    protected String mBaseUrl;

    /**
     * set the script name
     *
     * @param scriptName script name
     */
    public void setScriptName(String scriptName) {
        mScriptName = scriptName;
    }

    /**
     * get the script name
     *
     * @return script name
     */
    public String getScriptName() {
        return mScriptName;
    }

    /**
     * set the HTTP method
     *
     * @param method HTTP method type
     */
    public void setMethod(MethodType method) {
        mMethod = method;
    }

    /**
     * get the HTTP method
     *
     * @return HTTP method type
     */
    public MethodType getMethod() {
        return mMethod;
    }

    /**
     * set the script base url
     *
     * @param baseUrl script base url
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * get the script base url
     *
     * @return script base url
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * HTTP method types
     */
    public enum MethodType {
        POST,
        PUT,
        GET,
        DELETE
    }

    /**
     * Create NCMBScript instance with specified script name and request method
     *
     * @param scriptName script name
     * @param method     HTTP method
     */
    public NCMBScript(String scriptName, MethodType method) {
        this(scriptName, method, null);
    }

    /**
     * Create NCMBScript instance with specified script name and request method <br/>
     * This constructor can set the custom endpoint for debug
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param baseUrl    script base url
     */
    public NCMBScript(String scriptName, MethodType method, String baseUrl) {
        mScriptName = scriptName;
        mMethod = method;
        mBaseUrl = baseUrl;
    }

    /**
     * Execute the script with request parameters
     *
     * @param header header data
     * @param body   content data
     * @param query  query params
     * @return Result to script
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public byte[] execute(Map<String, String> header, JSONObject body, JSONObject query) throws NCMBException {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        return scriptService.executeScript(mScriptName, mMethod, header, body, query, mBaseUrl);
    }

    /**
     * Execute the script asynchronously with request parameters
     *
     * @param header   header data
     * @param body     content data
     * @param query    query params
     * @param callback callback after execute script
     */
    public void executeInBackground(Map<String, String> header, JSONObject body, JSONObject query, final ExecuteScriptCallback callback) {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        scriptService.executeScriptInBackground(mScriptName, mMethod, header, body, query, mBaseUrl, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] data, NCMBException e) {
                if (callback != null) {
                    callback.done(data, e);
                }
            }
        });
    }
}

