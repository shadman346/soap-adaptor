package com.practice.soapadaptor.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.soapadaptor.util.XmlToJsonConverter;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

/*
   -During compilation or building up the project, we must have WSDl file for all the soap web services that we need to create rest APIs for.
   -We are using java plugin/tool to generate POJOs or JAXB classes for the Rest request and response Objects that will further interact with our business logic.
 */
/* Process that take place in service layer(core logic):-
   - Steps to be executed for one soap web service request:-
        Initialize: Prepare data transfer object that is going to hold all the necessary resources needed to share between different layers. (External+Internal Fields)
        Step 1: serialize/marshal jaxb-class request object to soap request message.
        Step 2: A Client that consumes soap request message to fetch us the result from the soap web service end-point and cast response in soap response message.
        Step 3: deserialize/unmarshal soap response message to jaxb-class response object.
   - Exceptional Handling must be taken care off in each Layer.

 */
@Slf4j
public class SOAPClientSAAJ<T,X> {
    //Required Fields
    private final String soapUrl;
    private final T request;
    private final Class<X> responseType;
    private final Map<String, String> nameSpaceUriMap;
    private final Map<String, String> headersMap;


    //Create Builder for Required/External fields only
    @Builder
    public SOAPClientSAAJ(String soapUrl, T request, Class<X> responseType, Map<String, String> nameSpaceUriMap, Map<String, String> headersMap) {
        this.soapUrl = soapUrl;
        this.request = request;
        this.responseType = responseType;
        this.nameSpaceUriMap = nameSpaceUriMap;
        this.headersMap = headersMap;
    }
    //Internal fields
    private SOAPMessage soapMessageRequest = null;
    private SOAPMessage soapMessageResponse = null;
    private X response = null;
    private boolean closed = false;

    public void close() {
        if (closed) {
            log.error("SOAPClientSAAJ Connection is already closed !!");
        }
        closed = true;
    }

    public X callSoapWebService() {
        if (closed) {
            log.error("SOAPClientSAAJ Connection is closed !!!");
            return null;
        }
        try {
            //Marshal process (Step 1)
            serializeRequestToSOAPMessageRequest();
            //Making Request to SOAP Server process (Step 2)
            publishSOAPRequestToSoapServer();
            //Unmarshall process (Step 3)
            deSerializeResponseFromSOAPMessageResponse();
        } catch (Exception e) {
            log.error("\nError occurred while creating and sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and all Required fields are present!\n");
            throw new RuntimeException(e);
        } finally {
            close();
        }
        if (Objects.isNull(response)) {
            log.error("response object is NULL, UnIdentified Error.");
        }
        return response;
    }

    private void deSerializeResponseFromSOAPMessageResponse() throws JAXBException, SOAPException {
        JAXBContext jaxbContext = JAXBContext.newInstance(responseType);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object responseObject = jaxbUnmarshaller.unmarshal(soapMessageResponse.getSOAPBody().extractContentAsDocument());
        response = objectMapper(responseObject, responseType);
    }

    private void publishSOAPRequestToSoapServer() throws Exception {
        // Create SOAP Connection
        CustomSoapConnectionClient customSoapConnectionClient = new CustomSoapConnectionClient();
        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = customSoapConnectionClient.call(soapMessageRequest, soapUrl);
        // Print the SOAP Response
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapResponse.writeTo(br);

        String JsonObject = convert(br.toString());
        log.info("parsed Json Object : \n{}",JsonObject);
//        log.info("Response SOAP Message:\n{}", br.toString());
        br.close();
        customSoapConnectionClient.close();
        soapMessageResponse = soapResponse;
    }

    public void serializeRequestToSOAPMessageRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSOAPRequestEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            headers.addHeader(entry.getKey(), entry.getValue());
        }
        soapMessage.saveChanges();
        /* Print the request message, just for debugging purposes */
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapMessage.writeTo(br);
        log.info("Request SOAP Message:\n{}", br.toString());
        br.close();

        soapMessageRequest = soapMessage;
    }

    public String convert(String xml) throws JsonProcessingException {

        MappingJackson2XmlHttpMessageConverter xmlConverter=new MappingJackson2XmlHttpMessageConverter();
        ObjectMapper xmlMapper = xmlConverter.getObjectMapper();
        JsonNode jsonNode = xmlMapper.readTree(xml);

        ObjectMapper jsonMapper = new ObjectMapper();

        return jsonMapper.writeValueAsString(jsonNode);
    }

    @SneakyThrows
    private void createSOAPRequestEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "";
        String myNamespaceURI = "";
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        for (Map.Entry<String, String> entry : nameSpaceUriMap.entrySet()) {
            myNamespace = entry.getKey();
            myNamespaceURI = entry.getValue();
            envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        }
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        //JAXB object to feed soapBody
        JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        jaxbMarshaller.marshal(request, soapBody);

    }

    public X objectMapper(Object from, Class<X> to) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.convertValue(from, to);
    }

}
