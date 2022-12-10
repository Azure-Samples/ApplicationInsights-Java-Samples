// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @GetMapping("/")
    public String root() {
        return "OK";
    }
}
