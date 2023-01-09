package com.practice.soapadaptor.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import com.practice.soapadaptor.client.SOAPClientSAAJ;
import com.practice.soapadaptor.constants.Constant;
import com.practice.soapadaptor.context.SharedApplicationContext;
import com.practice.soapadaptor.wsgenerated.ClaimServiceWeaklyTyped.FindClaims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/claim-service")
public class ClaimServiceWeaklyTypedController {
    private static final String SOAP_URL = "http://heeng-speedskat:7001/web-services/ClaimServiceWeaklyTyped";
    @PostMapping("/find-claims")
    public ResponseEntity<JsonNode> findClaims(
            @RequestBody FindClaims request) throws Exception {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.putAll(SharedApplicationContext.getHeaders());
        headersMap.putAll(Constant.headersMap0);
        SOAPClientSAAJ<FindClaims, JsonNode> soapClientSAAJ = SOAPClientSAAJ.<FindClaims, JsonNode>builder()
                .soapUrl(SOAP_URL).headersMap(headersMap).nameSpaceUriMap(Constant.nameSpaceUriMap0)
                .request(request).responseType(JsonNode.class).build();
        JsonNode response = soapClientSAAJ.callSoapWebService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
