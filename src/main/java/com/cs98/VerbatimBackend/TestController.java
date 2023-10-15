package com.cs98.VerbatimBackend;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Setting origins to "*" means that any origin is allowed to make requests to our Spring Boot server.
 * This completely disables CORS protection, which can be a security risk in production environments.
 * Therefore, in the future, we will have to specify the allowed origins explicitly or apply more fine-grained CORS configuration in a production environment.
 */
@CrossOrigin(origins = "*") 
@RestController
public class TestController {

    @GetMapping(path="/api/v1/helloWorld")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello, world!");
    }
}
