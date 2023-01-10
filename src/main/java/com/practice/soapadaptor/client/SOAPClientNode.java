package com.practice.soapadaptor.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.practice.soapadaptor.constants.Constant;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* Process that take place in service layer(core logic):-
   - Steps to be executed for one soap web service requestNode:-
        Initialize: Prepare data transfer object that is going to hold all the necessary resources needed to share between different layers. (External+Internal Fields)
        Step 1: serialize/marshal jaxb-class requestNode object to soap requestNode message.
        Step 2: A Client that consumes soap requestNode message to fetch us the result from the soap web service end-point and cast response in soap response message.
        Step 3: deserialize/unmarshal soap response message to jaxb-class response object.
   - Exceptional Handling must be taken care off in each Layer.

 */
@Slf4j
public class SOAPClientNode {

    //Required Fields
    @NonNull
    private final String soapUrl;
    @NonNull
    private final JsonNode requestNode;
    @NonNull
    private final Map<String, String> headersMap;
    @NonNull
    private final String soapAction;
    private final Map<String, String> nameSpaceEnvelopUriMap;
    private final String soapActionNameSpace;
    private final String soapActionUriNameSpace;


    //Create Builder for Required/External fields only
    @Builder
    public SOAPClientNode(String soapUrl, JsonNode requestNode, Map<String, String> headersMap, String soapAction, Map<String, String> nameSpaceEnvelopUriMap, String soapActionNameSpace, String soapActionUriNameSpace) {
        this.soapUrl = soapUrl;
        this.requestNode = requestNode;
        this.headersMap = headersMap;
        this.soapAction = soapAction;
        this.nameSpaceEnvelopUriMap = Objects.isNull(nameSpaceEnvelopUriMap) ? Constant.nameSpaceEnvelopeUriMap0 : nameSpaceEnvelopUriMap;
        this.soapActionNameSpace = Objects.isNull(soapActionNameSpace) ? Constant.soapActionNameSpace : soapActionNameSpace;
        this.soapActionUriNameSpace = Objects.isNull(soapActionUriNameSpace) ? Constant.soapActionUriNameSpace : soapActionUriNameSpace;
    }

    //Internal fields
    private SOAPMessage soapMessageRequest = null;
    private SOAPMessage soapMessageResponse = null;
    private JsonNode response = null;
    private boolean closed = false;

    public void close() {
        if (closed) {
            log.error("SOAPClientSAAJ Connection is already closed !!");
            return;
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
        try (ByteArrayOutputStream br = new ByteArrayOutputStream()) {
            soapMessageResponse.writeTo(br);
            response = convert(br.toString());
        }
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

        /* Print the requestNode message, just for debugging purposes */
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
        final String queryResultSet = "QueryResultSet";
        final String value = "value";
        final String type = "type";
        final String name = "name";
        Set<String> heObjects = new HashSet<>();
        if (!jsonNode.findValues(queryResultSet).isEmpty()) {
            jsonNode.findValues(queryResultSet).get(0).get("Metadata").get("Column").forEach(mCol -> {
                if(mCol.get(type).toString().contains("com.healthedge"))
                    heObjects.add(mCol.get(name).asText());
            });
            jsonNode.findValues(queryResultSet).get(0).get("Row").forEach(i -> {
                for (JsonNode j : i.get("Column")) {
                    if (heObjects.contains(j.get(name).asText()) && isXML(j.get(value).asText()) ) {
                        try {
                            JsonNode colNode = convert(j.get(value).asText());
                            ((ObjectNode) j).replace(value, colNode);

                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            });
        }
        return jsonNode;
    }

    public boolean isXML(String str) {
        //TODO: replace validation logic with something more efficient.
        if('<' != str.charAt(0)) return false;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
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
        return strData.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'")
                .replace("&quot;", "\"").replace("&amp;", "&");
    }

    private void createSOAPRequestEnvelope(SOAPMessage soapMessage) throws SOAPException, JsonProcessingException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "";
        String myNamespaceURI = "";
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        for (Map.Entry<String, String> entry : nameSpaceEnvelopUriMap.entrySet()) {
            myNamespace = entry.getKey();
            myNamespaceURI = entry.getValue();
            envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        }
        SOAPBody soapBody = envelope.getBody();
        ObjectMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(requestNode);
        soapBody.setTextContent(wrapInSOAPAction(xml));

    }

    private String wrapInSOAPAction(String xmlStr) {
        if (xmlStr == null) {
            return "";
        }
        xmlStr = xmlStr.replace("/ObjectNode", "/" + soapActionNameSpace + ":" + soapAction)
                .replace("ObjectNode", soapActionNameSpace + ":" + soapAction + " xmlns:" + soapActionNameSpace + "=" + soapActionUriNameSpace);
        return xmlStr;
    }

}