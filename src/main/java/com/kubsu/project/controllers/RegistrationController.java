package com.kubsu.project.controllers;

import com.kubsu.project.domain.User;
import com.kubsu.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(){
        
        return "registration";
    }

    @PostMapping("/registration")
    public String addPerson(User user, Model model){
        if (!userService.addUser(user)){
            model.addAttribute("usernameError", "User exists");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated=userService.activateUser(code);
        if(isActivated){
            model.addAttribute("messageType","success");
            model.addAttribute("message","Пользователь успешно активирован!");
        }else{
            model.addAttribute("messageType","danger");
            model.addAttribute("message","Активационный код не найден!");
        }
        return "login";
    }
}
