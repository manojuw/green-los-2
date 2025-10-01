package com.mixo.restController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/aeps")
@Slf4j
public class AepsKycController {

    @PostMapping("/RespKycData/2.0/urn:txnid:{txnId}")
    public ResponseEntity<String> handleKycResponse(@PathVariable("txnId") String txnId, @RequestBody String xmlRequest) {
        
        // Log the received XML
        log.info("Received KYC Response for txnId: " + txnId);
        log.info("XML Request: " + xmlRequest);

        // Process the XML request (Add your logic here)
        
        return ResponseEntity.ok("KYC response received successfully for txnId: " + txnId);
    }
}