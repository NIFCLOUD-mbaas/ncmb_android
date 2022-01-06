package com.nifcloud.mbaas.core;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Temporary pool for service object
 */
public class NCMBServicePool {

    /** stored services */
    protected Map<NCMB.ServiceType, HashMap<NCMBContext, NCMBService>> pool;

    /**
     * Constructor
     */
    public NCMBServicePool() {
        pool = new EnumMap<NCMB.ServiceType, HashMap<NCMBContext, NCMBService>>
                (NCMB.ServiceType.class);
    }

    /**
     * Get service object
     * @param serviceType service type
     * @param context context
     * @return service object
     */
    public NCMBService get(NCMB.ServiceType serviceType, NCMBContext context) {
        NCMBService service;
        if (exists(serviceType, context)) {
            service = getServices(serviceType).get(context);
        } else {
            service = newService(serviceType, context);
            getServices(serviceType).put(context, service);
        }
        return service;
    }

    /**
     * Return service object is already cached
     * @param serviceType service type
     * @param context context
     * @return
     */
    public boolean exists(NCMB.ServiceType serviceType, NCMBContext context) {
        if (!pool.containsKey(serviceType)) {
            return false;
        }
        HashMap<NCMBContext, NCMBService> services = pool.get(serviceType);
        if (!services.containsKey(context)) {
            return false;
        }
        return true;
    }

    /**
     * Get context-keyed hash of given service type
     * @param serviceType service type
     * @return
     */
    protected HashMap<NCMBContext, NCMBService> getServices(NCMB.ServiceType serviceType) {
        if (pool.containsKey(serviceType)) {
            return pool.get(serviceType);
        }
        HashMap<NCMBContext, NCMBService> services = new HashMap<NCMBContext, NCMBService>();
        pool.put(serviceType, services);
        return services;
    }

    /**
     * Create service object and return it
     * @param serviceType service type
     * @param context context
     * @return service object
     */
    public NCMBService newService(NCMB.ServiceType serviceType, NCMBContext context) {
        NCMBService service;
        switch (serviceType) {
            case OBJECT:
                service = (NCMBService)new NCMBObjectService(context);
                break;
            case USER:
                service = (NCMBService)new NCMBUserService(context);
                break;
            case ROLE:
                service = (NCMBService)new NCMBRoleService(context);
                break;
            case INSTALLATION:
                service = (NCMBInstallationService)new NCMBInstallationService(context);
                break;
            case PUSH:
                service = (NCMBPushService)new NCMBPushService(context);
                break;
            case FILE:
                service = (NCMBFileService) new NCMBFileService(context);
                break;
            case SCRIPT:
                service = (NCMBScriptService) new NCMBScriptService(context);
                break;
            default:
                throw new IllegalArgumentException("Invalid serviceType");
        }
        return service;
    }
}
