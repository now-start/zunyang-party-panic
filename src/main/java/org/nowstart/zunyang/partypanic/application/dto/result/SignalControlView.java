package org.nowstart.zunyang.partypanic.application.dto.result;

public record SignalControlView(
    String id,
    String label,
    int x,
    int y,
    int currentLevel,
    int targetLevel,
    String currentDescriptor,
    String targetDescriptor,
    boolean active,
    boolean aligned
) {
}
