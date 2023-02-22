package com.healthedge.payor.core.adaptor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.healthedge.payor.core.adaptor.DTO.response.IOMReference;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomParser {

    public Object parse(String datatype,Object value) throws JsonProcessingException {

        if(StringUtils.isBlank(datatype)){
            return null;
        }

        if(datatype.equalsIgnoreCase("long")){
            return  Long.valueOf((String) value);
        }else if(datatype.equalsIgnoreCase("Boolean")){
            return Boolean.valueOf((String) value);
        }else if(datatype.equalsIgnoreCase("IOMreference")){
            return parseIomReference((String)value);
        }else{
            return value;
        }
    }

    public IOMReference parseIomReference(String xml) throws JsonProcessingException {
        ObjectMapper objectMapper=new XmlMapper();
        JsonNode jsonNode=objectMapper.readTree(xml);
        return new IOMReference(jsonNode.get("ID").get("").asLong(),jsonNode.get("type").asText());
    }
}
