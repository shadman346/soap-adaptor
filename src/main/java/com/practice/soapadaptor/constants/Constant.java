package com.practice.soapadaptor.constants;

import java.util.Map;

public class Constant {
    public static final Map<String,String> nameSpaceEnvelopeUriMap0 = Map.of("xsi","http://www.w3.org/2001/XMLSchema-instance",
                                                        "xsd","http://www.w3.org/2001/XMLSchema");

    public static final String soapActionNameSpace = "SOAPActionNs";
    public static final Map<String,String > headersMap0 = Map.of("content-type","text/xml");
    public static final Map<String,String > headersMap1 = Map.of("content-type","application/soap+xml");


    public static final String SOAPACTION = "SOAPAction";
    public static String soapActionUriNameSpace = "\"http://healthedge.com\"";
}
