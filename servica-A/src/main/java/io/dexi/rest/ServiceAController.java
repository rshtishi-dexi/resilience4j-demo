package io.dexi.rest;

import io.dexi.client.ServiceBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceAController {

    @Autowired
    private ServiceBClient serviceBClient;

    @GetMapping("/hello")
    public String hello() {
        return "Hello "+serviceBClient.getUser();
    }
}
