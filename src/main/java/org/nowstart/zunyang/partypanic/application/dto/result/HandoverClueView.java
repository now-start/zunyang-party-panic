package org.nowstart.zunyang.partypanic.application.dto.result;

public record HandoverClueView(
    String id,
    String label,
    int x,
    int y,
    boolean required,
    boolean collected,
    boolean active
) {
}
