package com.kubsu.project.repos;

import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    Page<Schedule> findAll(Pageable pageable);

    Page<Schedule> findByTeamAndSubgroup(String team, String subgroup, Pageable pageable);

    Page<Schedule> deleteByAuthor(User author, Pageable pageable);
}
