package com.nifty.cloud.mb.core;

/**
 * NCMBScript is used to script.
 */
public class NCMBScript {

    protected String mScriptName;
    protected MethodType mMethod;

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
    NCMBScript(String scriptName, MethodType method) {
        mScriptName = scriptName;
        mMethod = method;
    }

    /**
     * Run the specified script in the constructor
     *
     * @param params ContentData or queryString
     * @return Result to script
     * @throws NCMBException
     */
    public byte[] execute(byte[] params) throws NCMBException {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        return scriptService.execute(mScriptName, mMethod, params);
    }

    /**
     * Run the specified script in the constructor is asynchronously
     *
     * @param params   ContentData or queryString
     * @param callback callback after execute script
     */
    public void executeInBackground(byte[] params, final ExecuteScriptCallback callback) {

    }
}

