package org.nowstart.zunyang.partypanic.screen.hub;

import java.util.List;

public record HubMapEvent(
        String id,
        String title,
        String lockedNotice,
        List<String> interactionLines,
        HubEventVisual visual,
        int tileX,
        int tileY,
        Runnable action
) {
}
