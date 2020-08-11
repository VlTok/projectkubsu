package com.kubsu.project.controllers;

import com.kubsu.project.models.Role;
import com.kubsu.project.models.User;
import com.kubsu.project.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public String getProfile( @AuthenticationPrincipal User userPresent,User user,Model model){
        model.addAttribute("usernamePresent",userPresent.getUsername());
        model.addAttribute("emailPresent",userPresent.getEmail());

        return "user-profile";
    }

    @PostMapping("profile")
    public String updateProfile(@AuthenticationPrincipal User userPresent, @RequestParam("password2") String passwordConfirm,@Valid User user
                                , BindingResult bindingResult,
                                Model model){
        ControllerUtils controllerUtils=new ControllerUtils();

        boolean isConfirmEmpty = controllerUtils.isConfirmEmptyAndPasswordError(passwordConfirm, user, model);
        if(isConfirmEmpty || bindingResult.hasErrors()){
            return "user-profile";
        }

        boolean userExists= userService.existsUser(user);
        if (!userExists){
            model.addAttribute("usernameError", "Такое имя пользователя уже существует!");
            return "user-profile";
        }
        userService.updateProfile(userPresent,user.getPassword(),user.getEmail(),user.getUsername());
        return "redirect:/main";
    }

}
