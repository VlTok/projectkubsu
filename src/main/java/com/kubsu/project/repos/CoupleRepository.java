package com.kubsu.project.repos;

import com.kubsu.project.models.Couple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoupleRepository extends CrudRepository<Couple, Long> {
    List<Couple> findAllByTeacher(String teacher);

    @Query("select couple.teacher from Couple as couple")
    List<String> findAllTeachers();

    @Query("select couple.title from Couple as couple")
    List<String> findAllTitles();
}
