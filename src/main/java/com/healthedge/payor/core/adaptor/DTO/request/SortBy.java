package com.healthedge.payor.core.adaptor.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortBy {
    @JsonProperty("SortColumn")
    SortColumn SortColumn;
}
