package com.practice.soapadaptor.controller.old;

import com.practice.soapadaptor.generatedold.calculator.*;
import com.practice.soapadaptor.service.implold.CalculatorServiceOneImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorControllerOne {
    private final CalculatorServiceOneImpl calculatorServiceOne;

    public CalculatorControllerOne(CalculatorServiceOneImpl calculatorService) {
        this.calculatorServiceOne = calculatorService;
    }

    @PostMapping("/add1")
    public ResponseEntity<AddResponse> addNumbers1(@RequestBody Add addRequest){
       return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceOne.add(addRequest));
   }

    @PostMapping("/subtract1")
    public ResponseEntity<SubtractResponse> subtractNumbers1(@RequestBody Subtract subtractRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceOne.subtract(subtractRequest));
    }

    @PostMapping("/divide1")
    public ResponseEntity<DivideResponse> divideNumbers1(@RequestBody Divide divideRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceOne.divide(divideRequest));
    }

    @PostMapping("/multiply1")
    public ResponseEntity<MultiplyResponse> multiplyNumbers1(@RequestBody Multiply multiplyRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceOne.multiply(multiplyRequest));
    }

}

