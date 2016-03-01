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
     * Constructor with script name
     *
     * @param scriptName script name
     * @param method     HTTP method
     */
    public NCMBScript(String scriptName, MethodType method) {
        this(scriptName, method, null);
    }

    /**
     * Constructor with script name and base url
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
     * Run the script with request parameters
     *
     * @param header header data
     * @param body   content data
     * @param query  query params
     * @return Result to script
     * @throws NCMBException
     */
    public byte[] execute(Map<String, String> header, JSONObject body, JSONObject query) throws NCMBException {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        return scriptService.executeScript(mScriptName, mMethod, header, body, query, mBaseUrl);
    }

    /**
     * Run the script asynchronously with request parameters
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

