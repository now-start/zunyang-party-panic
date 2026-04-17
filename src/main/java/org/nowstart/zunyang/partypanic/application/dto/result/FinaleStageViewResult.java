package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record FinaleStageViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeCheckpointId,
    int checkedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<FinaleCheckpointView> checkpoints
) {
}
