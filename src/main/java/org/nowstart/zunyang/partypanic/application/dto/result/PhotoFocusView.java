package org.nowstart.zunyang.partypanic.application.dto.result;

public record PhotoFocusView(
    String id,
    String label,
    int x,
    int y,
    boolean required,
    boolean locked,
    boolean active
) {
}
