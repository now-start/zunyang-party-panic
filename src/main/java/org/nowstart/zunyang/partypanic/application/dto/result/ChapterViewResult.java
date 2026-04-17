package org.nowstart.zunyang.partypanic.application.dto.result;

public record ChapterViewResult(
    String chapterId,
    String title,
    String subtitle,
    String visualToken,
    String activityType,
    String speaker,
    String text,
    int pageNumber,
    int totalPages,
    boolean activityReady,
    boolean completed
) {
}
