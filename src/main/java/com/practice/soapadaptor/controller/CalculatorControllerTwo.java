package com.practice.soapadaptor.controller;

import com.practice.soapadaptor.generated.calculator.*;
import com.practice.soapadaptor.service.impl.CalculatorServiceTwoImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorControllerTwo {
    private final CalculatorServiceTwoImpl calculatorServiceTwo;

    public CalculatorControllerTwo(CalculatorServiceTwoImpl calculatorServiceTwo) {
        this.calculatorServiceTwo = calculatorServiceTwo;
    }

    @PostMapping("/add2")
    public ResponseEntity<AddResponse> addNumbers2(@RequestBody Add addRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceTwo.add(addRequest));
    }

    @PostMapping("/subtract2")
    public ResponseEntity<SubtractResponse> subtractNumbers2(@RequestBody Subtract subtractRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceTwo.subtract(subtractRequest));
    }

    @PostMapping("/divide2")
    public ResponseEntity<DivideResponse> divideNumbers2(@RequestBody Divide divideRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceTwo.divide(divideRequest));
    }

    @PostMapping("/multiply2")
    public ResponseEntity<MultiplyResponse> multiplyNumbers2(@RequestBody Multiply multiplyRequest){
        return ResponseEntity.status(HttpStatus.OK).body(calculatorServiceTwo.multiply(multiplyRequest));
    }
}
