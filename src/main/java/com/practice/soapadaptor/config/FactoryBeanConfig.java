package com.practice.soapadaptor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;


public class FactoryBeanConfig {

    public SOAPConnectionFactory soapConnectionFactory() throws SOAPException {
        return SOAPConnectionFactory.newInstance();
    }


    public MessageFactory messageFactory() throws SOAPException {
        return MessageFactory.newInstance();
    }
}
