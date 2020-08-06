package com.kubsu.project.controllers;

import com.kubsu.project.domain.User;
import com.kubsu.project.domain.Post;
import com.kubsu.project.repos.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class MainController {

    private final PostRepository postRepository;

    public MainController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/main")
    public String mainTimeTable(@RequestParam(required = false , defaultValue = "") String team,@RequestParam(required = false , defaultValue = "") String subgroup,
                                Model model, @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable){
        Page<Post> page;
        if ((team!=null && !team.isEmpty())&&(subgroup!=null && !subgroup.isEmpty())){
            page = postRepository.findByTeamAndSubgroup(team,subgroup,pageable);
        } else {
            page = postRepository.findAll(pageable);
        }
        model.addAttribute("page",page);
        model.addAttribute("team",team);
        model.addAttribute("subgroup",subgroup);

        return "main";
    }
    @GetMapping("/error")
    public String mainError(Model model){
        return "error";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/add")
    public String mainAdd(Model model){
        return "main-add";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/add")
    public String mainPostAdd(@AuthenticationPrincipal User user, Post post, Model model) {
        post.setAuthor(user);

        postRepository.save(post);

        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/{id}/edit")
    public String mainEdit(@PathVariable(value = "id") Long id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/main";
        }
        Optional<Post> post= postRepository.findById(id);
        ArrayList<Post> res= new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "main-edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/{id}/edit")
    public String mainPostUpdate(@AuthenticationPrincipal User currentUser,
                                 @PathVariable(value="id") Long id, @RequestParam String team,
                                 @RequestParam String subgroup, @RequestParam String day_of_week,
                                 @RequestParam String parity, @RequestParam String couple1,
                                 @RequestParam String couple2, @RequestParam String couple3,
                                 @RequestParam String couple4, @RequestParam String couple5,
                                 @RequestParam String couple6, @RequestParam String couple7,
                                 Model model){
        Post post = postRepository.findById(id).orElseThrow();
        if (post.getAuthor().getUsername().equals(currentUser.getUsername())) {
            post.setTeam(team);
            post.setSubgroup(subgroup);
            post.setDayOfWeek(day_of_week);
            post.setParity(parity);
            post.setCouple1(couple1);
            post.setCouple2(couple2);
            post.setCouple3(couple3);
            post.setCouple4(couple4);
            post.setCouple5(couple5);
            post.setCouple6(couple6);
            post.setCouple7(couple7);

            postRepository.save(post);
        }
        return "redirect:/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/{id}/remove")
    public String blogPostDelete(@AuthenticationPrincipal User currentUser, @PathVariable(value="id") Long id, Model model){
        Post post = postRepository.findById(id).orElseThrow();
        if (post.getAuthor().getUsername().equals(currentUser.getUsername())) {
            postRepository.delete(post);
        }
        return "redirect:/main";
    }
}
