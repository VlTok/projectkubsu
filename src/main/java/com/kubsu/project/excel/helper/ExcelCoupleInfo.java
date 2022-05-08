package com.kubsu.project.excel.helper;

import com.kubsu.project.models.dto.CoupleDto;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExcelCoupleInfo {

    private String title;

    private String type;

    private String audience;

    private Date timeCouple;

    private String teacher;

    private String dayOfWeek;

    private String team;

    private String parity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcelCoupleInfo that = (ExcelCoupleInfo) o;
        return Objects.equals(title, that.title) && Objects.equals(type, that.type) && Objects.equals(audience, that.audience) && Objects.equals(timeCouple, that.timeCouple) && Objects.equals(teacher, that.teacher) && Objects.equals(dayOfWeek, that.dayOfWeek) && Objects.equals(team, that.team) && Objects.equals(parity, that.parity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, type, audience, timeCouple, teacher, dayOfWeek, team, parity);
    }

    @Override
    public String toString() {
        return "ExcelCoupleInfo{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", audience='" + audience + '\'' +
                ", timeCouple=" + timeCouple +
                ", teacher='" + teacher + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", team='" + team + '\'' +
                ", parity='" + parity + '\'' +
                '}';
    }
}
