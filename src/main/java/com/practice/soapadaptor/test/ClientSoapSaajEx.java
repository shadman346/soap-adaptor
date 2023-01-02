package com.practice.soapadaptor.test;

import com.practice.soapadaptor.generatedold.numberconversion.*;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import java.math.BigDecimal;


public class ClientSoapSaajEx {
    // SAAJ - SOAP Client Testing
    public static void main(String args[]) {
        /*
            The example below requests from the Web Service at:
             http://www.webservicex.net/uszip.asmx?op=GetInfoByCity


            To call other WS, change the parameters below, which are:
             - the SOAP Endpoint URL (that is, where the service is responding from)
             - the SOAP Action

            Also change the contents of the method createSoapEnvelope() in this class. It constructs
             the inner part of the SOAP envelope that is actually sent.
         */

        String soapEndpointUrl = "https://www.dataaccess.com/webservicesserver/numberconversion.wso";
        String soapAction = "http://www.webserviceX.NET/NumberToDollars";
        NumberToDollars request = new NumberToDollars();
        request.setDNum(BigDecimal.valueOf(60));
        NumberToDollarsResponse response = callSoapWebService1(soapEndpointUrl, soapAction,request,NumberToDollarsResponse.class);
        System.out.println(response.getNumberToDollarsResult());
    }

    @SneakyThrows
    private static void createSoapEnvelope(SOAPMessage soapMessage, Object request) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "xsi";
        String myNamespaceURI = "http://www.w3.org/2001/XMLSchema-instance";
        String myNamespace1 = "xsd";
        String myNamespaceURI1 = "http://www.w3.org/2001/XMLSchema";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        envelope.addNamespaceDeclaration(myNamespace1,myNamespaceURI1);

            /*
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:GetInfoByCity>
                        <myNamespace:USCity>New York</myNamespace:USCity>
                    </myNamespace:GetInfoByCity>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            */

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        //JAXB object to feed soapBody
        JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        jaxbMarshaller.marshal(request, soapBody);

//        SOAPElement soapBodyElem = soapBody.addChildElement("NumberToDollars", myNamespace);
//        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("dNum", myNamespace);
//        soapBodyElem1.addTextNode("40");
    }

    public static <T extends Object> T callSoapWebService1(String soapEndpointUrl, String soapAction, Object request, Class<T> responseType) {
        Object responseObject = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction,request), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();
            JAXBContext jaxbContext = JAXBContext.newInstance(responseType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Object response = jaxbUnmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
            responseObject = response;
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        return responseType.cast(responseObject);
    }

    private static SOAPMessage createSOAPRequest(String soapAction, Object request) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage,request);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

}

