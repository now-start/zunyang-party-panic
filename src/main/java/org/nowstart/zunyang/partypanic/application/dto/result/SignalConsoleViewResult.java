package org.nowstart.zunyang.partypanic.application.dto.result;

import java.util.List;

public record SignalConsoleViewResult(
    String title,
    String instructions,
    int width,
    int height,
    int actorX,
    int actorY,
    String facing,
    String activeControlId,
    String statusMessage,
    boolean stabilized,
    List<SignalControlView> controls
) {
}
