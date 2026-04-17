package org.nowstart.zunyang.partypanic.application.hub;

import java.util.List;
import org.nowstart.zunyang.partypanic.application.dto.result.HubHotspotView;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

final class HubViewMapper {

    private HubViewMapper() {
    }

    static HubViewResult toView(HubState state, RunProgress runProgress) {
        List<HubHotspotView> hotspots = state.layout().hotspots().stream()
            .map(hotspot -> new HubHotspotView(
                hotspot.chapterId().name(),
                hotspot.label(),
                hotspot.position().x(),
                hotspot.position().y(),
                runProgress.isUnlocked(hotspot.chapterId())
            ))
            .toList();

        return new HubViewResult(
            state.layout().width(),
            state.layout().height(),
            state.actor().position().x(),
            state.actor().position().y(),
            state.actor().facing().name(),
            runProgress.phase().name(),
            runProgress.completedChapters().size(),
            runProgress.placeholderArtEnabled(),
            runProgress.endingGrade() == null ? null : runProgress.endingGrade().title(),
            state.activeHotspot() == null ? null : state.activeHotspot().name(),
            state.currentMessage(),
            hotspots
        );
    }
}
