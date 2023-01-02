package com.practice.soapadaptor.service.implold;

import com.practice.soapadaptor.generatedold.calculator.*;
import org.springframework.stereotype.Service;

@Service
public class CalculatorServiceTwoImpl implements CalculatorSoap {
    private final CalculatorSoap calculatorSoapService;

    public CalculatorServiceTwoImpl() {
        Calculator service = new Calculator();
        this.calculatorSoapService = service.getCalculatorSoap();
    }

    @Override
    public AddResponse add(Add add) {
       return calculatorSoapService.add(add);
    }

    @Override
    public MultiplyResponse multiply(Multiply multiply) {
        return calculatorSoapService.multiply(multiply);
    }

    @Override
    public DivideResponse divide(Divide divide) {
        return calculatorSoapService.divide(divide);
    }

    @Override
    public SubtractResponse subtract(Subtract subtract) {
        return calculatorSoapService.subtract(subtract);
    }
}
