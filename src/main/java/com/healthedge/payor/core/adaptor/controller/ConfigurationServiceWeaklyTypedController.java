package com.healthedge.payor.core.adaptor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.request.GetInstanceFromId;
import com.healthedge.payor.core.adaptor.service.ConfigurationServiceWeaklyTyped;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/configuration")
public class ConfigurationServiceWeaklyTypedController {

    @Autowired
    private ConfigurationServiceWeaklyTyped configurationServiceWeaklyTyped;

    @PostMapping("/getInstanceFromId")
    public ResponseEntity<JsonNode> getInstanceFromId(@RequestBody GetInstanceFromId jsonRequest)
         throws Exception{
            return ResponseEntity.status(HttpStatus.OK).body(
                    configurationServiceWeaklyTyped.getInstanceFromId(jsonRequest)
            );
    }

    @GetMapping("/getHicSettings")
    public ResponseEntity<JsonNode> getHicSettings(@RequestBody JsonNode jsonRequest) throws Exception{
        return ResponseEntity.status(HttpStatus.OK).body(
                configurationServiceWeaklyTyped.getHicSettings(jsonRequest)
        );
    }
}
