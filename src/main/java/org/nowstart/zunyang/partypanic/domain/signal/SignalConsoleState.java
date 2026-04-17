package org.nowstart.zunyang.partypanic.domain.signal;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record SignalConsoleState(
    GridActivityLayout<SignalControlId> layout,
    Position actorPosition,
    Direction facing,
    SignalControlId activeControl,
    Set<SignalControlId> inspectedControls,
    int micLevel,
    int lampLevel,
    int monitorLevel,
    int cueLevel,
    boolean stabilized,
    String statusMessage
) {

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 2;

    public SignalConsoleState {
        Objects.requireNonNull(layout, "layout must not be null");
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(inspectedControls, "inspectedControls must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<SignalControlId> normalized = EnumSet.noneOf(SignalControlId.class);
        normalized.addAll(inspectedControls);
        inspectedControls = Collections.unmodifiableSet(normalized);
    }

    public static SignalConsoleState initial(GridActivityLayout<SignalControlId> layout) {
        Position start = layout.actorStart();
        return refresh(
            layout,
            start,
            Direction.UP,
            null,
            Set.of(),
            0,
            2,
            0,
            2,
            previewMessage(layout, Direction.UP, start)
        );
    }

    public SignalConsoleState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolvedPosition = layout.isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            layout,
            resolvedPosition,
            direction,
            null,
            inspectedControls,
            micLevel,
            lampLevel,
            monitorLevel,
            cueLevel,
            previewMessage(layout, direction, resolvedPosition)
        );
    }

    public SignalConsoleState inspect() {
        SignalControlId control = facingControl();
        if (control == null) {
            return refresh(
                layout,
                actorPosition,
                facing,
                null,
                inspectedControls,
                micLevel,
                lampLevel,
                monitorLevel,
                cueLevel,
                "여긴 지금 점검할 장비가 없다."
            );
        }
        EnumSet<SignalControlId> nextInspected = EnumSet.noneOf(SignalControlId.class);
        nextInspected.addAll(inspectedControls);
        nextInspected.add(control);
        return refresh(
            layout,
            actorPosition,
            facing,
            control,
            nextInspected,
            micLevel,
            lampLevel,
            monitorLevel,
            cueLevel,
            inspectionMessage(control)
        );
    }

    public SignalConsoleState adjustActive(int delta) {
        SignalControlId control = activeControl != null ? activeControl : facingControl();
        if (control == null) {
            return refresh(
                layout,
                actorPosition,
                facing,
                null,
                inspectedControls,
                micLevel,
                lampLevel,
                monitorLevel,
                cueLevel,
                "먼저 조절할 장비 앞에 선다."
            );
        }

        return switch (control) {
            case MIC -> refresh(layout, actorPosition, facing, control, inspectedControls, clamp(micLevel + delta), lampLevel, monitorLevel, cueLevel, tuningMessage(control, clamp(micLevel + delta)));
            case LAMP -> refresh(layout, actorPosition, facing, control, inspectedControls, micLevel, clamp(lampLevel + delta), monitorLevel, cueLevel, tuningMessage(control, clamp(lampLevel + delta)));
            case MONITOR -> refresh(layout, actorPosition, facing, control, inspectedControls, micLevel, lampLevel, clamp(monitorLevel + delta), cueLevel, tuningMessage(control, clamp(monitorLevel + delta)));
            case CUE -> refresh(layout, actorPosition, facing, control, inspectedControls, micLevel, lampLevel, monitorLevel, clamp(cueLevel + delta), tuningMessage(control, clamp(cueLevel + delta)));
        };
    }

    public int levelOf(SignalControlId controlId) {
        return switch (controlId) {
            case MIC -> micLevel;
            case LAMP -> lampLevel;
            case MONITOR -> monitorLevel;
            case CUE -> cueLevel;
        };
    }

    public boolean aligned(SignalControlId controlId) {
        return levelOf(controlId) == controlId.targetLevel();
    }

    public boolean inspected(SignalControlId controlId) {
        return inspectedControls.contains(controlId);
    }

    public int inspectedCount() {
        return inspectedControls.size();
    }

    public Position frontPosition() {
        return actorPosition.translate(facing);
    }

    public SignalControlId facingControl() {
        return layout.pointAt(frontPosition());
    }

    public int width() {
        return layout.width();
    }

    public int height() {
        return layout.height();
    }

    private static SignalConsoleState refresh(
        GridActivityLayout<SignalControlId> layout,
        Position actorPosition,
        Direction facing,
        SignalControlId activeControl,
        Set<SignalControlId> inspectedControls,
        int micLevel,
        int lampLevel,
        int monitorLevel,
        int cueLevel,
        String statusOverride
    ) {
        boolean stabilized = micLevel == SignalControlId.MIC.targetLevel()
            && lampLevel == SignalControlId.LAMP.targetLevel()
            && monitorLevel == SignalControlId.MONITOR.targetLevel()
            && cueLevel == SignalControlId.CUE.targetLevel();

        String statusMessage = stabilized
            ? "좋아. 첫 신호가 흔들리지 않겠다."
            : statusOverride;

        return new SignalConsoleState(
            layout,
            actorPosition,
            facing,
            activeControl,
            inspectedControls,
            micLevel,
            lampLevel,
            monitorLevel,
            cueLevel,
            stabilized,
            statusMessage
        );
    }

    private static String previewMessage(
        GridActivityLayout<SignalControlId> layout,
        Direction direction,
        Position actorPosition
    ) {
        SignalControlId control = layout.pointAt(actorPosition.translate(direction));
        if (control == null) {
            return "큐 부스 안쪽으로 움직이며 장비 위치를 확인한다.";
        }
        return control.label() + " 앞이다. 조사한 뒤 값을 맞춘다.";
    }

    private static String inspectionMessage(SignalControlId control) {
        return control.label() + " 상태를 읽는다. 목표는 " + control.describeLevel(control.targetLevel()) + " 쪽이다.";
    }

    private static String tuningMessage(SignalControlId control, int level) {
        if (level == control.targetLevel()) {
            return control.label() + " 값이 " + control.describeLevel(level) + "으로 맞았다.";
        }
        return control.label() + " 값은 지금 " + control.describeLevel(level) + "이다. 아직 목표와 다르다.";
    }

    private static int clamp(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }
}
