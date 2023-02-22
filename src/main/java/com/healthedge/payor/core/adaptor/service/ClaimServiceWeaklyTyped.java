package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ClaimServiceWeaklyTyped {

     JsonNode findClaims(JsonNode jsonRequest);
     JsonNode getClaimSummaryInfo(JsonNode jsonRequest);
}
