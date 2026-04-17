package org.nowstart.zunyang.partypanic.domain.model;

import java.util.List;

public final class Dialogue {
    private final List<DialogueLine> lines;
    private final int currentIndex;

    public Dialogue(List<DialogueLine> lines, int currentIndex) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Dialogue must contain at least one line.");
        }
        if (currentIndex < 0 || currentIndex >= lines.size()) {
            throw new IllegalArgumentException("Dialogue index out of range: " + currentIndex);
        }
        this.lines = List.copyOf(lines);
        this.currentIndex = currentIndex;
    }

    public static Dialogue singleSpeaker(String speaker, List<String> pages) {
        return new Dialogue(
                pages.stream()
                        .map(page -> new DialogueLine(speaker, page))
                        .toList(),
                0
        );
    }

    public List<DialogueLine> lines() {
        return lines;
    }

    public int currentIndex() {
        return currentIndex;
    }

    public int lineCount() {
        return lines.size();
    }

    public DialogueLine currentLine() {
        return lines.get(currentIndex);
    }

    public boolean hasNext() {
        return currentIndex < lines.size() - 1;
    }

    public Dialogue advance() {
        if (!hasNext()) {
            return this;
        }
        return new Dialogue(lines, currentIndex + 1);
    }
}
