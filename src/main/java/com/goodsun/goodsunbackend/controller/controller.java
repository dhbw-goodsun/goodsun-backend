package com.goodsun.goodsunbackend.controller;

import com.goodsun.goodsunbackend.model.request.*;
import com.goodsun.goodsunbackend.model.response.Results;
import com.goodsun.goodsunbackend.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="api/v1")
public class controller {

    private final CalculatorService calculatorService;

    @Autowired
    public controller(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/yield")
    @ResponseBody
    public Results yield(@RequestBody UserData userData){
        return calculatorService.calculateResult(userData);
    }

    @GetMapping("/keepalive")
    public String keepAlive(){
        return "success";
    }
}