package org.nowstart.zunyang.partypanic.domain.centerpiece;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record CenterpieceTableState(
    Position actorPosition,
    Direction facing,
    CenterpiecePlacementId activePlacement,
    Set<CenterpiecePlacementId> placedItems,
    Set<CenterpiecePlacementId> reviewedOptionalPlacements,
    boolean readyToReturn,
    String statusMessage
) {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 5;

    public CenterpieceTableState {
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(placedItems, "placedItems must not be null");
        Objects.requireNonNull(reviewedOptionalPlacements, "reviewedOptionalPlacements must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<CenterpiecePlacementId> normalized = EnumSet.noneOf(CenterpiecePlacementId.class);
        normalized.addAll(placedItems);
        placedItems = Collections.unmodifiableSet(normalized);

        EnumSet<CenterpiecePlacementId> reviewedNormalized = EnumSet.noneOf(CenterpiecePlacementId.class);
        reviewedNormalized.addAll(reviewedOptionalPlacements);
        reviewedOptionalPlacements = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static CenterpieceTableState initial() {
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

    public CenterpieceTableState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            resolved,
            direction,
            null,
            placedItems,
            reviewedOptionalPlacements,
            previewMessage(direction, resolved)
        );
    }

    public CenterpieceTableState inspect() {
        CenterpiecePlacementId placement = facingPlacement();
        if (placement == null) {
            return refresh(
                actorPosition,
                facing,
                null,
                placedItems,
                reviewedOptionalPlacements,
                "지금 손볼 배치 포인트가 없다."
            );
        }

        if (placedItems.contains(placement)) {
            return refresh(
                actorPosition,
                facing,
                placement,
                placedItems,
                reviewedOptionalPlacements,
                placement.label() + "는 이미 정리됐다."
            );
        }

        if (placement.required()) {
            EnumSet<CenterpiecePlacementId> nextPlaced = EnumSet.noneOf(CenterpiecePlacementId.class);
            nextPlaced.addAll(placedItems);
            nextPlaced.add(placement);
            return refresh(
                actorPosition,
                facing,
                placement,
                nextPlaced,
                reviewedOptionalPlacements,
                placeMessage(placement, nextPlaced)
            );
        }

        if (reviewedOptionalPlacements.contains(placement)) {
            return refresh(
                actorPosition,
                facing,
                placement,
                placedItems,
                reviewedOptionalPlacements,
                placement.label() + "는 이미 한 번 균형을 확인했다."
            );
        }

        EnumSet<CenterpiecePlacementId> nextReviewed = EnumSet.noneOf(CenterpiecePlacementId.class);
        nextReviewed.addAll(reviewedOptionalPlacements);
        nextReviewed.add(placement);

        return refresh(
            actorPosition,
            facing,
            placement,
            placedItems,
            nextReviewed,
            placement.label() + "는 지금 더 보태지 않는 편이 낫다. 대신 자리감은 확인해 둔다."
        );
    }

    public boolean placed(CenterpiecePlacementId placement) {
        return placedItems.contains(placement);
    }

    public boolean reviewedOptional(CenterpiecePlacementId placement) {
        return reviewedOptionalPlacements.contains(placement);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalPlacements.size();
    }

    public CenterpiecePlacementId facingPlacement() {
        return placementAt(actorPosition.translate(facing));
    }

    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    private static CenterpieceTableState refresh(
        Position actorPosition,
        Direction facing,
        CenterpiecePlacementId activePlacement,
        Set<CenterpiecePlacementId> placedItems,
        Set<CenterpiecePlacementId> reviewedOptionalPlacements,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(CenterpiecePlacementId.values())
            .filter(CenterpiecePlacementId::required)
            .allMatch(placedItems::contains);

        String resolvedMessage = readyToReturn
            ? "중심 배치가 다 맞았다. 이제 허브 전체가 오늘 밤처럼 보인다."
            : statusMessage;

        return new CenterpieceTableState(
            actorPosition,
            facing,
            activePlacement,
            placedItems,
            reviewedOptionalPlacements,
            readyToReturn,
            resolvedMessage
        );
    }

    private static boolean isWalkable(Position position) {
        return position.x() >= 0
            && position.x() < WIDTH
            && position.y() >= 0
            && position.y() < HEIGHT
            && placementAt(position) == null;
    }

    private static CenterpiecePlacementId placementAt(Position position) {
        return Arrays.stream(CenterpiecePlacementId.values())
            .filter(item -> item.position().equals(position))
            .findFirst()
            .orElse(null);
    }

    private static String previewMessage(Direction direction, Position actorPosition) {
        CenterpiecePlacementId placement = placementAt(actorPosition.translate(direction));
        if (placement == null) {
            return "중앙 테이블 둘레를 돌며 비어 보이는 지점을 먼저 본다.";
        }
        return placement.label() + " 앞이다. 균형을 맞출지 판단한다.";
    }

    private static String placeMessage(
        CenterpiecePlacementId placement,
        Set<CenterpiecePlacementId> placedItems
    ) {
        long placedRequired = placedItems.stream()
            .filter(CenterpiecePlacementId::required)
            .count();
        return placement.label() + "를 맞췄다. 중심 배치는 " + placedRequired + "/3개 정리됐다.";
    }
}
