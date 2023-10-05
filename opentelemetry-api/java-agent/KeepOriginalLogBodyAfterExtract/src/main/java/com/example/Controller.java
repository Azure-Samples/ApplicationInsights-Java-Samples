// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);

    @GetMapping("/")
    public String root() {
        return "OK!";
    }

    @GetMapping("/hello")
    public String hello() {
        logger.info("Hello, this is testing PID 9876");
        return "Hello!";
    }
}
