package com.healthedge.payor.core.adaptor.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthedge.payor.core.adaptor.context.Context;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class SendHardCodedJsonResponseProcessor implements Processor {
    @Override
    public void process(Context context) {
        log.info("Executing: {}", this.getClass().getSimpleName());
        try {
            RequestResponseBean requestResponseBean = (RequestResponseBean) context;
            JsonNode jsonResponse = getHardCodedJsonResponse(requestResponseBean.getSoapMessageResponse(), requestResponseBean.getJsonRequest(), requestResponseBean.getSoapWebServiceName(), requestResponseBean.getSoapAction());
            if(SoapMessageUtil.isResponseReturnWithFault(jsonResponse)) requestResponseBean.setIsSoapMessageResponseReturnWithFault(true);
            requestResponseBean.setJsonResponse(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private JsonNode getHardCodedJsonResponse(SOAPMessage soapMessageResponse, JsonNode jsonRequest, String soapWebServiceName, String soapAction) throws IOException, SOAPException {
        String soapXmlString;
        try (ByteArrayOutputStream br = new ByteArrayOutputStream()) {
            soapMessageResponse.writeTo(br);
            soapXmlString = br.toString();
        }
        if (soapXmlString.contains("<faultstring>")) {
            log.warn("Error return from HE backend");
            return SoapMessageUtil.convertSoapXmlToJsonNode(soapXmlString);
        }
        return helperGetJsonResponseFromFiles(jsonRequest, soapWebServiceName, soapAction);
    }

    private JsonNode helperGetJsonResponseFromFiles(JsonNode jsonRequestNode, String soapWebServiceName, String soapAction) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        String path;
        String basePath = "src/main/resources/json/";
        String fileName = soapWebServiceName + "_" + soapAction;
        if (("getInstanceFromId").equals(soapAction) && ("ConfigurationServiceWeaklyTyped").equals(soapWebServiceName)) {
            path = basePath + (fileName + "/" + jsonRequestNode.get("id").toString()) + ".json";
        } else {
            path = basePath + fileName + ".json";
        }
        try {
            node = mapper.readTree(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
}
