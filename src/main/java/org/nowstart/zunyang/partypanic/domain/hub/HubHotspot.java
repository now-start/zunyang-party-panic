package org.nowstart.zunyang.partypanic.domain.hub;

import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record HubHotspot(
    ChapterId chapterId,
    String label,
    Position position,
    String interactionText,
    String lockedText
) {
}
