package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.request.GetInstanceFromId;

public interface ConfigurationServiceWeaklyTyped {

    JsonNode getInstanceFromId(GetInstanceFromId jsonNode);

    JsonNode getHicSettings(JsonNode jsonNode);

}
