package com.healthedge.payor.core.adaptor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthedge.payor.core.adaptor.DTO.request.FindClaimRequest;
import com.healthedge.payor.core.adaptor.DTO.request.GetClaimSummaryInfoRequest;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.processors.ProcessorManager;
import com.healthedge.payor.core.adaptor.service.ClaimServiceWeaklyTyped;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClaimServiceWeaklyTypedImpl implements ClaimServiceWeaklyTyped {

    public static final String CLAIM_SERVICE_WEAKLY_TYPED = "ClaimServiceWeaklyTyped";
    public static final String FIND_CLAIMS = "findClaims";
    public static final String GET_CLAIM_SUMMARY_INFO = "getClaimSummaryInfo";
    @Autowired
    private ProcessorManager processorManager;

    @Autowired
    private RequestResponseBean requestResponseBean;


    @Override
    public JsonNode findClaims(FindClaimRequest jsonRequest) {
        log.info("Executing: {}", "ADAPTOR_SERVICE_LAYER");
        return doPost(FIND_CLAIMS, jsonRequest);
    }

    /**
     * @param jsonRequest
     * @return
     */
    @Override
    public JsonNode getClaimSummaryInfo(GetClaimSummaryInfoRequest jsonRequest) {
       return doPost(GET_CLAIM_SUMMARY_INFO,jsonRequest);
    }

    private JsonNode doPost(String soapActionName, Object jsonRequest) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodeRequest = mapper.valueToTree(jsonRequest);
        setRequestResponseBean(soapActionName, nodeRequest);
        return processorManager.init(requestResponseBean)
                .processSoapMessage()
                .sendSoapMessage()
                .processJsonResponse()
                .getJsonResponse();
    }

    private void setRequestResponseBean(String soapActionName, JsonNode jsonRequest) {
        requestResponseBean.setJsonRequest(jsonRequest);
        requestResponseBean.setSoapAction(soapActionName);
        requestResponseBean.setSoapWebServiceName(CLAIM_SERVICE_WEAKLY_TYPED);
        requestResponseBean.setHeadersMap(SoapMessageUtil.getHeadersMap());
    }


}
