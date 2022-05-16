package com.kubsu.project.service;

import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.repos.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Page<Schedule> findAll(Pageable pageable, String team) {
        Page<Schedule> page;
        if (team != null && !team.isEmpty()) {
            page = scheduleRepository.findByTeam(team, pageable);
        } else {
            page = scheduleRepository.findAll(pageable);
        }
        return page;
    }

    public List<Schedule> findAllByAuthor(User author){
        return scheduleRepository.findAllByAuthor(author);
    }

    public Schedule findById(Long id){
        return scheduleRepository.findById(id).orElseThrow();
    }

    public void delete(Schedule schedule){
        scheduleRepository.delete(schedule);
    }
}
