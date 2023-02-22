package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.request.FindClaimRequest;
import com.healthedge.payor.core.adaptor.DTO.request.GetClaimSummaryInfoRequest;

public interface ClaimServiceWeaklyTyped {

     JsonNode findClaims(FindClaimRequest jsonRequest);
     JsonNode getClaimSummaryInfo(GetClaimSummaryInfoRequest jsonRequest);
}
