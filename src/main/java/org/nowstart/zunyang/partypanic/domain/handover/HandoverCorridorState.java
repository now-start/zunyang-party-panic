package org.nowstart.zunyang.partypanic.domain.handover;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record HandoverCorridorState(
    Position actorPosition,
    Direction facing,
    HandoverClueId activeClue,
    Set<HandoverClueId> collectedClues,
    Set<HandoverClueId> reviewedOptionalClues,
    boolean readyToReturn,
    String statusMessage
) {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 5;

    public HandoverCorridorState {
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(collectedClues, "collectedClues must not be null");
        Objects.requireNonNull(reviewedOptionalClues, "reviewedOptionalClues must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<HandoverClueId> normalized = EnumSet.noneOf(HandoverClueId.class);
        normalized.addAll(collectedClues);
        collectedClues = Collections.unmodifiableSet(normalized);

        EnumSet<HandoverClueId> reviewedNormalized = EnumSet.noneOf(HandoverClueId.class);
        reviewedNormalized.addAll(reviewedOptionalClues);
        reviewedOptionalClues = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static HandoverCorridorState initial() {
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

    public HandoverCorridorState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            resolved,
            direction,
            null,
            collectedClues,
            reviewedOptionalClues,
            previewMessage(direction, resolved)
        );
    }

    public HandoverCorridorState inspect() {
        HandoverClueId clue = facingClue();
        if (clue == null) {
            return refresh(
                actorPosition,
                facing,
                null,
                collectedClues,
                reviewedOptionalClues,
                "지금 읽을 기록 조각이 없다."
            );
        }

        if (collectedClues.contains(clue)) {
            return refresh(
                actorPosition,
                facing,
                clue,
                collectedClues,
                reviewedOptionalClues,
                clue.label() + "는 이미 확인했다."
            );
        }

        if (clue.required()) {
            EnumSet<HandoverClueId> nextCollected = EnumSet.noneOf(HandoverClueId.class);
            nextCollected.addAll(collectedClues);
            nextCollected.add(clue);
            return refresh(
                actorPosition,
                facing,
                clue,
                nextCollected,
                reviewedOptionalClues,
                collectMessage(clue, nextCollected)
            );
        }

        if (reviewedOptionalClues.contains(clue)) {
            return refresh(
                actorPosition,
                facing,
                clue,
                collectedClues,
                reviewedOptionalClues,
                clue.label() + "는 이미 한번 더 읽어 봤다."
            );
        }

        EnumSet<HandoverClueId> nextReviewed = EnumSet.noneOf(HandoverClueId.class);
        nextReviewed.addAll(reviewedOptionalClues);
        nextReviewed.add(clue);

        return refresh(
            actorPosition,
            facing,
            clue,
            collectedClues,
            nextReviewed,
            clue.label() + "도 의미는 있다. 이번 밤의 맥락을 위해 기록해 둔다."
        );
    }

    public boolean collected(HandoverClueId clue) {
        return collectedClues.contains(clue);
    }

    public boolean reviewedOptional(HandoverClueId clue) {
        return reviewedOptionalClues.contains(clue);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalClues.size();
    }

    public HandoverClueId facingClue() {
        return clueAt(actorPosition.translate(facing));
    }

    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    private static HandoverCorridorState refresh(
        Position actorPosition,
        Direction facing,
        HandoverClueId activeClue,
        Set<HandoverClueId> collectedClues,
        Set<HandoverClueId> reviewedOptionalClues,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(HandoverClueId.values())
            .filter(HandoverClueId::required)
            .allMatch(collectedClues::contains);

        String resolvedMessage = readyToReturn
            ? "이어진 단서가 모였다. 오늘 밤도 누적된 무대 위에 있었다."
            : statusMessage;

        return new HandoverCorridorState(
            actorPosition,
            facing,
            activeClue,
            collectedClues,
            reviewedOptionalClues,
            readyToReturn,
            resolvedMessage
        );
    }

    private static boolean isWalkable(Position position) {
        return position.x() >= 0
            && position.x() < WIDTH
            && position.y() >= 0
            && position.y() < HEIGHT
            && clueAt(position) == null;
    }

    private static HandoverClueId clueAt(Position position) {
        return Arrays.stream(HandoverClueId.values())
            .filter(clue -> clue.position().equals(position))
            .findFirst()
            .orElse(null);
    }

    private static String previewMessage(Direction direction, Position actorPosition) {
        HandoverClueId clue = clueAt(actorPosition.translate(direction));
        if (clue == null) {
            return "기록 복도를 따라 걸으며 이어진 밤의 흔적을 찾는다.";
        }
        return clue.label() + " 앞이다. 지금 이어 볼 단서인지 확인한다.";
    }

    private static String collectMessage(HandoverClueId clue, Set<HandoverClueId> collectedClues) {
        long collectedRequired = collectedClues.stream()
            .filter(HandoverClueId::required)
            .count();
        return clue.label() + "를 읽었다. 인수인계 단서는 " + collectedRequired + "/3개 이어졌다.";
    }
}
