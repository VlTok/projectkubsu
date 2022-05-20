package com.kubsu.project.repos;

import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    List<Schedule> findAllByAuthor(User author);
    @Query("select schedule from Schedule as schedule left join Couple as couple on schedule = couple.schedule where couple.teacher = :teacher and schedule.team = :team and schedule.dayOfWeek = :dayOfWeek")
    Page<Schedule> findAllByTeamAndTeacherAndDayOfWeek(@Param("team") String team, @Param("teacher") String teacher,
                                                       @Param("dayOfWeek") String dayOfWeek, Pageable pageable);
    @Query("select schedule from Schedule as schedule left join Couple as couple on schedule = couple.schedule where couple.teacher = :teacher and schedule.team = :team")
    Page<Schedule> findAllByTeamAndTeacher(@Param("team") String team, @Param("teacher") String teacher, Pageable pageable);
    @Query("select schedule from Schedule as schedule where schedule.team = :team and schedule.dayOfWeek = :dayOfWeek")
    Page<Schedule> findAllByTeamAndDayOfWeek(@Param("team") String team, @Param("dayOfWeek") String dayOfWeek, Pageable pageable);
    @Query("select schedule from Schedule as schedule left join Couple as couple on schedule = couple.schedule where couple.teacher = :teacher and schedule.dayOfWeek = :dayOfWeek")
    Page<Schedule> findAllByDayOfWeekAndTeacher(@Param("dayOfWeek") String dayOfWeek, @Param("teacher") String teacher, Pageable pageable);
    @Query("select schedule from Schedule as schedule left join Couple as couple on schedule = couple.schedule where couple.teacher = :teacher")
    Page<Schedule> findAllByTeacher(@Param("teacher") String teacher, Pageable pageable);
    Page<Schedule> findAllByDayOfWeek(String dayOfWeek, Pageable pageable);
    Page<Schedule> findByTeam(String team, Pageable pageable);
    Page<Schedule> findAll(Pageable pageable);
    @Query("select schedule.team from Schedule as schedule")
    List<String> findAllTeam();
}
