package com.healthedge.payor.core.adaptor.DTO.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class MetaData {

    @JsonUnwrapped
    private Map<String, Object> columns;

    public Map<String, Object> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Object> columns) {
        this.columns = columns;
    }

    public void setColumnsMap(Map<String,Object> map){
        this.columns=map;
    }

}
