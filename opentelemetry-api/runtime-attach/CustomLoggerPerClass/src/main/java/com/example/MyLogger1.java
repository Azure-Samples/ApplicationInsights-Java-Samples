package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;

import java.util.HashMap;
import java.util.Map;

class MyLogger1 {

  private static final Logger logger = LogManager.getLogger(MyLogger1.class);

  public void trackWarn() {
    Map<String, Object> mapMessage = new HashMap<>();
    mapMessage.put("key", "track");
    mapMessage.put("message", "INFO log message from MyLogger1 with custom attributes will get ignored");
    logger.info(new MapMessage<>(mapMessage)); // this log will get ignored because MyLogger1 is at logging level WARN
    ThreadContext.clearAll();
    logger.warn("WARN log message from MyLogger1 without custom attributes");
  }
}
