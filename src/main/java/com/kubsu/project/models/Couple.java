package com.kubsu.project.models;

import lombok.*;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "k_couple")
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couple couple = (Couple) o;
        return Objects.equals(id, couple.id) && title.equals(couple.title) && Objects.equals(type, couple.type) && Objects.equals(audience, couple.audience) && timeCouple.equals(couple.timeCouple) && Objects.equals(teacher, couple.teacher) && Objects.equals(schedule, couple.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type, audience, timeCouple, teacher, schedule);
    }
}
