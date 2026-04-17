package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record HandoverCorridorViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeClueId,
    int collectedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<HandoverClueView> clues
) {
}
