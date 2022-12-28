package com.practice.soapadaptor.client;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/*
   -During compilation or building up the project, we must have WSDl file for all the soap web services that we need to create rest APIs for.
   -We are using java plugin/tool to generate POJOs or JAXB classes for the Rest request and response Objects that will further interact with our business logic.
 */
/* Process that take place in service layer(core logic):-
   (Everytime)
   - Steps to be executed for one soap web service request:-
        Layer 1st: Prepare data transfer object that is going to hold all the necessary resources needed to share between different layers.
        Layer 2nd: serialize/marshal jaxb-class request object to soap request message.
        Layer 3rd: A Client that consumes soap request message to fetch us the result from the soap web service end-point and cast response in soap response message.
        Layer 4th: deserialize/unmarshal soap response message to jaxb-class response object.
   - Exceptional Handling must be taken care off in each Layer.

   (Use case Dependent)
   - It might be possible that we need to hit multiple soap web service end-points from our service,
     therefore we follow the above process again, preparing new dto for each request.
   - Lastly Modify Rest Response Object if needed.
 */
@Builder
@Slf4j
public class SOAPClientSAAJ<T,X> {
    private final String soapUrl;
    private final String soapAction;
    private final T request;
    private final Class<X> responseType;
    private final Map<String,String> nameSpaceUriMap;
    private final Map<String,String> headersMap;

    public X callSoapWebService() {
        X responseObject = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), soapUrl);
            // Print the SOAP Response
            ByteArrayOutputStream br = new ByteArrayOutputStream();
            soapResponse.writeTo(br);
            log.info("Response SOAP Message:\n{}",br.toString());
            br.close();
            JAXBContext jaxbContext = JAXBContext.newInstance(responseType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Object response = jaxbUnmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
            responseObject = responseType.cast(response);
            soapConnection.close();
        } catch (Exception e) {
            log.error("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        return responseObject;
    }
    private SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        for (Map.Entry<String,String> entry : headersMap.entrySet()) {
            headers.addHeader(entry.getKey(), entry.getValue());
        }
        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        ByteArrayOutputStream br = new ByteArrayOutputStream();
        soapMessage.writeTo(br);
        log.info("Request SOAP Message:\n{}", br.toString());
        br.close();
        return soapMessage;
    }
    @SneakyThrows
    private void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "";
        String myNamespaceURI = "";
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        for (Map.Entry<String,String> entry : nameSpaceUriMap.entrySet()) {
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

}
