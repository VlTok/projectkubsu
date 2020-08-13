package com.kubsu.project.controllers;

import com.kubsu.project.models.User;
import com.kubsu.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecoverController {
    private final UserService userService;

    public RecoverController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/recover")
    public String recover(Model model){
        return "recover-user";
    }
    @PostMapping("/recover")
    public String recoverUser(@RequestParam String username, @RequestParam String email, Model model){

        User user = userService.findByUsernameAndEmail(username,email);

        if (user==null){
            model.addAttribute("nonExistsUser","Такого пользователя или такой почты не существует!");
            return "recover-user";
        }

        userService.recoverUser(user);

        return "redirect:/login";
    }
}
