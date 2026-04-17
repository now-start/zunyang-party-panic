package org.nowstart.zunyang.partypanic.application.dto.result;

public record FinaleCheckpointView(
    String id,
    String label,
    int x,
    int y,
    boolean required,
    boolean checked,
    boolean active
) {
}
