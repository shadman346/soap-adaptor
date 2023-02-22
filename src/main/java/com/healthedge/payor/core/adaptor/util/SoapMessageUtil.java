package com.healthedge.payor.core.adaptor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.healthedge.payor.core.adaptor.constants.SoapConstants;
import com.healthedge.payor.core.adaptor.context.SharedApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SoapMessageUtil {
    private SoapMessageUtil(){
        /*Sonar*/
    }
    public static Map<String, String> getHeadersMap() {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.putAll(SharedApplicationContext.getHeaders());
        headersMap.putAll(SoapConstants.HEADER_CONTENT_TYPE_TEXT_XML_MAP);
        return headersMap;
    }

    public static String decodeString(String strData) {
        if (strData == null) {
            return "";
        }
        String str=strData.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'")
                .replace("&quot;", "\"").replace("&amp;", "&")
                .replace("<init>","&lt;init&gt;").replace("<TextNode>","").replace("</TextNode>","")
                .replace("<IntNode>","").replace("</IntNode>","").replace("</BooleanNode>","")
                .replace("<BooleanNode>","");
        return str.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>","");
    }

    public static String prepareCData(String filedName, JsonNode requestNode) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        String searchInputXml=xmlMapper.writeValueAsString(requestNode.get(filedName))
                .replace("</ObjectNode>","")
                .replace("<ObjectNode>", "");
        String sb = "<![CDATA[" + searchInputXml + "]]>";
        ((ObjectNode) requestNode).remove(filedName);
        return sb;
    }

    public static String getJsonToXml(String fieldName,JsonNode requestNode) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        return decodeString(xmlMapper.writeValueAsString(requestNode.get(fieldName)));
    }

    public static JsonNode convertSoapXmlToJsonNode(String xml) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode;
        try {
            jsonNode = xmlMapper.readTree(SoapMessageUtil.decodeString(xml));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        return jsonNode;
    }

    public static boolean isResponseReturnWithFault(JsonNode jsonResponse) {
        return Objects.nonNull(jsonResponse.findValue("Fault"));
    }
}
