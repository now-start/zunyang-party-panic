package org.nowstart.zunyang.partypanic.domain.props;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record PropsArchiveState(
    Position actorPosition,
    Direction facing,
    PropsItemId activeItem,
    Set<PropsItemId> collectedItems,
    Set<PropsItemId> reviewedOptionalItems,
    boolean readyToReturn,
    String statusMessage
) {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 5;

    public PropsArchiveState {
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(collectedItems, "collectedItems must not be null");
        Objects.requireNonNull(reviewedOptionalItems, "reviewedOptionalItems must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<PropsItemId> normalized = EnumSet.noneOf(PropsItemId.class);
        normalized.addAll(collectedItems);
        collectedItems = Collections.unmodifiableSet(normalized);

        EnumSet<PropsItemId> reviewedNormalized = EnumSet.noneOf(PropsItemId.class);
        reviewedNormalized.addAll(reviewedOptionalItems);
        reviewedOptionalItems = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static PropsArchiveState initial() {
        Position start = new Position(3, 2);
        return refresh(
            start,
            Direction.UP,
            null,
            Set.of(),
            Set.of(),
            previewMessage(Direction.UP, start)
        );
    }

    public PropsArchiveState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            resolved,
            direction,
            null,
            collectedItems,
            reviewedOptionalItems,
            previewMessage(direction, resolved)
        );
    }

    public PropsArchiveState inspect() {
        PropsItemId item = facingItem();
        if (item == null) {
            return refresh(
                actorPosition,
                facing,
                null,
                collectedItems,
                reviewedOptionalItems,
                "지금 손댈 상자가 없다."
            );
        }

        if (collectedItems.contains(item)) {
            return refresh(
                actorPosition,
                facing,
                item,
                collectedItems,
                reviewedOptionalItems,
                item.label() + "는 이미 챙겼다."
            );
        }

        if (item.required()) {
            EnumSet<PropsItemId> nextCollected = EnumSet.noneOf(PropsItemId.class);
            nextCollected.addAll(collectedItems);
            nextCollected.add(item);
            return refresh(
                actorPosition,
                facing,
                item,
                nextCollected,
                reviewedOptionalItems,
                collectMessage(item, nextCollected)
            );
        }

        if (reviewedOptionalItems.contains(item)) {
            return refresh(
                actorPosition,
                facing,
                item,
                collectedItems,
                reviewedOptionalItems,
                item.label() + "는 이미 한번 살펴봤다."
            );
        }

        EnumSet<PropsItemId> nextReviewed = EnumSet.noneOf(PropsItemId.class);
        nextReviewed.addAll(reviewedOptionalItems);
        nextReviewed.add(item);

        return refresh(
            actorPosition,
            facing,
            item,
            collectedItems,
            nextReviewed,
            item.label() + "는 지금 쓰기엔 과하다. 이번엔 두고 가지만 위치는 기억해 둔다."
        );
    }

    public boolean collected(PropsItemId item) {
        return collectedItems.contains(item);
    }

    public boolean reviewedOptional(PropsItemId item) {
        return reviewedOptionalItems.contains(item);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalItems.size();
    }

    public PropsItemId facingItem() {
        return itemAt(actorPosition.translate(facing));
    }

    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    private static PropsArchiveState refresh(
        Position actorPosition,
        Direction facing,
        PropsItemId activeItem,
        Set<PropsItemId> collectedItems,
        Set<PropsItemId> reviewedOptionalItems,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(PropsItemId.values())
            .filter(PropsItemId::required)
            .allMatch(collectedItems::contains);

        String resolvedMessage = readyToReturn
            ? "필요한 소품은 다 모였다. 허브로 돌아가 배치하면 된다."
            : statusMessage;

        return new PropsArchiveState(
            actorPosition,
            facing,
            activeItem,
            collectedItems,
            reviewedOptionalItems,
            readyToReturn,
            resolvedMessage
        );
    }

    private static boolean isWalkable(Position position) {
        return position.x() >= 0
            && position.x() < WIDTH
            && position.y() >= 0
            && position.y() < HEIGHT
            && itemAt(position) == null;
    }

    private static PropsItemId itemAt(Position position) {
        return Arrays.stream(PropsItemId.values())
            .filter(item -> item.position().equals(position))
            .findFirst()
            .orElse(null);
    }

    private static String previewMessage(Direction direction, Position actorPosition) {
        PropsItemId item = itemAt(actorPosition.translate(direction));
        if (item == null) {
            return "아카이브 선반 사이를 돌며 오늘 필요한 상자를 찾는다.";
        }
        return item.label() + " 앞이다. 조사해서 챙길지 판단한다.";
    }

    private static String collectMessage(PropsItemId item, Set<PropsItemId> collectedItems) {
        long collectedRequired = collectedItems.stream()
            .filter(PropsItemId::required)
            .count();
        return item.label() + "를 챙겼다. 필요한 소품은 " + collectedRequired + "/3개 모였다.";
    }
}
