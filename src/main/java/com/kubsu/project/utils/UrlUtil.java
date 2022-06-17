package com.kubsu.project.utils;

import static java.util.Objects.nonNull;

public class UrlUtil {

    public static String createRightUrl(String team, String teacher, String dayOfWeek, String title) {
        StringBuilder url = new StringBuilder();

        if (nonNull(team)) {
            url.append("&team=").append(team);
        }
        if (nonNull(teacher)) {
            url.append("&teacher=").append(teacher);
        }
        if (nonNull(dayOfWeek)) {
            url.append("&dayOfWeek=").append(dayOfWeek);
        }
        if (nonNull(title)) {
            url.append("&title=").append(title);
        }

        return url.toString();
    }
}
