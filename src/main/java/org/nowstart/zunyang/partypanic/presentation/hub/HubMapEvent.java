package org.nowstart.zunyang.partypanic.presentation.hub;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

import java.util.List;

public record HubMapEvent(
        ActivityId id,
        String title,
        String lockedNotice,
        List<String> interactionLines,
        HubEventVisual visual,
        int tileX,
        int tileY
) {
}
