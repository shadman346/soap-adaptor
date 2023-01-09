package com.practice.soapadaptor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.practice.soapadaptor.client.SOAPClientNode;
import com.practice.soapadaptor.constants.Constant;
import com.practice.soapadaptor.context.SharedApplicationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AllSOAPWebserviceController {
    @Value("${soap.host.url}")
    private static String baseUrl;

    @PostMapping("/{webServiceName}/{soapActionName}")
    public ResponseEntity<JsonNode> findClaims(
            @PathVariable String webServiceName,
            @PathVariable String soapActionName,
            @RequestBody JsonNode request) throws Exception {
        String soapUrl = baseUrl+webServiceName;
        SOAPClientNode soapClientNode = SOAPClientNode.builder()
                .soapUrl(soapUrl)
                .soapAction(soapActionName)
                .headersMap(getHeadersMap())
                .requestNode(request)
                .build();
        JsonNode response = soapClientNode.callSoapWebService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private Map<String, String> getHeadersMap() {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.putAll(SharedApplicationContext.getHeaders());
        headersMap.putAll(Constant.headersMap0);
        return headersMap;
    }

}
