package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestedColumn {
    List<String> columnName;
}
