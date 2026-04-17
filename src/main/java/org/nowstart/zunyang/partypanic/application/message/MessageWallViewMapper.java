package org.nowstart.zunyang.partypanic.application.message;

import java.util.Arrays;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageNoteView;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.domain.message.MessageNoteId;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

final class MessageWallViewMapper {

    private MessageWallViewMapper() {
    }

    static MessageWallViewResult toView(MessageWallState state) {
        long selectedRequired = state.selectedNotes().stream()
            .filter(MessageNoteId::required)
            .count();
        long requiredCount = Arrays.stream(MessageNoteId.values())
            .filter(MessageNoteId::required)
            .count();

        return new MessageWallViewResult(
            "메시지 월",
            "arrow: 이동  |  z: 문장 고르기  |  enter: 복귀",
            state.width(),
            state.height(),
            state.actorPosition().x(),
            state.actorPosition().y(),
            state.facing().name(),
            state.activeNote() == null ? null : state.activeNote().name(),
            (int) selectedRequired,
            (int) requiredCount,
            state.readyToReturn(),
            state.statusMessage(),
            Arrays.stream(MessageNoteId.values())
                .map(note -> new MessageNoteView(
                    note.name(),
                    note.label(),
                    note.excerpt(),
                    note.position().x(),
                    note.position().y(),
                    note.required(),
                    state.selected(note),
                    state.activeNote() == note
                ))
                .toList()
        );
    }
}
