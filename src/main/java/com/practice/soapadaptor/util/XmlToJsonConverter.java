package com.practice.soapadaptor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;


public class XmlToJsonConverter {



    @Autowired
    MappingJackson2XmlHttpMessageConverter xmlConverter;


    public String convert(String xml) throws JsonProcessingException {

        ObjectMapper xmlMapper = xmlConverter.getObjectMapper();
        JsonNode jsonNode = xmlMapper.readTree(xml);

        ObjectMapper jsonMapper = new ObjectMapper();

        return jsonMapper.writeValueAsString(jsonNode);
    }
}
