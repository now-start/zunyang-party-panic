package org.nowstart.zunyang.partypanic.domain.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record MessageWallState(
    GridActivityLayout<MessageNoteId> layout,
    Position actorPosition,
    Direction facing,
    MessageNoteId activeNote,
    Set<MessageNoteId> selectedNotes,
    Set<MessageNoteId> reviewedOptionalNotes,
    boolean readyToReturn,
    String statusMessage
) {

    public MessageWallState {
        Objects.requireNonNull(layout, "layout must not be null");
        Objects.requireNonNull(actorPosition, "actorPosition must not be null");
        Objects.requireNonNull(facing, "facing must not be null");
        Objects.requireNonNull(selectedNotes, "selectedNotes must not be null");
        Objects.requireNonNull(reviewedOptionalNotes, "reviewedOptionalNotes must not be null");
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");

        EnumSet<MessageNoteId> normalized = EnumSet.noneOf(MessageNoteId.class);
        normalized.addAll(selectedNotes);
        selectedNotes = Collections.unmodifiableSet(normalized);

        EnumSet<MessageNoteId> reviewedNormalized = EnumSet.noneOf(MessageNoteId.class);
        reviewedNormalized.addAll(reviewedOptionalNotes);
        reviewedOptionalNotes = Collections.unmodifiableSet(reviewedNormalized);
    }

    public static MessageWallState initial(GridActivityLayout<MessageNoteId> layout) {
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

    public MessageWallState move(Direction direction) {
        Position nextPosition = actorPosition.translate(direction);
        Position resolved = layout.isWalkable(nextPosition) ? nextPosition : actorPosition;
        return refresh(
            layout,
            resolved,
            direction,
            null,
            selectedNotes,
            reviewedOptionalNotes,
            previewMessage(layout, direction, resolved)
        );
    }

    public MessageWallState inspect() {
        MessageNoteId note = facingNote();
        if (note == null) {
            return refresh(
                layout,
                actorPosition,
                facing,
                null,
                selectedNotes,
                reviewedOptionalNotes,
                "지금 고를 문장이 없다."
            );
        }

        if (selectedNotes.contains(note)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                note,
                selectedNotes,
                reviewedOptionalNotes,
                note.label() + "는 이미 오늘 벽에 남길 문장으로 골랐다."
            );
        }

        if (note.required()) {
            EnumSet<MessageNoteId> nextSelected = EnumSet.noneOf(MessageNoteId.class);
            nextSelected.addAll(selectedNotes);
            nextSelected.add(note);
            return refresh(
                layout,
                actorPosition,
                facing,
                note,
                nextSelected,
                reviewedOptionalNotes,
                selectMessage(note, nextSelected)
            );
        }

        if (reviewedOptionalNotes.contains(note)) {
            return refresh(
                layout,
                actorPosition,
                facing,
                note,
                selectedNotes,
                reviewedOptionalNotes,
                note.label() + "는 이미 한번 더 읽어 봤다."
            );
        }

        EnumSet<MessageNoteId> nextReviewed = EnumSet.noneOf(MessageNoteId.class);
        nextReviewed.addAll(reviewedOptionalNotes);
        nextReviewed.add(note);

        return refresh(
            layout,
            actorPosition,
            facing,
            note,
            selectedNotes,
            nextReviewed,
            note.label() + "도 좋다. 오늘의 온도를 위해 따로 기억해 둔다."
        );
    }

    public boolean selected(MessageNoteId note) {
        return selectedNotes.contains(note);
    }

    public boolean reviewedOptional(MessageNoteId note) {
        return reviewedOptionalNotes.contains(note);
    }

    public int reviewedOptionalCount() {
        return reviewedOptionalNotes.size();
    }

    public MessageNoteId facingNote() {
        return layout.pointAt(actorPosition.translate(facing));
    }

    public int width() {
        return layout.width();
    }

    public int height() {
        return layout.height();
    }

    private static MessageWallState refresh(
        GridActivityLayout<MessageNoteId> layout,
        Position actorPosition,
        Direction facing,
        MessageNoteId activeNote,
        Set<MessageNoteId> selectedNotes,
        Set<MessageNoteId> reviewedOptionalNotes,
        String statusMessage
    ) {
        boolean readyToReturn = Arrays.stream(MessageNoteId.values())
            .filter(MessageNoteId::required)
            .allMatch(selectedNotes::contains);

        String resolvedMessage = readyToReturn
            ? "오늘 곁에 둘 문장이 정리됐다. 이제 마지막 무대 점검만 남았다."
            : statusMessage;

        return new MessageWallState(
            layout,
            actorPosition,
            facing,
            activeNote,
            selectedNotes,
            reviewedOptionalNotes,
            readyToReturn,
            resolvedMessage
        );
    }

    private static String previewMessage(
        GridActivityLayout<MessageNoteId> layout,
        Direction direction,
        Position actorPosition
    ) {
        MessageNoteId note = layout.pointAt(actorPosition.translate(direction));
        if (note == null) {
            return "메시지 월을 따라 오늘 곁에 둘 짧은 문장을 고른다.";
        }
        return note.label() + " 앞이다. 오늘 벽에 남길 말인지 확인한다.";
    }

    private static String selectMessage(MessageNoteId note, Set<MessageNoteId> selectedNotes) {
        long selectedRequired = selectedNotes.stream()
            .filter(MessageNoteId::required)
            .count();
        return note.label() + "를 남기기로 했다. 선택한 문장은 " + selectedRequired + "/2개다.";
    }
}
