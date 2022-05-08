package com.kubsu.project.models;

import lombok.*;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "k_couple")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class Couple {

    @Id
    @Column(name = "c_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "c_title")
    private String title;

    @Column(name = "c_type")
    private String type;

    @Column(name = "c_audience")
    private String audience;

    @Column(name = "c_time")
    @NotNull
    private Date timeCouple;

    @Column(name = "c_teacher")
    private String teacher;

    @ManyToOne
    @JoinColumn(name = "c_schedule_id")
    private Schedule schedule;
}
