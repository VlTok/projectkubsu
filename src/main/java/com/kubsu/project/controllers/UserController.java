package com.kubsu.project.controllers;

import com.kubsu.project.domain.User;
import com.kubsu.project.domain.Role;
import com.kubsu.project.repos.UserRepository;
import com.kubsu.project.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userService.findAll());

        return "user-list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable Long user, Model model){
        model.addAttribute("user", userService.findById(user));
        model.addAttribute("roles", Role.values());

        return "user-edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(@RequestParam String username, @RequestParam Map<String,String> form, @RequestParam("userId")Long id) {
        User user = userService.findById(id);
        userService.saveUser(user, username, form);

        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username",user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "user-profile";
    }

    @PostMapping("profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String password,
                                @RequestParam String email
    ){
        userService.updateProfile(user,password,email);
        return "redirect:/user/profile";
    }
}
