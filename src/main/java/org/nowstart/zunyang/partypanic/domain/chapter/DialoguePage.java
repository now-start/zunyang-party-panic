package org.nowstart.zunyang.partypanic.domain.chapter;

public record DialoguePage(
    String speaker,
    String text
) {

    public DialoguePage {
        if (speaker == null || speaker.isBlank()) {
            throw new IllegalArgumentException("Dialogue speaker must not be blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Dialogue text must not be blank");
        }
    }
}
