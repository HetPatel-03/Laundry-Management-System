package com.laundry.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/login.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin/dashboard.html";
    }
    
    @GetMapping("/staff")
    public String staff() {
        return "forward:/staff/dashboard.html";
    }
    
    @GetMapping("/customer")
    public String customer() {
        return "forward:/customer/dashboard.html";
    }
    
    @GetMapping("/api-docs")
    public String apiDocs() {
        return "redirect:/swagger-ui.html";
    }
}