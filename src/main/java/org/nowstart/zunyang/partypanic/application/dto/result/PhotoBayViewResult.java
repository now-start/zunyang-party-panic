package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record PhotoBayViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeFocusId,
    int lockedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<PhotoFocusView> focuses
) {
}
