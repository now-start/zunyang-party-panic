package org.nowstart.zunyang.partypanic.application.props;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsItemView;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;
import org.nowstart.zunyang.partypanic.domain.props.PropsItemId;

final class PropsArchiveViewMapper {

    private PropsArchiveViewMapper() {
    }

    static PropsArchiveViewResult toView(PropsArchiveState state) {
        long collectedRequired = state.collectedItems().stream()
            .filter(PropsItemId::required)
            .count();
        long requiredCount = Arrays.stream(PropsItemId.values())
            .filter(PropsItemId::required)
            .count();

        return new PropsArchiveViewResult(
            "소품 아카이브",
            "arrow: 이동  |  z: 조사/회수  |  enter: 복귀",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeItem() == null ? null : state.activeItem().name(),
            (int) collectedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(PropsItemId.values())
                .map(item -> {
                    Position position = state.layout().positionOf(item);
                    return new PropsItemView(
                        item.name(),
                        item.label(),
                        position.x(),
                        position.y(),
                        item.required(),
                        state.collected(item),
                        state.activeItem() == item
                    );
                })
                .toList()
        );
    }
}
