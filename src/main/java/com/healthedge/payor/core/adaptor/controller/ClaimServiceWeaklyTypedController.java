package com.healthedge.payor.core.adaptor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.request.FindClaimRequest;
import com.healthedge.payor.core.adaptor.DTO.request.GetClaimSummaryInfoRequest;
import com.healthedge.payor.core.adaptor.service.ClaimServiceWeaklyTyped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/claim")
public class ClaimServiceWeaklyTypedController {
    @Autowired
    private ClaimServiceWeaklyTyped claimServiceWeaklyTyped;


    @GetMapping("/hello")
    public String hello() {
        return "All is well";
    }


    @PostMapping("/findClaims")
    public ResponseEntity<JsonNode> findClaim(
            @RequestBody FindClaimRequest jsonRequest) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                claimServiceWeaklyTyped.findClaims(jsonRequest)
        );
    }

    @PostMapping("/getClaimSummaryInfo")
    public ResponseEntity<JsonNode> getClaimSummaryInfo(
            @RequestBody GetClaimSummaryInfoRequest jsonRequest) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                claimServiceWeaklyTyped.getClaimSummaryInfo(jsonRequest)
        );
    }


}