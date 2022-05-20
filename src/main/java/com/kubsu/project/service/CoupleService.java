package com.kubsu.project.service;

import com.kubsu.project.models.Couple;
import com.kubsu.project.repos.CoupleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CoupleService {

    private final CoupleRepository coupleRepository;

    public CoupleService(CoupleRepository coupleRepository) {
        this.coupleRepository = coupleRepository;
    }

    public void addCouple(Couple couple){
        coupleRepository.save(couple);
    }

    public Set<String> findAllTeachers(){
        List<String> teachers = coupleRepository.findAllTeachers();
        return new HashSet<>(teachers);
    }
}
