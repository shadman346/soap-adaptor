package com.healthedge.payor.core.adaptor.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessorManager {

    @Autowired
    private JsonRequestToSoapRequestProcessor jsonRequestToSoapRequestProcessor;

    @Autowired
    private SOAPMessageProcessor soapMessageProcessor;

    @Autowired
    private SoapResponseToJsonResponseProcessor soapResponseToJsonResponseProcessor;

    @Autowired
    private SendHardCodedJsonResponseProcessor sendHardCodedJsonResponseProcessor;
    private RequestResponseBean requestResponseBean;

    public ProcessorManager init(RequestResponseBean requestResponseBean) {
        this.requestResponseBean = requestResponseBean;
        return this;
    }

    public ProcessorManager processSoapMessage() {
        jsonRequestToSoapRequestProcessor.process(requestResponseBean);
        return this;
    }

    public ProcessorManager sendSoapMessage() {
        soapMessageProcessor.process(requestResponseBean);
        return this;
    }

    public ProcessorManager processJsonResponse() {
        soapResponseToJsonResponseProcessor.process(requestResponseBean);
        return this;
    }

    public ProcessorManager processHardCodedJsonResponse() {
        sendHardCodedJsonResponseProcessor.process(requestResponseBean);
        return this;
    }

    public JsonNode getJsonResponse() {
        return requestResponseBean.getJsonResponse();
    }
}
