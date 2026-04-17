package org.nowstart.zunyang.partypanic.domain.story;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

import java.util.List;

public record StoryChapter(
        ActivityId activityId,
        String title,
        String subtitle,
        String backgroundPath,
        String returnNotice,
        String completionNotice,
        List<String> pages
) {
}
