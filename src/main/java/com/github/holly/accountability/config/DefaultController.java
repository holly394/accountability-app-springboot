package com.github.holly.accountability.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Order(Ordered.LOWEST_PRECEDENCE)
@Controller
public class DefaultController {
    @Order(Ordered.LOWEST_PRECEDENCE)
    @GetMapping("{_:^(?!index\\.html|api|error|h2-console).*$}")
    public String fallback() {
        return "forward:/index.html";
    }

}
