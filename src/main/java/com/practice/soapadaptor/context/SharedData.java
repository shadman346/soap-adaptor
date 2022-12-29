package com.practice.soapadaptor.context;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SharedData {
    private Map<String,String> headers = new HashMap<>();

}
