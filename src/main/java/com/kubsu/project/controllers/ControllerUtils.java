package com.kubsu.project.controllers;

import com.kubsu.project.models.User;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.*;

public class ControllerUtils {
    public ControllerUtils() {
    }

    public boolean isConfirmEmptyAndPasswordError(@RequestParam("password2") String passwordConfirm, @Valid User user, Model model) {
        boolean isConfirmEmpty= StringUtils.isEmpty(passwordConfirm);
        model.addAttribute("user",user);
        if (isConfirmEmpty){
            model.addAttribute("password2Error","Потверждение пароля не может быть пустым!");
        }
        if(user.getPassword()!=null&& !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError","Пароли не совпадают!");
        }
        return isConfirmEmpty;
    }
}
