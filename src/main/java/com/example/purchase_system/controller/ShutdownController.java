package com.example.purchase_system.controller;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class ShutdownController {
    private final ConfigurableApplicationContext context;

    public ShutdownController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @PostMapping("/shutdown")
    public String shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                context.close();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "系統正在關閉";
    }
}
