package com.otg.tech.test.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TestController {

    @PostMapping
    public Map<String, String> doSth(@RequestBody Map<String, String> request) {
        return Map.of("response", "test-response");
    }

    @PostMapping
    public Map<String, String> doSthOnException(@RequestBody Map<String, String> request) {
        throw new RuntimeException("testing for exception");
    }
}
