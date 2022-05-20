package com.kubsu.project.service;

import com.kubsu.project.models.Couple;
import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.repos.CoupleRepository;
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

    public Page<Schedule> findAll(String team, String teacher, String dayOfWeek, Pageable pageable) {
        Page<Schedule> schedules;
        if (nonNull(team)&&!team.isEmpty()&&nonNull(teacher)&&!teacher.isEmpty()&&nonNull(dayOfWeek)&&!dayOfWeek.isEmpty()){
            schedules = scheduleRepository.findAllByTeamAndTeacherAndDayOfWeek(team,teacher,dayOfWeek, pageable);
        }
        else if (nonNull(team)&&!team.isEmpty()&&nonNull(dayOfWeek)&&!dayOfWeek.isEmpty()){
            schedules = scheduleRepository.findAllByTeamAndDayOfWeek(team,dayOfWeek, pageable);
        }
        else if (nonNull(dayOfWeek)&&!dayOfWeek.isEmpty()&&nonNull(teacher)&&!teacher.isEmpty()){
            schedules = scheduleRepository.findAllByDayOfWeekAndTeacher(dayOfWeek,teacher, pageable);
        }
        else if (nonNull(team)&&!team.isEmpty()&&nonNull(teacher)&&!teacher.isEmpty()){
            schedules = scheduleRepository.findAllByTeamAndTeacher(team,teacher, pageable);
        }
        else if (nonNull(dayOfWeek)&&!dayOfWeek.isEmpty()){
            schedules = scheduleRepository.findAllByDayOfWeek(dayOfWeek, pageable);
        }
        else if (nonNull(teacher)&&!teacher.isEmpty()){
            schedules = scheduleRepository.findAllByTeacher(teacher, pageable);
        }
        else if (nonNull(team)&&!team.isEmpty()) {
            schedules = scheduleRepository.findByTeam(team, pageable);
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

    public Schedule addSchedule(Schedule schedule){
        return scheduleRepository.save(schedule);
    }

    public void delete(Schedule schedule){
        scheduleRepository.delete(schedule);
    }
}
