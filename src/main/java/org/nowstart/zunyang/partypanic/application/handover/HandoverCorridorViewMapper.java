package org.nowstart.zunyang.partypanic.application.handover;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverClueView;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverClueId;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

final class HandoverCorridorViewMapper {

    private HandoverCorridorViewMapper() {
    }

    static HandoverCorridorViewResult toView(HandoverCorridorState state) {
        long collectedRequired = state.collectedClues().stream()
            .filter(HandoverClueId::required)
            .count();
        long requiredCount = Arrays.stream(HandoverClueId.values())
            .filter(HandoverClueId::required)
            .count();

        return new HandoverCorridorViewResult(
            "기록 복도",
            "arrow: 이동  |  z: 기록 확인  |  enter: 복귀",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeClue() == null ? null : state.activeClue().name(),
            (int) collectedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(HandoverClueId.values())
                .map(clue -> {
                    Position position = state.layout().positionOf(clue);
                    return new HandoverClueView(
                        clue.name(),
                        clue.label(),
                        position.x(),
                        position.y(),
                        clue.required(),
                        state.collected(clue),
                        state.activeClue() == clue
                    );
                })
                .toList()
        );
    }
}
