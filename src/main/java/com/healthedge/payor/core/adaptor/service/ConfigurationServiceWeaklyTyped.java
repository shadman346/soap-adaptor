package com.healthedge.payor.core.adaptor.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ConfigurationServiceWeaklyTyped {

    JsonNode getInstanceFromId(JsonNode jsonNode);

    JsonNode getHicSettings(JsonNode jsonNode);

}
