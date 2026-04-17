package org.nowstart.zunyang.partypanic.application.dto.result;

public record CenterpiecePlacementView(
    String id,
    String label,
    int x,
    int y,
    boolean required,
    boolean placed,
    boolean active
) {
}
