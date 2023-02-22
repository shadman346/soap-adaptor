package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortColumn {
    String columnName;
    Boolean isDescending;
}
