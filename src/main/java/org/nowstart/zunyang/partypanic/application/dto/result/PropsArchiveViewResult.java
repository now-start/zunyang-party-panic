package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record PropsArchiveViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeItemId,
    int collectedRequiredCount,
    int requiredCount,
    boolean readyToReturn,
    String statusMessage,
    List<PropsItemView> items
) {
}
