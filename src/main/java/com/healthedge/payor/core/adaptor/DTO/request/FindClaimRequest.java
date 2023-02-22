package com.healthedge.payor.core.adaptor.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
public class FindClaimRequest {
    @NonNull
    @JsonProperty("searchInputXml")
    SearchInputXml searchInputXml;
}
