package com.kubsu.project.controllers;

import com.kubsu.project.excel.ExcelWriter;
import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.repos.ScheduleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

@Controller
public class MainController {

    private final ScheduleRepository postRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public MainController(ScheduleRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/main")
    public String mainTimeTable(@RequestParam(required = false, defaultValue = "") String team, @RequestParam(required = false, defaultValue = "") String subgroup,
                                Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Schedule> page;
        if ((team != null && !team.isEmpty()) && (subgroup != null && !subgroup.isEmpty())) {
            page = postRepository.findByTeamAndSubgroup(team, subgroup, pageable);
        } else {
            page = postRepository.findAll(pageable);
        }
        model.addAttribute("page", page);
        model.addAttribute("team", team);
        model.addAttribute("subgroup", subgroup);

        return "main";
    }

    @GetMapping("/error")
    public String mainError(Model model) {
        return "error";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/add")
    public String mainAdd(Schedule schedule, Model model) {
        return "main-add";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/add")
    public String mainPostAdd(@AuthenticationPrincipal User user, @Valid Schedule schedule, BindingResult bindingResult, Model model) {
        schedule.setAuthor(user);

        if (bindingResult.hasErrors()) {

            model.addAttribute("post", schedule);
            return "main-add";
        } else {
            model.addAttribute("post", null);
            postRepository.save(schedule);
        }
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/{id}/remove")
    public String blogPostDelete(@AuthenticationPrincipal User currentUser, @PathVariable(value = "id") Long id, Model model) {
        Schedule schedule = postRepository.findById(id).orElseThrow();
        if (schedule.getAuthor().getUsername().equals(currentUser.getUsername())) {
            postRepository.delete(schedule);
        }
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/download")
    public String downloadScheduleForm(Model model) {
        return "download-files";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/download")
    public String downloadSchedule(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file) throws IOException, ParseException {
        if (!file.isEmpty()) {

            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            String pathname = uploadPath + "/" + resultFilename;

            file.transferTo(new File(pathname));

            ExcelWriter.readExcelFile(user, pathname);
        }

        return "download-files";
    }
}
