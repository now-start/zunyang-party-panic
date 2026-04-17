package org.nowstart.zunyang.partypanic.domain.photo;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record PhotoBayState(
    GridActivityLayout<PhotoFocusId> layout,
    Position actorPosition,
    Direction facing,
    PhotoFocusId activeFocus,
    Set<PhotoFocusId> lockedFocuses,
    Set<PhotoFocusId> reviewedOptionalFocuses,
    boolean readyToReturn,
    String statusMessage
) {

    public PhotoBayState {
        Objects.requireNonNull(layout, "layout must not be null");
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(lockedFocuses, "lockedFocuses must not be null");
        Objects.requireNonNull(reviewedOptionalFocuses, "reviewedOptionalFocuses must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<PhotoFocusId> normalized = EnumSet.noneOf(PhotoFocusId.class);
        normalized.addAll(lockedFocuses);
        lockedFocuses = Collections.unmodifiableSet(normalized);

        EnumSet<PhotoFocusId> reviewedNormalized = EnumSet.noneOf(PhotoFocusId.class);
        reviewedNormalized.addAll(reviewedOptionalFocuses);
        reviewedOptionalFocuses = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static PhotoBayState initial(GridActivityLayout<PhotoFocusId> layout) {
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

    public PhotoBayState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = layout.isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            layout,
            resolved,
            direction,
            null,
            lockedFocuses,
            reviewedOptionalFocuses,
            previewMessage(layout, direction, resolved)
        );
    }

    public PhotoBayState inspect() {
        PhotoFocusId focus = facingFocus();
        if (focus == null) {
            return refresh(
                layout,
                actorPosition,
                facing,
                null,
                lockedFocuses,
                reviewedOptionalFocuses,
                "지금 손볼 포토 포인트가 없다."
            );
        }

        if (lockedFocuses.contains(focus)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                focus,
                lockedFocuses,
                reviewedOptionalFocuses,
                focus.label() + "는 이미 고정됐다."
            );
        }

        if (focus.required()) {
            EnumSet<PhotoFocusId> nextLocked = EnumSet.noneOf(PhotoFocusId.class);
            nextLocked.addAll(lockedFocuses);
            nextLocked.add(focus);
            return refresh(
                layout,
                actorPosition,
                facing,
                focus,
                nextLocked,
                reviewedOptionalFocuses,
                lockMessage(focus, nextLocked)
            );
        }

        if (reviewedOptionalFocuses.contains(focus)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                focus,
                lockedFocuses,
                reviewedOptionalFocuses,
                focus.label() + "는 이미 한번 살펴봤다."
            );
        }

        EnumSet<PhotoFocusId> nextReviewed = EnumSet.noneOf(PhotoFocusId.class);
        nextReviewed.addAll(reviewedOptionalFocuses);
        nextReviewed.add(focus);

        return refresh(
            layout,
            actorPosition,
            facing,
            focus,
            lockedFocuses,
            nextReviewed,
            focus.label() + "는 이번에 고정하지 않는다. 대신 장면 톤을 위해 한 번 체크해 둔다."
        );
    }

    public boolean locked(PhotoFocusId focus) {
        return lockedFocuses.contains(focus);
    }

    public boolean reviewedOptional(PhotoFocusId focus) {
        return reviewedOptionalFocuses.contains(focus);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalFocuses.size();
    }

    public PhotoFocusId facingFocus() {
        return layout.pointAt(actorPosition.translate(facing));
    }

    public int width() {
        return layout.width();
    }

    public int height() {
        return layout.height();
    }

    private static PhotoBayState refresh(
        GridActivityLayout<PhotoFocusId> layout,
        Position actorPosition,
        Direction facing,
        PhotoFocusId activeFocus,
        Set<PhotoFocusId> lockedFocuses,
        Set<PhotoFocusId> reviewedOptionalFocuses,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(PhotoFocusId.values())
            .filter(PhotoFocusId::required)
            .allMatch(lockedFocuses::contains);

        String resolvedMessage = readyToReturn
            ? "프레임, 의자, 빛이 다 맞았다. 오늘을 남길 장면이 고정됐다."
            : statusMessage;

        return new PhotoBayState(
            layout,
            actorPosition,
            facing,
            activeFocus,
            lockedFocuses,
            reviewedOptionalFocuses,
            readyToReturn,
            resolvedMessage
        );
    }

    private static String previewMessage(
        GridActivityLayout<PhotoFocusId> layout,
        Direction direction,
        Position actorPosition
    ) {
        PhotoFocusId focus = layout.pointAt(actorPosition.translate(direction));
        if (focus == null) {
            return "포토 베이를 돌며 남길 장면의 중심선을 먼저 찾는다.";
        }
        return focus.label() + " 앞이다. 지금 고정할지 판단한다.";
    }

    private static String lockMessage(PhotoFocusId focus, Set<PhotoFocusId> lockedFocuses) {
        long lockedRequired = lockedFocuses.stream()
            .filter(PhotoFocusId::required)
            .count();
        return focus.label() + "를 맞췄다. 프레임 고정은 " + lockedRequired + "/3개 정리됐다.";
    }
}
