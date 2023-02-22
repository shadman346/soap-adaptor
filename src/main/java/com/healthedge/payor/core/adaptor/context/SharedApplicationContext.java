package com.healthedge.payor.core.adaptor.context;


import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

public class SharedApplicationContext {
    private static final ThreadLocal<SharedData> SHARED_DATA_THREAD_LOCAL = new InheritableThreadLocal<>();

    private SharedApplicationContext() throws IllegalClassFormatException {
        throw new IllegalClassFormatException("Utility class");
    }
    public static void init() {
        SharedData share = new SharedData();
        SHARED_DATA_THREAD_LOCAL.set(share);
    }

    public static Map<String,String> getHeaders(){
        return SHARED_DATA_THREAD_LOCAL.get().getHeaders();
    }

    public static void unload(){
        SHARED_DATA_THREAD_LOCAL.remove();
    }

    public static void close(){
        SHARED_DATA_THREAD_LOCAL.remove();
    }
}