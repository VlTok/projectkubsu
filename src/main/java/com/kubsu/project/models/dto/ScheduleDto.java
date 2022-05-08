package com.kubsu.project.models.dto;

import com.kubsu.project.models.Couple;
import com.kubsu.project.models.User;
import org.jetbrains.annotations.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleDto {

    private List<CoupleDto> couples;

    @NotNull
    private String dayOfWeek;

    @NotNull
    private String team;

    @NotNull
    private String parity;

    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDto that = (ScheduleDto) o;
        return Objects.equals(couples, that.couples) && dayOfWeek.equals(that.dayOfWeek) && team.equals(that.team) && parity.equals(that.parity) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couples, dayOfWeek, team, parity, author);
    }

    @Override
    public String toString() {
        return "ScheduleDto{" +
                "couples=" + couples +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", team='" + team + '\'' +
                ", parity='" + parity + '\'' +
                ", author=" + author +
                '}';
    }
}
