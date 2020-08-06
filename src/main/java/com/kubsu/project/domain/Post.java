package com.kubsu.project.domain;

import javax.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String couple1;
    private String couple2;
    private String couple3;
    private String couple4;
    private String couple5;
    private String couple6;
    private String couple7;
    private String dayOfWeek;
    private String team;
    private String parity;
    private String subgroup;

    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    public Post() {
    }

    public Post(String team, String subgroup, String dayOfWeek, String parity, String couple1,
                String couple2, String couple3, String couple4, String couple5, String couple6,
                String couple7, User user){
        this.team = team;
        this.subgroup = subgroup;
        this.dayOfWeek = dayOfWeek;
        this.parity=parity;
        this.couple1=couple1;
        this.couple2=couple2;
        this.couple3=couple3;
        this.couple4=couple4;
        this.couple5=couple5;
        this.couple6=couple6;
        this.couple7=couple7;
        this.author= user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String my_group) {
        this.team = my_group;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String my_subgroup) {
        this.subgroup = my_subgroup;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String day_of_week) {
        this.dayOfWeek = day_of_week;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getCouple1() {
        return couple1;
    }

    public void setCouple1(String couple1) {
        this.couple1 = couple1;
    }

    public String getCouple2() {
        return couple2;
    }

    public void setCouple2(String couple2) {
        this.couple2 = couple2;
    }

    public String getCouple3() {
        return couple3;
    }

    public void setCouple3(String couple3) {
        this.couple3 = couple3;
    }

    public String getCouple4() {
        return couple4;
    }

    public void setCouple4(String couple4) {
        this.couple4 = couple4;
    }

    public String getCouple5() {
        return couple5;
    }

    public void setCouple5(String couple5) {
        this.couple5 = couple5;
    }

    public String getCouple6() {
        return couple6;
    }

    public void setCouple6(String couple6) {
        this.couple6 = couple6;
    }

    public String getCouple7() {
        return couple7;
    }

    public void setCouple7(String couple7) {
        this.couple7 = couple7;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
