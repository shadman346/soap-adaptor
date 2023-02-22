package com.healthedge.payor.core.adaptor.processors;


import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

@Slf4j
@Component
public class SOAPMessageProcessor implements Processor {

    @Value("${healthedge.host.url}")
    private String hostUrl;
    @Override
    public void process(Context context) {
        log.info("Executing: {}", this.getClass().getSimpleName());
        if (!(context instanceof RequestResponseBean)) {
            throw new RuntimeException("not suitable context");
        }
        try {
            RequestResponseBean requestResponseBean = (RequestResponseBean) context;
            SOAPConnection soapConnection = prepareSoapConnection();
            assert soapConnection!=null;
            SOAPMessage soapResponse = soapConnection.call(requestResponseBean.getSoapMessageRequest(), hostUrl + requestResponseBean.getSoapWebServiceName());
            requestResponseBean.setSoapMessageResponse(soapResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private SOAPConnection prepareSoapConnection() {
        SOAPConnectionFactory soapConnectionFactory;
        SOAPConnection soapConnection = null;
        try {
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
            return soapConnection;
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }
}
