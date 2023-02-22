package com.healthedge.payor.core.adaptor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthedge.payor.core.adaptor.DTO.request.GetInstanceFromId;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.processors.ProcessorManager;
import com.healthedge.payor.core.adaptor.service.ConfigurationServiceWeaklyTyped;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigurationServiceWeaklyTypedImpl implements ConfigurationServiceWeaklyTyped {

    public static final String CONFIGURATION_SERVICE_WEAKLY_TYPED = "ConfigurationServiceWeaklyTyped";
    public static final String GET_INSTANCE_FROM_ID = "getInstanceFromId";
    public static final String GET_HIC_SETTINGS = "getHicSettings";
    @Autowired
    private ProcessorManager processorManager;

    @Autowired
    private RequestResponseBean requestResponseBean;

    @Override
    public JsonNode getInstanceFromId(GetInstanceFromId jsonNode) {
        return doPost(GET_INSTANCE_FROM_ID,jsonNode);
    }

    /**
     * @param jsonNode
     * @return
     */
    @Override
    public JsonNode getHicSettings(JsonNode jsonNode) {
        return doPost(GET_HIC_SETTINGS,jsonNode);
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
        requestResponseBean.setSoapWebServiceName(CONFIGURATION_SERVICE_WEAKLY_TYPED);
        requestResponseBean.setHeadersMap(SoapMessageUtil.getHeadersMap());
    }
}
