package com.kubsu.project.controllers;

import com.kubsu.project.excel.ExcelWorker;
import com.kubsu.project.models.Couple;
import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.service.CoupleService;
import com.kubsu.project.service.ScheduleService;
import com.kubsu.project.utils.Pagination;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.kubsu.project.utils.UrlUtil.createRightUrl;
import static java.util.Objects.nonNull;

@Controller
public class MainController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${errors.path}")
    private String errorsPath;

    @Value("${warnings.path}")
    private String warningsPath;

    private final ScheduleService scheduleService;
    private final CoupleService coupleService;

    public MainController(ScheduleService scheduleService, CoupleService coupleService) {
        this.scheduleService = scheduleService;
        this.coupleService = coupleService;
    }

    @GetMapping("/main")
    public String mainTable(@RequestParam(name = "team", value = "team", required = false) String team,
                            @RequestParam(name = "teacher", value = "teacher", required = false) String teacher,
                            @RequestParam(name = "dayOfWeek", value = "dayOfWeek", required = false) String dayOfWeek,
                            @RequestParam(name = "title", value = "title", required = false) String title,
                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable,
                            Model model) {


        Page<Schedule> page = scheduleService.findAll(nonNull(team) ? team.trim() : null,
                nonNull(teacher) ? teacher.trim() : null,
                nonNull(dayOfWeek) ? dayOfWeek.trim() : null,
                nonNull(title) ? title.trim() : null, pageable);
        Set<String> teams = scheduleService.findAllTeam();
        Set<String> teachers = coupleService.findAllTeachers();
        Set<String> titles = coupleService.findAllTitles();
        String paramsUrl = createRightUrl(team, teacher, dayOfWeek, title);
        model.addAttribute("page", page);
        model.addAttribute("pagination", Pagination.computePagination(page));
        model.addAttribute("url", "/main");
        model.addAttribute("urlParam", paramsUrl);
        model.addAttribute("teams", teams);
        model.addAttribute("teachers", teachers);
        model.addAttribute("titles", titles);
        model.addAttribute("team", team);
        model.addAttribute("teacher", teacher);
        model.addAttribute("dayOfWeek", dayOfWeek);

        return "main";
    }

    @GetMapping("/error")
    public String mainError(Model model) {
        return "error";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/{id}/remove-schedule")
    public String blogScheduleDelete(@AuthenticationPrincipal User currentUser, @PathVariable(value = "id") Long id, Model model) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule.getAuthor().getUsername().equals(currentUser.getUsername())) {
            scheduleService.delete(schedule);
        }
        model.addAttribute("messageType","success");
        model.addAttribute("message","Расписание успешно удалено!");
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/remove-schedules-by-team")
    public String blogScheduleDeleteByTeam(@AuthenticationPrincipal User currentUser, @RequestParam(name = "group") String team, Model model) {
        List<Schedule> scheduleList = scheduleService.findByTeam(team);
        for (Schedule schedule: scheduleList) {
            if (schedule.getAuthor().getUsername().equals(currentUser.getUsername())) {
                scheduleService.delete(schedule);
            }
        }
        model.addAttribute("messageType","success");
        model.addAttribute("message","Расписание группы "+team+" успешно удалено!");
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/remove-schedules-by-author")
    public String blogScheduleDeleteByAuthor(@AuthenticationPrincipal User currentUser, Model model) {
        List<Schedule> scheduleList = scheduleService.findAllByAuthor(currentUser);
        for (Schedule schedule: scheduleList) {
            if (schedule.getAuthor().getUsername().equals(currentUser.getUsername())) {
                scheduleService.delete(schedule);
            }
        }
        model.addAttribute("messageType","success");
        model.addAttribute("message","Расписание добавленное "+currentUser.getUsername()+" успешно удалено!");
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/upload")
    public String uploadScheduleForm(Model model) {
        return "upload-files";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/upload")
    public String uploadSchedule(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file,
                                 Model model) throws IOException, ParseException {
        if (!file.isEmpty() && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            File errorsDir = new File(errorsPath);
            File warningDir = new File(warningsPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            if (!errorsDir.exists()) {
                errorsDir.mkdir();
            }
            if (!warningDir.exists()) {
                warningDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultUploadFilename = uuidFile + "_" + file.getOriginalFilename();
            String resultErrorFilename = uuidFile + "_error_file.txt";
            String resultWarningFilename = uuidFile + "_warning_file.txt";
            String uploadFilePathname = uploadPath + "/" + resultUploadFilename;
            String fileWithErrorsInfoPathname = errorsPath + "/" + resultErrorFilename;
            String fileWithWarningsInfoPathname = warningsPath + "/" + resultWarningFilename;

            file.transferTo(new File(uploadFilePathname));
            List<Schedule> scheduleListFromDb = scheduleService.findAllByAuthor(user);
            List<Schedule> scheduleListForAddIntoDb = new ArrayList<>();
            Set<String> someWarnings = new HashSet<>();
            Set<String> someErrors = ExcelWorker.readExcelFile(scheduleListFromDb, scheduleListForAddIntoDb, user, uploadFilePathname, fileWithErrorsInfoPathname, fileWithWarningsInfoPathname, someWarnings);
            if (someErrors.size() == 0) {
                for (Schedule schedule : scheduleListForAddIntoDb) {
                    schedule.setFilenameWithErrors(resultErrorFilename);
                    schedule.setFilenameWithExcel(resultUploadFilename);
                    Schedule saveSchedule = scheduleService.addSchedule(schedule);
                    for (Couple couple : schedule.getCouples()) {
                        couple.setSchedule(saveSchedule);
                        coupleService.addCouple(couple);
                    }
                }
                model.addAttribute("messageType", "success");
                model.addAttribute("message", "Расписание успешно добавлено!");
            } else {
                model.addAttribute("messageType", "danger");
                model.addAttribute("message", "Были найдены ошибки!");
                model.addAttribute("resultUploadFilename", resultUploadFilename);
                model.addAttribute("resultErrorFilename", resultErrorFilename);
            }
            if (!someWarnings.isEmpty()) {
                model.addAttribute("messageWarning", "warning");
                model.addAttribute("messageWarningInfo", "Были найдены места на которые стоит обратить внимание!");
                model.addAttribute("resultUploadFilename2", resultUploadFilename);
                model.addAttribute("resultWarningFilename", resultWarningFilename);
            }
        }

        return "upload-files";
    }
}
