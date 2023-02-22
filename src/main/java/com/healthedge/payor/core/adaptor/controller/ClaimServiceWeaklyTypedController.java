package com.healthedge.payor.core.adaptor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.healthedge.payor.core.adaptor.DTO.response.ClaimSummaryInfoResponse;
import com.healthedge.payor.core.adaptor.DTO.response.FindClaimsResponse;
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
    public ResponseEntity<FindClaimsResponse> findClaim(
            @RequestBody JsonNode jsonRequest) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                claimServiceWeaklyTyped.findClaims(jsonRequest)
        );
    }

    @PostMapping("/getLatestVersionOfClaimWithNoLogEntry")
    public ResponseEntity<JsonNode> getLatestVersionOfClaimWithNoLogEntry(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(
                claimServiceWeaklyTyped.getLatestVersionOfClaimWithNoLogEntry(jsonNode));
    }

    @PostMapping("/getClaimSummaryInfo")
    public ResponseEntity<ClaimSummaryInfoResponse> getClaimSummaryInfo(
            @RequestBody JsonNode jsonRequest) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                claimServiceWeaklyTyped.getClaimSummaryInfo(jsonRequest)
        );
    }

}