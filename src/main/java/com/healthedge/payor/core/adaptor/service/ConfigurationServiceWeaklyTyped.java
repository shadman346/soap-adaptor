package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.request.GetInstanceFromIdRequest;

public interface ConfigurationServiceWeaklyTyped {

    JsonNode getInstanceFromId(GetInstanceFromIdRequest jsonNode);

    JsonNode getHicSettings(JsonNode jsonNode);

}
