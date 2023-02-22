package com.healthedge.payor.core.adaptor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthedge.payor.core.adaptor.DTO.response.ClaimSummaryInfoResponse;
import com.healthedge.payor.core.adaptor.DTO.response.FindClaimsResponse;
import com.healthedge.payor.core.adaptor.DTO.response.QueryResultSet;
import com.healthedge.payor.core.adaptor.DTO.response.Row;
import com.healthedge.payor.core.adaptor.context.RequestResponseBean;
import com.healthedge.payor.core.adaptor.processors.ProcessorManager;
import com.healthedge.payor.core.adaptor.service.ClaimServiceWeaklyTyped;
import com.healthedge.payor.core.adaptor.util.CustomParser;
import com.healthedge.payor.core.adaptor.util.SoapMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ClaimServiceWeaklyTypedImpl implements ClaimServiceWeaklyTyped {

    public static final String CLAIM_SERVICE_WEAKLY_TYPED = "ClaimServiceWeaklyTyped";
    public static final String FIND_CLAIMS = "findClaims";
    public static final String GET_CLAIM_SUMMARY_INFO = "getClaimSummaryInfo";
    public static final String GET_LATEST_VERSION_OF_CLAIM_WITH_NO_LOG_ENTRY = "getLatestVersionOfClaimWithNoLogEntry";
    @Autowired
    CustomParser customParser;
    @Autowired
    private ProcessorManager processorManager;
    @Autowired
    private RequestResponseBean requestResponseBean;


    @Override
    public FindClaimsResponse findClaims(JsonNode jsonRequest) throws JsonProcessingException {
        JsonNode response = doPost(FIND_CLAIMS, jsonRequest);
        FindClaimsResponse findClaimsResponse = new FindClaimsResponse();
        findClaimsResponse.setQueryResultSet(deserializeQueryResultSet(response));
        return findClaimsResponse;
    }

    private QueryResultSet deserializeQueryResultSet(JsonNode jsonNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        JsonNode node = jsonNode.findValue("QueryResultSet");
        QueryResultSet queryResultSet = mapper.treeToValue(node, QueryResultSet.class);
        fillMetaData(queryResultSet, jsonNode.findValue("Metadata"));
        fillRows(queryResultSet, jsonNode.findValue("Row"));
        return queryResultSet;
    }

    public void fillRows(QueryResultSet queryResultSet, JsonNode jsonNode) {
        Map<Integer, JsonNode> colData = new HashMap<>();
        if (jsonNode.isArray()) {
            jsonNode.forEach(row -> {
                colData.putIfAbsent(row.get("originalRowNumber").asInt(), row);
            });
        } else {
            colData.putIfAbsent(jsonNode.get("originalRowNumber").asInt(), jsonNode);
        }
        Map<String, Object> dataTypes = queryResultSet.getMetaData().getColumns();
        if (!CollectionUtils.isEmpty(queryResultSet.getRows())) {
            for (Row row : queryResultSet.getRows()) {
                JsonNode data = colData.getOrDefault(row.getOriginalRowNumber(), null);
                Map<String, Object> map = new HashMap<>();
                if (Objects.nonNull(data)) {
                    data.findValue("Column").forEach(col -> {
                        try {
                            map.putIfAbsent(col.get("name").asText(), searchDataType(col, dataTypes));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                row.setColumnMap(map);
            }
        }
    }

    public Object searchDataType(JsonNode col, Map<String, Object> dataTypes) throws JsonProcessingException {
        return customParser.parse((String) dataTypes.get(col.get("name").asText()), col.get("value").asText());
    }

    public void fillMetaData(QueryResultSet queryResultSet, JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        jsonNode.findValue("Column").forEach(col -> {
            map.putIfAbsent(col.get("name").asText(), getDataType(col.get("type").asText()));
        });
        queryResultSet.getMetaData().setColumnsMap(map);
    }

    public String getDataType(String dataType) {
        String arr[] = dataType.split("\\.");
        return arr[arr.length - 1];
    }

    /**
     * @param jsonRequest
     * @return
     */
    @Override
    public ClaimSummaryInfoResponse getClaimSummaryInfo(JsonNode jsonRequest) throws JsonProcessingException {
         JsonNode response = doPost(GET_CLAIM_SUMMARY_INFO, jsonRequest);
         return deserialize(response.findValue("getClaimSummaryInfoResponse"),ClaimSummaryInfoResponse.class);
    }

    /**
     * @param jsonNode
     * @return
     */
    @Override
    public JsonNode getLatestVersionOfClaimWithNoLogEntry(JsonNode jsonNode) {
        return doPost(GET_LATEST_VERSION_OF_CLAIM_WITH_NO_LOG_ENTRY,jsonNode);
    }

    private <T> T deserialize(JsonNode jsonNode, Class<T> object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.treeToValue(jsonNode,object);
    }

    private JsonNode doPost(String soapActionName, JsonNode jsonRequest) {
        setRequestResponseBean(soapActionName, jsonRequest);
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
