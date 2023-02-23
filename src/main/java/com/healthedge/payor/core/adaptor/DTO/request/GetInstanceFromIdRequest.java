package com.healthedge.payor.core.adaptor.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetInstanceFromIdRequest {
    String iomTypeName;
    Integer id;
    String fulfilledCode;
}
