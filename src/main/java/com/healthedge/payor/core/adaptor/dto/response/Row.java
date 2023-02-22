package com.healthedge.payor.core.adaptor.DTO.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Row {


    private Map<String, Object> column=new HashMap<>();

    @JsonProperty("originalRowNumber")
    Integer originalRowNumber;

    @JsonAnyGetter
    public Map<String, Object> getColumn() {
        return column;
    }

    @JsonAnySetter
    public void setColumn(String name, Object value) {
        column.put(name, value);
    }

    public void setColumnMap(Map<String,Object> map){
        this.column=map;
    }
}
