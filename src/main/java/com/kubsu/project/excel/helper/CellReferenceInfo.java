package com.kubsu.project.excel.helper;

import lombok.*;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CellReferenceInfo {

    private CellReference cellTitleReference;
    private CellReference cellTeacherReference;
    private CellReference cellTypeReference;
    private CellReference cellAudienceReference;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellReferenceInfo that = (CellReferenceInfo) o;
        return Objects.equals(cellTitleReference, that.cellTitleReference) && Objects.equals(cellTeacherReference, that.cellTeacherReference) && Objects.equals(cellTypeReference, that.cellTypeReference) && Objects.equals(cellAudienceReference, that.cellAudienceReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellTitleReference, cellTeacherReference, cellTypeReference, cellAudienceReference);
    }

    @Override
    public String toString() {
        return "CellReferenceInfo{" +
                "cellTitleReference='" + cellTitleReference + '\'' +
                ", cellTeacherReference='" + cellTeacherReference + '\'' +
                ", cellTypeReference='" + cellTypeReference + '\'' +
                ", cellAudienceReference='" + cellAudienceReference + '\'' +
                '}';
    }
}
