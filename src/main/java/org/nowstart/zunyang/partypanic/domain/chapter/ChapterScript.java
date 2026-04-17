package org.nowstart.zunyang.partypanic.domain.chapter;

import java.util.List;
import java.util.Objects;

public record ChapterScript(
    ChapterId chapterId,
    String title,
    String subtitle,
    String visualToken,
    ChapterActivityType activityType,
    List<DialoguePage> pages
) {

    public ChapterScript {
        Objects.requireNonNull(chapterId, "chapterId must not be null");
        if (pages == null || pages.isEmpty()) {
            throw new IllegalArgumentException("Chapter script must contain at least one page");
        }
        if (activityType == null) {
            throw new IllegalArgumentException("Chapter activity type must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Chapter title must not be blank");
        }
        if (subtitle == null || subtitle.isBlank()) {
            throw new IllegalArgumentException("Chapter subtitle must not be blank");
        }
        if (visualToken == null || visualToken.isBlank()) {
            throw new IllegalArgumentException("Chapter visual token must not be blank");
        }
    }

    public boolean hasActivity() {
        return activityType != ChapterActivityType.NONE;
    }
}
