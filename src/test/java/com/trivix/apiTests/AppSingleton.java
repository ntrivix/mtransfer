package com.trivix.apiTests;

import com.trivix.App;

public class AppSingleton {
    private static App appSingleton;
    
    public static App getApp() {
        if (appSingleton != null)
            return appSingleton;

        synchronized (App.class)
        {
            if(appSingleton==null) {
                appSingleton = new App();
                appSingleton.run();
            }
        }
        
        return appSingleton;
    }
}
