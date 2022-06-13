package com.kubsu.project.service;

import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.repos.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Page<Schedule> findAll(String team, String teacher, String dayOfWeek, String title, Pageable pageable) {
        Page<Schedule> schedules;
        boolean nonNullTeam = nonNull(team) && !team.isEmpty();
        boolean nonNullTeacher = nonNull(teacher) && !teacher.isEmpty();
        boolean nonNullDayOfWeek = nonNull(dayOfWeek) && !dayOfWeek.isEmpty();
        boolean nonNullTitle = nonNull(title) && !title.isEmpty();
        if (nonNullTitle && nonNullTeacher && nonNullDayOfWeek && nonNullTeam) {
            schedules = scheduleRepository.findAllByTeamAndTeacherAndDayOfWeekAndTitle(team, teacher, dayOfWeek, title, pageable);
        } else if (nonNullTeam && nonNullTitle && nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByTeamAndTitleAndDayOfWeek(team, title, dayOfWeek, pageable);
        } else if (nonNullTeam && nonNullTeacher && nonNullTitle) {
            schedules = scheduleRepository.findAllByTeamAndTeacherAndTitle(team, teacher, title, pageable);
        } else if (nonNullTitle && nonNullTeacher && nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByTitleAndTeacherAndDayOfWeek(title, teacher, dayOfWeek, pageable);
        } else if (nonNullTeam && nonNullTeacher && nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByTeamAndTeacherAndDayOfWeek(team, teacher, dayOfWeek, pageable);
        } else if (nonNullTitle && nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByDayOfWeekAndTitle(dayOfWeek, title, pageable);
        } else if (nonNullTitle && nonNullTeacher) {
            schedules = scheduleRepository.findAllByTeacherAndTitle(teacher, title, pageable);
        } else if (nonNullTitle && nonNullTeam) {
            schedules = scheduleRepository.findAllByTeamAndTitle(team, title, pageable);
        } else if (nonNullTeam && nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByTeamAndDayOfWeek(team, dayOfWeek, pageable);
        } else if (nonNullDayOfWeek && nonNullTeacher) {
            schedules = scheduleRepository.findAllByDayOfWeekAndTeacher(dayOfWeek, teacher, pageable);
        } else if (nonNullTeam && nonNullTeacher) {
            schedules = scheduleRepository.findAllByTeamAndTeacher(team, teacher, pageable);
        } else if (nonNullDayOfWeek) {
            schedules = scheduleRepository.findAllByDayOfWeek(dayOfWeek, pageable);
        } else if (nonNullTeacher) {
            schedules = scheduleRepository.findAllByTeacher(teacher, pageable);
        } else if (nonNullTeam) {
            schedules = scheduleRepository.findByTeam(team, pageable);
        } else if (nonNullTitle) {
            schedules = scheduleRepository.findAllByTitle(title, pageable);
        } else {
            schedules = scheduleRepository.findAll(pageable);
        }
        return schedules;
    }

    public Set<String> findAllTeam(){
        List<String> groups = scheduleRepository.findAllTeam();
        return new HashSet<>(groups);
    }

    public List<Schedule> findAllByAuthor(User author){
        return scheduleRepository.findAllByAuthor(author);
    }

    public Schedule findById(Long id){
        return scheduleRepository.findById(id).orElseThrow();
    }

    public List<Schedule> findByTeam(String team) {
        return scheduleRepository.findByTeam(team.trim());
    }

    public Schedule addSchedule(Schedule schedule){
        return scheduleRepository.save(schedule);
    }

    public boolean existsSchedule(Long id){
        return scheduleRepository.existsById(id);
    }

    public void delete(Schedule schedule){
        scheduleRepository.delete(schedule);
    }

}
