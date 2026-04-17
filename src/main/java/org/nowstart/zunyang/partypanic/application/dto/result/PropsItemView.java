package org.nowstart.zunyang.partypanic.application.dto.result;

public record PropsItemView(
    String id,
    String label,
    int x,
    int y,
    boolean required,
    boolean collected,
    boolean active
) {
}
