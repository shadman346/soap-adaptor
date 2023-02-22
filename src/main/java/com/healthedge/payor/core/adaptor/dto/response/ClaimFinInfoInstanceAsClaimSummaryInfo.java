package com.healthedge.payor.core.adaptor.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimFinInfoInstanceAsClaimSummaryInfo {
    @JsonProperty("ClaimSummaryInfo")
    ClaimSummaryInfo claimSummaryInfo;
}
