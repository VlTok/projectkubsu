package com.kubsu.project.controllers;

import com.kubsu.project.domain.Person;
import com.kubsu.project.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/main")
    public String getAllUsers(Model model) {
        Iterable<Person> users = userRepository.findAll();

        model.addAttribute("users",users);

        return "main";
    }

    @PostMapping("/main")
    public String addNewUser (@RequestParam String name
            , @RequestParam String email, Model model) {

        Person user = new Person(name,email);

        userRepository.save(user);

        Iterable<Person> users = userRepository.findAll();

        model.addAttribute("users", users);

        return "main";
    }


}
