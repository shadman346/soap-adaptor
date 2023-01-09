package com.practice.soapadaptor.context;

import java.util.Map;

public class SharedApplicationContext {
    private static final ThreadLocal<SharedData> sharedData = new InheritableThreadLocal<>();

    private SharedApplicationContext(){
        throw new IllegalCallerException("Utility class");
    }
    public static void loadData() {
        SharedData share = new SharedData();
        sharedData.set(share);
    }

    public static Map<String,String> getHeaders(){
        return sharedData.get().getHeaders();
    }

    public static void unload(){
        sharedData.remove();
    }

    public static void close(){
        sharedData.remove();
    }
}
