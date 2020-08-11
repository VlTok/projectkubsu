package com.kubsu.project.controllers;

import com.kubsu.project.models.User;
import com.kubsu.project.models.dto.CaptchaResponseDto;
import com.kubsu.project.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;

@Controller
public class RegistrationController {
    private final static String CAPTCHA_URL= "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    private final UserService userService;
    private final RestTemplate restTemplate;

    public RegistrationController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @Value("${recaptcha.secret}")
    public String secret;

    @GetMapping("/registration")
    public String registration(User user, Model model){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("password2") String passwordConfirm,@RequestParam("g-recaptcha-response") String captchaResponse, @Valid User user, BindingResult bindingResult, Model model){
        String url=String.format(CAPTCHA_URL,secret,captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        if (response == null) {
            throw new AssertionError();
        }
        if(!response.isSuccess()){
            model.addAttribute("captchaError", "Заполните капчу!");
        }

        ControllerUtils controllerUtils=new ControllerUtils();

        boolean isConfirmEmpty = controllerUtils.isConfirmEmptyAndPasswordError(passwordConfirm, user, model);

        if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()){
            return "registration";
        }

        if (!userService.addUser(user)){
            model.addAttribute("usernameError", "Пользователь уже существует!");
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
