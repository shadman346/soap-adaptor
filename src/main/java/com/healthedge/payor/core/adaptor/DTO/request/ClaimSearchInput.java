package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ClaimSearchInput {
    Integer maxRows;
    Integer rowsPerPage;
    Integer startRow;
    Boolean startFromLastRow;
    Boolean returnPreviousRows;
    SortBy sortBy;
    RequestedColumns requestedColumns;
    String hccClaimNumber;
    Boolean isReplaced;
    String claimType;
    String hccMemberId;
}
