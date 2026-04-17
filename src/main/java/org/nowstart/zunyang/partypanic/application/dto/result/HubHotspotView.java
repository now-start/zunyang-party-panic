package org.nowstart.zunyang.partypanic.application.dto.result;

public record HubHotspotView(
    String id,
    String label,
    int x,
    int y,
    boolean unlocked
) {
}
