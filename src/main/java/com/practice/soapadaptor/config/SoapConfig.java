package com.practice.soapadaptor.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
//
//
//public class SoapConfig {
//
//    public Jaxb2Marshaller marshaller() {
//        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        // this is the package name specified in the <generatePackage> specified in
//        // pom.xml
//        marshaller.setContextPath("com.practice.soapadaptor.dto");
//        return marshaller;
//    }
//
//
//    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
//        SOAPConnector client = new SOAPConnector();
//        client.setMarshaller(marshaller);
//        client.setUnmarshaller(marshaller);
//        return client;
//    }
//}
