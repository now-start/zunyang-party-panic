package org.nowstart.zunyang.partypanic.application.signal;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalControlView;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;

final class SignalConsoleViewMapper {

    private SignalConsoleViewMapper() {
    }

    static SignalConsoleViewResult toView(SignalConsoleState state) {
        return new SignalConsoleViewResult(
            "첫 신호 맞추기",
            "arrow: 이동  |  z: 조사  |  x/c: 값 조정  |  enter: 완료",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeControl() == null ? null : state.activeControl().name(),
            state.statusMessage(),
            state.stabilized(),
            Arrays.stream(SignalControlId.values())
                .map(control -> {
                    Position position = state.layout().positionOf(control);
                    return new SignalControlView(
                        control.name(),
                        control.label(),
                        position.x(),
                        position.y(),
                        state.levelOf(control),
                        control.targetLevel(),
                        control.describeLevel(state.levelOf(control)),
                        control.describeLevel(control.targetLevel()),
                        state.activeControl() == control,
                        state.aligned(control)
                    );
                })
                .toList()
        );
    }
}
