// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/")
    public String root() {
        return "OK";
    }

    @GetMapping("/longDuration")
    public String longDuration() throws Exception {
        Span.current().setAttribute("enduser.id", "myuser");
        Span.current().updateName("myLongDurationSpan");
        Thread.sleep(6000); // make duration longer than 5 seconds
        return "TEST";
    }

    @GetMapping("/shortDuration")
    public String shortDuration() throws Exception {
        Span.current().setAttribute("enduser.id", "myuser");
        Span.current().updateName("myShortDurationSpan");
        return "TEST";
    }
}
