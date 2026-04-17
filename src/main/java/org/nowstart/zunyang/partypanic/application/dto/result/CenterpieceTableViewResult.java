package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record CenterpieceTableViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activePlacementId,
    int placedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<CenterpiecePlacementView> placements
) {
}
