package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLatestVersionOfClaimWithNoLogEntryRequest {
    Integer claimId;
}