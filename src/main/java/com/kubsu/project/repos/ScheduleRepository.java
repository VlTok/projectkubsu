package com.kubsu.project.repos;

import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    List<Schedule> findAllByAuthor(User author);
    Page<Schedule> findAll(Pageable pageable);

    Page<Schedule> findByTeam(String team, Pageable pageable);
}
