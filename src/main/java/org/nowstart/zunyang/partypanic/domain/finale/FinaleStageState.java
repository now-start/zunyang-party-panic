package org.nowstart.zunyang.partypanic.domain.finale;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record FinaleStageState(
    GridActivityLayout<FinaleCheckpointId> layout,
    Position actorPosition,
    Direction facing,
    FinaleCheckpointId activeCheckpoint,
    Set<FinaleCheckpointId> checkedCheckpoints,
    Set<FinaleCheckpointId> reviewedOptionalCheckpoints,
    boolean readyToReturn,
    String statusMessage
) {

    public FinaleStageState {
        Objects.requireNonNull(layout, "layout must not be null");
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(checkedCheckpoints, "checkedCheckpoints must not be null");
        Objects.requireNonNull(reviewedOptionalCheckpoints, "reviewedOptionalCheckpoints must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<FinaleCheckpointId> normalized = EnumSet.noneOf(FinaleCheckpointId.class);
        normalized.addAll(checkedCheckpoints);
        checkedCheckpoints = Collections.unmodifiableSet(normalized);

        EnumSet<FinaleCheckpointId> reviewedNormalized = EnumSet.noneOf(FinaleCheckpointId.class);
        reviewedNormalized.addAll(reviewedOptionalCheckpoints);
        reviewedOptionalCheckpoints = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static FinaleStageState initial(GridActivityLayout<FinaleCheckpointId> layout) {
        Position start = layout.actorStart();
        return refresh(
            layout,
            start,
            Direction.UP,
            null,
            Set.of(),
            Set.of(),
            previewMessage(layout, Direction.UP, start)
        );
    }

    public FinaleStageState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = layout.isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            layout,
            resolved,
            direction,
            null,
            checkedCheckpoints,
            reviewedOptionalCheckpoints,
            previewMessage(layout, direction, resolved)
        );
    }

    public FinaleStageState inspect() {
        FinaleCheckpointId checkpoint = facingCheckpoint();
        if (checkpoint == null) {
            return refresh(
                layout,
                actorPosition,
                facing,
                null,
                checkedCheckpoints,
                reviewedOptionalCheckpoints,
                "지금 확인할 최종 점검 지점이 없다."
            );
        }

        if (checkedCheckpoints.contains(checkpoint)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                checkpoint,
                checkedCheckpoints,
                reviewedOptionalCheckpoints,
                checkpoint.label() + "는 이미 마지막 확인을 마쳤다."
            );
        }

        if (checkpoint.required()) {
            EnumSet<FinaleCheckpointId> nextChecked = EnumSet.noneOf(FinaleCheckpointId.class);
            nextChecked.addAll(checkedCheckpoints);
            nextChecked.add(checkpoint);
            return refresh(
                layout,
                actorPosition,
                facing,
                checkpoint,
                nextChecked,
                reviewedOptionalCheckpoints,
                checkMessage(checkpoint, nextChecked)
            );
        }

        if (reviewedOptionalCheckpoints.contains(checkpoint)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                checkpoint,
                checkedCheckpoints,
                reviewedOptionalCheckpoints,
                checkpoint.label() + "는 이미 한번 더 점검했다."
            );
        }

        EnumSet<FinaleCheckpointId> nextReviewed = EnumSet.noneOf(FinaleCheckpointId.class);
        nextReviewed.addAll(reviewedOptionalCheckpoints);
        nextReviewed.add(checkpoint);

        return refresh(
            layout,
            actorPosition,
            facing,
            checkpoint,
            checkedCheckpoints,
            nextReviewed,
            checkpoint.label() + "도 한번 확인해 둔다. 무대 전체의 마감도를 올리는 체크다."
        );
    }

    public boolean checked(FinaleCheckpointId checkpoint) {
        return checkedCheckpoints.contains(checkpoint);
    }

    public boolean reviewedOptional(FinaleCheckpointId checkpoint) {
        return reviewedOptionalCheckpoints.contains(checkpoint);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalCheckpoints.size();
    }

    public FinaleCheckpointId facingCheckpoint() {
        return layout.pointAt(actorPosition.translate(facing));
    }

    public int width() {
        return layout.width();
    }

    public int height() {
        return layout.height();
    }

    private static FinaleStageState refresh(
        GridActivityLayout<FinaleCheckpointId> layout,
        Position actorPosition,
        Direction facing,
        FinaleCheckpointId activeCheckpoint,
        Set<FinaleCheckpointId> checkedCheckpoints,
        Set<FinaleCheckpointId> reviewedOptionalCheckpoints,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(FinaleCheckpointId.values())
            .filter(FinaleCheckpointId::required)
            .allMatch(checkedCheckpoints::contains);

        String resolvedMessage = readyToReturn
            ? "좋아. 이제 신호를 보내면 된다."
            : statusMessage;

        return new FinaleStageState(
            layout,
            actorPosition,
            facing,
            activeCheckpoint,
            checkedCheckpoints,
            reviewedOptionalCheckpoints,
            readyToReturn,
            resolvedMessage
        );
    }

    private static String previewMessage(
        GridActivityLayout<FinaleCheckpointId> layout,
        Direction direction,
        Position actorPosition
    ) {
        FinaleCheckpointId checkpoint = layout.pointAt(actorPosition.translate(direction));
        if (checkpoint == null) {
            return "메인 스테이지를 돌며 마지막 점검 지점을 확인한다.";
        }
        return checkpoint.label() + " 앞이다. 지금 최종 확인이 필요한지 본다.";
    }

    private static String checkMessage(FinaleCheckpointId checkpoint, Set<FinaleCheckpointId> checkedCheckpoints) {
        long checkedRequired = checkedCheckpoints.stream()
            .filter(FinaleCheckpointId::required)
            .count();
        return checkpoint.label() + " 점검을 마쳤다. 최종 확인은 " + checkedRequired + "/3개다.";
    }
}
