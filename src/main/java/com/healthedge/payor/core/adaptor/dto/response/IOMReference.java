package com.healthedge.payor.core.adaptor.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IOMReference {

    @JsonProperty("ID")
    Long id;

    @JsonProperty("type")
    String type;

    public IOMReference(Long id, String type){
        this.id=id;
        this.type=type;
    }


}
