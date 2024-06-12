package com.goodsun.goodsunbackend.controller;

import com.goodsun.goodsunbackend.model.request.*;
import com.goodsun.goodsunbackend.model.response.Results;
import com.goodsun.goodsunbackend.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles the API requests for the GoodSun backend.
 * @author Jonas Nunnenmacher
 */
@RestController
@RequestMapping(path="api/v1")
public class Controller {
    private final CalculatorService calculatorService;

    /**
     * Constructor for Controller.
     *
     * @param calculatorService the service used for calculations
     */
    @Autowired
    public Controller(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    /**
     * Endpoint to calculate the yield based on user data.
     *
     * @param userData the user data for calculation
     * @return the calculated results (yield of the pv-system in kWh/year with and without shadowing)
     */
    @PostMapping("/yield")
    @ResponseBody
    public Results yield(@RequestBody UserData userData){
        return calculatorService.calculateResult(userData);
    }

    /**
     * Endpoint to check if the server is alive and to keep the server active on render.
     *
     * @return a success message
     */
    @GetMapping("/keepalive")
    public String keepAlive(){
        return "success";
    }
}