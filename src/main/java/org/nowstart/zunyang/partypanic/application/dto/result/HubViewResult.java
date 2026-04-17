package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record HubViewResult(
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String sessionPhase,
    int completedChapterCount,
    boolean placeholderArtEnabled,
    String endingGradeTitle,
    String activeHotspotId,
    String currentMessage,
    List<HubHotspotView> hotspots
) {
}
