package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetClaimSummaryInfoRequest {
    Integer claimId;
    String w3cVersionDate;
    String w3cProcessingDate;
    String hccClaimNumber;

}
