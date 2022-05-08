package com.kubsu.project.models;

import lombok.*;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "schedule")
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
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", couples=" + couples +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", team='" + team + '\'' +
                ", parity='" + parity + '\'' +
                ", author=" + author +
                '}';
    }
}
