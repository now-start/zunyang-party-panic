package org.nowstart.zunyang.partypanic.application.dto.result;

public record MessageNoteView(
    String id,
    String label,
    String excerpt,
    int x,
    int y,
    boolean required,
    boolean selected,
    boolean active
) {
}
