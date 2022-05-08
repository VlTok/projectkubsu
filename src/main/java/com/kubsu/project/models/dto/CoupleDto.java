package com.kubsu.project.models.dto;

import org.jetbrains.annotations.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CoupleDto {

    @NotNull
    private String title;

    private String type;

    private String audience;

    @NotNull
    private Date timeCouple;

    private String teacher;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoupleDto coupleDto = (CoupleDto) o;
        return title.equals(coupleDto.title) && Objects.equals(type, coupleDto.type) && Objects.equals(audience, coupleDto.audience) && timeCouple.equals(coupleDto.timeCouple) && Objects.equals(teacher, coupleDto.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, type, audience, timeCouple, teacher);
    }

    @Override
    public String toString() {
        return "CoupleDto{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", audience='" + audience + '\'' +
                ", timeCouple=" + timeCouple +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
