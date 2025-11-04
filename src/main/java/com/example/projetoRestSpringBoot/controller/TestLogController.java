package com.example.projetoRestSpringBoot.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TestLogController {

    private Logger logger = LoggerFactory.getLogger(TestLogController.class.getName());
    @GetMapping("/test/v1")
    public String testLog(){
        logger.debug("Este e o DEBUG log");
        logger.info("Este e o INFO log");
        logger.warn("Este e o WARN log");
        logger.error("Este e o ERROR log");
        return "logs gerados com sucessos";
    }
}
