package com.healthedge.payor.core.adaptor.constants;

import java.util.HashMap;
import java.util.Map;

public class SoapConstants {
    public static final Map<String,String> NAME_SPACE_ENVALOP_URI_MAP0 = getNameSpaceURIMap();

    private static Map<String, String> getNameSpaceURIMap() {
        Map<String, String> map = new HashMap<>();
        map.put("xsi","http://www.w3.org/2001/XMLSchema-instance");
        map.put("xsd","http://www.w3.org/2001/XMLSchema");

        return map;
    }

    public static final Map<String,String > HEADER_CONTENT_TYPE_TEXT_XML_MAP = getHeaderContentTypeTextXmlMap();

    private static Map<String, String> getHeaderContentTypeTextXmlMap() {
        Map<String, String> map = new HashMap<>();
        map.put("content-type","text/xml");

        return map;
    }

    public static final Map<String,String > HEADER_CONTENT_TYPE_APPLICATION_SOAP_XML_MAP = getHeaderContentTypeApplicationSoapXmlMap();
    private static Map<String, String> getHeaderContentTypeApplicationSoapXmlMap() {
        Map<String, String> map = new HashMap<>();
        map.put("content-type","application/soap+xml");

        return map;
    }

    public static final String SOAP_ACTION = "SOAPAction";

    public static String SOAP_ACTION_URI_NAMESPACE = "\"http://healthedge.com\"";

    public static final String SOAP_ACTION_NAME_SPACE = "SOAPActionNs";

}
