package com.practice.soapadaptor.test;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.*;

import com.practice.soapadaptor.generated.numberconversion.*;
import com.sun.xml.txw2.output.CharacterEscapeHandler;

public class JAXBExample {
    public static void main(String[] args) throws SOAPException, IOException {

        NumberToDollars request = new NumberToDollars();
        request.setDNum(BigDecimal.valueOf(50));

        try {

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
// Obtain SOAP Part

            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
// Obtain Envelope from SOAP Part

            SOAPHeader header = soapEnvelope.getHeader();
// Obtain Header from Envelope

            SOAPBody soapBody = soapEnvelope.getBody();
// Obtain Body from Envelope

            QName headerName = new QName("namespaceURI", "localPart");
// SOAPHeaderElement must have an associated QName object.

            SOAPHeaderElement headerElement = header.addHeaderElement(headerName);
// Create new SOAPHeaderElement object initialized with the specified Qname
// and add it to this SOAPHeader object.

            headerElement.addAttribute(new QName("localPart"), "valueToAdd");
// Add attribute to header

            QName bodyName = new QName("namespaceURI", "localPart");
// SOAPBodyElement must have an associated QName object.

            SOAPBodyElement bodyElement = soapBody.addBodyElement(bodyName);
// Add Body Element


            JAXBContext jaxbContext = JAXBContext.newInstance(NumberToDollars.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//             jaxbMarshaller.setProperty(CharacterEscapeHandler.class.getName(), new CustomCharacterEscapeHandler());
            jaxbMarshaller.setProperty("com.sun.xml.internal.bind.characterEscapeHandler",
                    new CharacterEscapeHandler() {
                        public void escape(char[] ch, int start, int length, boolean isAttVal, Writer writer)
                                throws IOException {
                            writer.write(ch, start, length);
                        }
                    });


            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            jaxbMarshaller.marshal(request, System.out);
            jaxbMarshaller.marshal(request, soapBody);
            soapMessage.saveChanges();
            soapMessage.writeTo(System.out);

        }
        catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
