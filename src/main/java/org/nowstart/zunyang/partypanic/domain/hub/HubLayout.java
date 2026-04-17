package org.nowstart.zunyang.partypanic.domain.hub;

import java.util.List;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record HubLayout(
    int width,
    int height,
    Position startPosition,
    List<HubHotspot> hotspots
) {

    public boolean isInside(Position position) {
        return position.x() >= 0
            && position.y() >= 0
            && position.x() < width
            && position.y() < height;
    }

    public boolean isWalkable(Position position) {
        return isInside(position) && hotspotAt(position).isEmpty();
    }

    public Optional<HubHotspot> hotspotAt(Position position) {
        return hotspots.stream()
            .filter(hotspot -> hotspot.position().equals(position))
            .findFirst();
    }
}
