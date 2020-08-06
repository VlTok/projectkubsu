package com.kubsu.project.repos;

import com.kubsu.project.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTeamAndSubgroup(String team, String subgroup, Pageable pageable);
}
