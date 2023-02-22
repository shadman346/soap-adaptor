package com.healthedge.payor.core.adaptor.context;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import javax.xml.soap.SOAPMessage;
import java.util.Map;

@Configuration
@Getter
@Setter
@RequestScope
public class RequestResponseBean implements Context {

    private String soapWebServiceName;
    private JsonNode jsonRequest;
    private JsonNode jsonResponse;
    private Map<String, String> headersMap;
    private String soapAction;

    private SOAPMessage soapMessageRequest = null;
    private SOAPMessage soapMessageResponse = null;
    private Boolean isSoapMessageResponseReturnWithFault = null;

    public RequestResponseBean() {

    }

}
