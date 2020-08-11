package com.kubsu.project.controllers;

import com.kubsu.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartController {
    final UserService userService;

    public StartController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String begin(Model model){
        return "begin";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}
