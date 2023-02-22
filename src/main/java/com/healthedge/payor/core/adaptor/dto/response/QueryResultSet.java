package com.healthedge.payor.core.adaptor.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryResultSet {

    @JsonProperty("Metadata")
    MetaData metaData;

    @JsonProperty("Row")
    List<Row> rows;

    @JsonProperty("Truncated")
    Boolean truncated;

    @JsonProperty("VIPExcluded")
    Boolean VIPExcluded;

}
