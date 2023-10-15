package com.cs98.VerbatimBackend;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class TestController {

    @GetMapping(path="/api/v1/helloWorld")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello, world!");
    }
}
