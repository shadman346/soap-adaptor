package com.practice.soapadaptor.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
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
public class SOAPClientSAAJ<T, X> {

    //Required Fields
    private final String soapUrl;
    private final T request;
    private final Class<X> responseType;
    private final Map<String, String> nameSpaceUriMap;
    private final Map<String, String> headersMap;
    private final String SOAPAction;


    //Create Builder for Required/External fields only
    @Builder
    public SOAPClientSAAJ(String soapUrl, T request, Class<X> responseType, Map<String, String> nameSpaceUriMap, Map<String, String> headersMap, String soapAction) {
        this.soapUrl = soapUrl;
        this.request = request;
        this.responseType = responseType;
        this.nameSpaceUriMap = nameSpaceUriMap;
        this.headersMap = headersMap;
        SOAPAction = soapAction;
    }

    //Internal fields
    private SOAPMessage soapMessageRequest = null;
    private SOAPMessage soapMessageResponse = null;
    private JsonNode response = null;
    private boolean closed = false;

    public void close() {
        if (closed) {
            log.error("SOAPClientSAAJ Connection is already closed !!");
        }
        closed = true;
    }

    public JsonNode callSoapWebService() throws JsonProcessingException {

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

    private void deSerializeResponseFromSOAPMessageResponse() throws IOException, SOAPException {
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapMessageResponse.writeTo(br);
        response = convert(br.toString());
        br.close();
    }

    private void publishSOAPRequestToSoapServer() throws Exception {

        // Create SOAP Connection
        CustomSoapConnectionClient customSoapConnectionClient = new CustomSoapConnectionClient();
        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = customSoapConnectionClient.call(soapMessageRequest, soapUrl);
        // Print the SOAP Response
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapResponse.writeTo(br);
        log.info("Response SOAP Message:\n{}", br);
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
        // Get the SOAP body and add the JSON string as a SOAP body element

        /* Print the request message, just for debugging purposes */
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapMessage.writeTo(br);
        log.info("Request SOAP Message:\n{}", br);
        br.close();

        soapMessageRequest = soapMessage;
    }

    public JsonNode convert(String xml) throws JsonProcessingException {
        String xml1 = decodeString(xml);
        ObjectMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.readTree(xml1);

        if (!jsonNode.findValues("QueryResultSet").isEmpty())
            jsonNode.findValues("QueryResultSet").get(0).get("Row").forEach(i -> {
                for (JsonNode j : i.get("Column")) {
                    if (isXML(j.get("value").asText())) {
                        try {
                            JsonNode colNode = convert(j.get("value").asText());
                            ((ObjectNode) j).replace("value", colNode);

                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            });
        return jsonNode;
    }

    public boolean isXML(String str) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(str));
            builder.parse(is);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String decodeString(String strData) {
        if (strData == null) {
            return "";
        }
        return strData.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&apos;", "'").replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&");
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
//        SOAP Body
        SOAPBody soapBody = envelope.getBody();
        //JAXB object to feed soapBody
//        JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
//        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//        // output pretty printed
//        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//        jaxbMarshaller.marshal(request, soapBody);
        ObjectMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(request);
        soapBody.setTextContent(wrapInSOAPAction(xml));

    }

    private String wrapInSOAPAction(String xmlStr) {
        if (xmlStr == null) {
            return "";
        }
        return xmlStr.replace("ObjectNode",SOAPAction);
    }

    public X objectMapper(Object from, Class<X> to) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.convertValue(from, to);
    }

}
