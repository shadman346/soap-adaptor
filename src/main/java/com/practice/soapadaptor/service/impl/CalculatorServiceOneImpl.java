package com.practice.soapadaptor.service.impl;

import com.practice.soapadaptor.context.SharedApplicationContext;
import com.practice.soapadaptor.generated.calculator.*;
import com.practice.soapadaptor.constants.Constant;
import com.practice.soapadaptor.generated.calculator.CalculatorSoap;
import com.practice.soapadaptor.client.SOAPClientSAAJ;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalculatorServiceOneImpl implements CalculatorSoap {
    protected static final Map<String,String> soapActionMap = new HashMap<>();
    private static final String SOAP_URL = "http://www.dneonline.com/calculator.asmx";
    @Override
    public AddResponse add(Add add) {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(Constant.SOAPACTION,"http://tempuri.org/Add");
        headersMap.putAll(Constant.headersMap0);
        headersMap.putAll(SharedApplicationContext.getHeaders());
        SOAPClientSAAJ<Add,AddResponse> soapClientSAAJ = SOAPClientSAAJ.<Add, AddResponse>builder()
                .soapUrl(SOAP_URL).headersMap(headersMap).nameSpaceUriMap(new HashMap<>())
                .request(add).responseType(AddResponse.class).build();
        return soapClientSAAJ.callSoapWebService();
    }

    @Override
    public MultiplyResponse multiply(Multiply multiply) {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(Constant.SOAPACTION,"http://tempuri.org/Multiply");
        headersMap.putAll(Constant.headersMap0);
        SOAPClientSAAJ<Multiply,MultiplyResponse> soapClientSAAJ = SOAPClientSAAJ.<Multiply, MultiplyResponse>builder()
                .soapUrl(SOAP_URL).headersMap(headersMap).nameSpaceUriMap(Constant.nameSpaceUriMap0)
                .request(multiply).responseType(MultiplyResponse.class).build();
        return soapClientSAAJ.callSoapWebService();
    }

    @Override
    public DivideResponse divide(Divide divide) {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(Constant.SOAPACTION,"http://tempuri.org/Divide");
        headersMap.putAll(Constant.headersMap0);
        SOAPClientSAAJ<Divide,DivideResponse> soapClientSAAJ = SOAPClientSAAJ.<Divide, DivideResponse>builder()
                .soapUrl(SOAP_URL).headersMap(headersMap).nameSpaceUriMap(Constant.nameSpaceUriMap0)
                .request(divide).responseType(DivideResponse.class).build();
        return soapClientSAAJ.callSoapWebService();
    }

    @Override
    public SubtractResponse subtract(Subtract subtract) {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(Constant.SOAPACTION,"http://tempuri.org/Subtract");
        headersMap.putAll(Constant.headersMap0);
        SOAPClientSAAJ<Subtract,SubtractResponse> soapClientSAAJ = SOAPClientSAAJ.<Subtract,SubtractResponse>builder()
                .soapUrl(SOAP_URL).headersMap(headersMap).nameSpaceUriMap(Constant.nameSpaceUriMap0)
                .request(subtract).responseType(SubtractResponse.class).build();
        return soapClientSAAJ.callSoapWebService();
    }
}


