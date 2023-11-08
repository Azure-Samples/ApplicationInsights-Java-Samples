package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;

import java.util.HashMap;
import java.util.Map;

class MyLogger2 {

    private static final Logger logger = LogManager.getLogger(MyLogger2.class);

    public void trackError() {
        Map<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("key", "track");
        mapMessage.put("message", "DEBUG log message from MyLogger2 with custom attributes will get ignored");
        logger.debug(new MapMessage<>(mapMessage)); // this log will get ignored because MyLogger2 is at logging level ERROR
        ThreadContext.clearAll();
        logger.error("ERROR log message from MyLogger2 without custom attributes");
    }
}
