package com.healthedge.payor.core.adaptor.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.context.Context;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static com.healthedge.payor.core.adaptor.constants.SoapConstants.NAME_SPACE_ENVALOP_URI_MAP0;
import static com.healthedge.payor.core.adaptor.constants.SoapConstants.SOAP_ACTION_NAME_SPACE;

@Slf4j
@Component
public class JsonRequestToSoapRequestProcessor implements Processor {

    @Override
    public void process(Context context) {
        log.info("Executing: {}", this.getClass().getSimpleName());
        if(!(context instanceof RequestResponseBean)){
            throw new RuntimeException("not suitable context");
        }
        try {
            RequestResponseBean requestResponseBean = (RequestResponseBean) context;
            SOAPMessage soapMessage=prepareSoapMessage(requestResponseBean.getJsonRequest(), requestResponseBean.getSoapAction(), requestResponseBean.getHeadersMap());
            ByteArrayOutputStream br= new ByteArrayOutputStream();
            soapMessage.writeTo(br);
            log.info(String.valueOf(br));
            br.close();
            requestResponseBean.setSoapMessageRequest(soapMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public SOAPMessage prepareSoapMessage(JsonNode requestNode, String soapAction, Map<String, String> headersMap) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        for (Map.Entry<String, String> entry : NAME_SPACE_ENVALOP_URI_MAP0.entrySet()) {
            envelope.addNamespaceDeclaration(entry.getKey(), entry.getValue());
        }
        SOAPBody soapBody = envelope.getBody();
        SOAPBodyElement actionNameElement = soapBody.addBodyElement(envelope.createName(soapAction.trim(), SOAP_ACTION_NAME_SPACE, "http://healthedge.com"));
        requestNode.fieldNames().forEachRemaining(fieldName -> {
            if (fieldName.equalsIgnoreCase("searchInputXml")) {
                try {
                    String cdata = SoapMessageUtil.prepareCData(fieldName, requestNode);
                    SOAPElement searchInputXmlElement = actionNameElement.addChildElement("searchInputXml");
                    searchInputXmlElement.addTextNode(cdata);
                } catch (JsonProcessingException | SOAPException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    SOAPElement element = actionNameElement.addChildElement(fieldName);
                    element.addTextNode(SoapMessageUtil.getJsonToXml(fieldName, requestNode));
                } catch (JsonProcessingException | SOAPException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        MimeHeaders headers = soapMessage.getMimeHeaders();
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            headers.addHeader(entry.getKey(), entry.getValue());
        }

        soapMessage.saveChanges();
        return soapMessage;
    }

}
