package com.kubsu.project.controllers;

import com.kubsu.project.domain.Post;
import com.kubsu.project.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/main")
    public String mainTimeTable(@RequestParam(required = false , defaultValue = "") String group,@RequestParam(required = false , defaultValue = "") String subgroup, Model model, @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable){
        Page<Post> page;
        if ((group!=null && !group.isEmpty())&&(subgroup!=null && !subgroup.isEmpty())){
            page = postRepository.findByTeamAndSubgroup(group,subgroup,pageable);
        }else {
            page = postRepository.findAll(pageable);
        }
        model.addAttribute("page",page);
        model.addAttribute("group",group);
        model.addAttribute("subgroup",subgroup);

        return "main";
    }
    @GetMapping("/main/add")
    public String blogAdd(Model model){
        return "main-add";
    }

    @PostMapping("/main/add")
    public String blogPostAdd(Post post, Model model) {


        postRepository.save(post);

        return "redirect:/main";
    }
}
