package com.kubsu.project.models;

import lombok.*;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "k_schedule")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "s_id")
    private Long id;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Couple> couples;

    @Column(name = "s_day_of_week")
    @NotNull
    private String dayOfWeek;

    @Column(name = "s_team")
    @NotNull
    private String team;

    @Column(name = "s_parity")
    @NotNull
    private String parity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "s_author_id")
    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(id, schedule.id) && Objects.equals(couples, schedule.couples) && dayOfWeek.equals(schedule.dayOfWeek) && team.equals(schedule.team) && parity.equals(schedule.parity) && Objects.equals(author, schedule.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, couples, dayOfWeek, team, parity, author);
    }
}
