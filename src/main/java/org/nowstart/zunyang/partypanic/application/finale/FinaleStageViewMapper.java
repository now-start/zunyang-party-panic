package org.nowstart.zunyang.partypanic.application.finale;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleCheckpointView;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleCheckpointId;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

final class FinaleStageViewMapper {

    private FinaleStageViewMapper() {
    }

    static FinaleStageViewResult toView(FinaleStageState state) {
        long checkedRequired = state.checkedCheckpoints().stream()
            .filter(FinaleCheckpointId::required)
            .count();
        long requiredCount = Arrays.stream(FinaleCheckpointId.values())
            .filter(FinaleCheckpointId::required)
            .count();

        return new FinaleStageViewResult(
            "메인 스테이지",
            "arrow: 이동  |  z: 최종 점검  |  enter: 개장",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeCheckpoint() == null ? null : state.activeCheckpoint().name(),
            (int) checkedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(FinaleCheckpointId.values())
                .map(checkpoint -> {
                    Position position = state.layout().positionOf(checkpoint);
                    return new FinaleCheckpointView(
                        checkpoint.name(),
                        checkpoint.label(),
                        position.x(),
                        position.y(),
                        checkpoint.required(),
                        state.checked(checkpoint),
                        state.activeCheckpoint() == checkpoint
                    );
                })
                .toList()
        );
    }
}
