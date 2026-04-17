package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record MessageWallViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeNoteId,
    int selectedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<MessageNoteView> notes
) {
}
