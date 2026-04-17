package org.nowstart.zunyang.partypanic.application.photo;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoFocusView;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoFocusId;

final class PhotoBayViewMapper {

    private PhotoBayViewMapper() {
    }

    static PhotoBayViewResult toView(PhotoBayState state) {
        long lockedRequired = state.lockedFocuses().stream()
            .filter(PhotoFocusId::required)
            .count();
        long requiredCount = Arrays.stream(PhotoFocusId.values())
            .filter(PhotoFocusId::required)
            .count();

        return new PhotoBayViewResult(
            "포토 베이",
            "arrow: 이동  |  z: 프레임 확인  |  enter: 복귀",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeFocus() == null ? null : state.activeFocus().name(),
            (int) lockedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(PhotoFocusId.values())
                .map(focus -> {
                    Position position = state.layout().positionOf(focus);
                    return new PhotoFocusView(
                        focus.name(),
                        focus.label(),
                        position.x(),
                        position.y(),
                        focus.required(),
                        state.locked(focus),
                        state.activeFocus() == focus
                    );
                })
                .toList()
        );
    }
}
