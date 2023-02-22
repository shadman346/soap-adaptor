package com.healthedge.payor.core.adaptor.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.context.Context;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class SoapResponseToJsonResponseProcessor implements Processor {

    @Override
    public void process(Context context) {
        log.info("Executing: {}", this.getClass().getSimpleName());
        try {
            RequestResponseBean requestResponseBean = (RequestResponseBean) context;
            JsonNode jsonResponse = convertSoapToJson(requestResponseBean.getSoapMessageResponse());
            if(SoapMessageUtil.isResponseReturnWithFault(jsonResponse)) requestResponseBean.setIsSoapMessageResponseReturnWithFault(true);
            requestResponseBean.setJsonResponse(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    private JsonNode convertSoapToJson(SOAPMessage soapMessageResponse) throws IOException, SOAPException {
        try (ByteArrayOutputStream br = new ByteArrayOutputStream()) {
            soapMessageResponse.writeTo(br);
            String soapXmlString = br.toString();
            if (soapXmlString.contains("<faultstring>")) {
                log.warn("Error return from HE backend");
            }
            return SoapMessageUtil.convertSoapXmlToJsonNode(soapXmlString);
        }
    }

}
