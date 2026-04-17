package org.nowstart.zunyang.partypanic.application.centerpiece;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpiecePlacementView;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpiecePlacementId;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

final class CenterpieceTableViewMapper {

    private CenterpieceTableViewMapper() {
    }

    static CenterpieceTableViewResult toView(CenterpieceTableState state) {
        long placedRequired = state.placedItems().stream()
            .filter(CenterpiecePlacementId::required)
            .count();
        long requiredCount = Arrays.stream(CenterpiecePlacementId.values())
            .filter(CenterpiecePlacementId::required)
            .count();

        return new CenterpieceTableViewResult(
            "중앙 테이블",
            "arrow: 이동  |  z: 배치 확인  |  enter: 복귀",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activePlacement() == null ? null : state.activePlacement().name(),
            (int) placedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(CenterpiecePlacementId.values())
                .map(item -> {
                    Position position = state.layout().positionOf(item);
                    return new CenterpiecePlacementView(
                        item.name(),
                        item.label(),
                        position.x(),
                        position.y(),
                        item.required(),
                        state.placed(item),
                        state.activePlacement() == item
                    );
                })
                .toList()
        );
    }
}
