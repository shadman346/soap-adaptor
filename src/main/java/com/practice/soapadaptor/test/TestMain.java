package com.practice.soapadaptor.test;

import com.practice.soapadaptor.generatedold.numberconversion.NumberConversion;
import com.practice.soapadaptor.generatedold.numberconversion.NumberConversionSoapType;

import java.math.BigDecimal;

public class TestMain {
    public static void main(String[] args) {
        NumberConversion service = new NumberConversion();
        NumberConversionSoapType numberConversionService = service.getNumberConversionSoap();

        String res = numberConversionService.numberToDollars(BigDecimal.valueOf(50));
        System.out.println(res);
    }
}



//    Method[] methods = CalculatorSoap.class.getDeclaredMethods();
//    StackWalker walker = StackWalker.getInstance();
//    Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));
//        for (Method method : methods){
//                if(method.getName().equals(methodName.orElse(null)) && method.isAnnotationPresent(WebMethod.class)){
//        WebMethod fAnno = method.getAnnotation(WebMethod.class);
//        soapActionMap.putIfAbsent(method.getName(), fAnno.action());
//        break;
//        }
//        }
//        Map<String,String> headersMap = new HashMap<>();
//        headersMap.put(Constant.SOAPACTION,soapActionMap.get(methodName.orElse("")));
