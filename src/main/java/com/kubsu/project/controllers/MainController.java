package com.kubsu.project.controllers;

import com.kubsu.project.excel.ExcelWorker;
import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.models.dto.ScheduleDto;
import com.kubsu.project.service.ScheduleService;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
public class MainController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${errors.path}")
    private String errorsPath;

    private final ScheduleService scheduleService;

    public MainController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/main")
    public String mainTable(@RequestParam(required = false, defaultValue = "") String team,
                            Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Schedule> page = scheduleService.findAll(pageable,team);
        model.addAttribute("page", page);
        model.addAttribute("team", team);

        return "main";
    }

    @GetMapping("/error")
    public String mainError(Model model) {
        return "error";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/{id}/remove")
    public String blogScheduleDelete(@AuthenticationPrincipal User currentUser, @PathVariable(value = "id") Long id, Model model) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule.getAuthor().getUsername().equals(currentUser.getUsername())) {
            scheduleService.delete(schedule);
        }
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/upload")
    public String uploadScheduleForm(Model model) {
        return "upload-files";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/upload")
    public String uploadSchedule(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file) throws IOException, ParseException {
        if (!file.isEmpty() && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            File errorsDir = new File(errorsPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            if (!errorsDir.exists()){
                errorsDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultUploadFilename = uuidFile + "." + file.getOriginalFilename();
            String resultErrorFilename = uuidFile +".error_file.txt";
            String uploadFilePathname = uploadPath + "/" + resultUploadFilename;
            String fileWithErrorsInfoPathname = errorsPath + "/" + resultErrorFilename;

            file.transferTo(new File(uploadFilePathname));
            List<Schedule> scheduleListFromDb = scheduleService.findAllByAuthor(user);
            List<Schedule> scheduleListForAddIntoDb = new ArrayList<>();
            Set<String> someErrors= ExcelWorker.readExcelFile(scheduleListFromDb, scheduleListForAddIntoDb,user, uploadFilePathname, fileWithErrorsInfoPathname);
        }

        return "upload-files";
    }
}
