package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.response.ClaimSummaryInfoResponse;
import com.healthedge.payor.core.adaptor.DTO.response.FindClaimsResponse;

public interface ClaimServiceWeaklyTyped {

     FindClaimsResponse findClaims(JsonNode jsonRequest) throws JsonProcessingException;
     ClaimSummaryInfoResponse getClaimSummaryInfo(JsonNode jsonRequest) throws JsonProcessingException;
     JsonNode getLatestVersionOfClaimWithNoLogEntry(JsonNode jsonNode);
}
