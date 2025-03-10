package com.example.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @GetMapping("/api/test")
    public ResponseEntity<String> test(@RequestParam(value = "val", defaultValue = "값") String val) {
        return ResponseEntity.ok(val);
    }

}
