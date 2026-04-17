package org.nowstart.zunyang.partypanic.adapter.out.content;

import com.badlogic.gdx.utils.Json;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterScriptPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;

public final class ResourceChapterScriptAdapter implements LoadChapterScriptPort {

    private static final String RESOURCE_ROOT = "content/chapters/";

    private final Json json = new Json();

    @Override
    public ChapterScript load(ChapterId chapterId) {
        String resourcePath = RESOURCE_ROOT + chapterId.name().toLowerCase() + ".json";
        String rawDocument = readResource(resourcePath);
        return parse(resourcePath, rawDocument, chapterId);
    }

    ChapterScript parse(String resourcePath, String rawDocument, ChapterId expectedChapterId) {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        Objects.requireNonNull(rawDocument, "rawDocument must not be null");
        Objects.requireNonNull(expectedChapterId, "expectedChapterId must not be null");

        ChapterScriptDocument document = json.fromJson(ChapterScriptDocument.class, rawDocument);
        if (document == null) {
            throw new IllegalStateException("Unable to parse chapter resource: " + resourcePath);
        }

        return new ChapterScript(
            parseChapterId(resourcePath, expectedChapterId, document.chapterId),
            requireText(document.title, "title", resourcePath),
            requireText(document.subtitle, "subtitle", resourcePath),
            requireText(document.visualToken, "visualToken", resourcePath),
            parseActivityType(resourcePath, document.activityType),
            parsePages(resourcePath, document.pages)
        );
    }

    private String readResource(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing chapter resource: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read chapter resource: " + resourcePath, exception);
        }
    }

    private ChapterId parseChapterId(String resourcePath, ChapterId expectedChapterId, String rawChapterId) {
        String chapterIdText = requireText(rawChapterId, "chapterId", resourcePath);
        try {
            ChapterId parsedChapterId = ChapterId.valueOf(chapterIdText);
            if (parsedChapterId != expectedChapterId) {
                throw new IllegalStateException(
                    "Chapter id mismatch in " + resourcePath + ": expected " + expectedChapterId + " but was " + parsedChapterId
                );
            }
            return parsedChapterId;
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Unsupported chapterId in " + resourcePath + ": " + chapterIdText, exception);
        }
    }

    private ChapterActivityType parseActivityType(String resourcePath, String rawActivityType) {
        if (rawActivityType == null || rawActivityType.isBlank()) {
            return ChapterActivityType.NONE;
        }
        String activityTypeText = rawActivityType.strip();
        try {
            return ChapterActivityType.valueOf(activityTypeText);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                "Unsupported activityType in " + resourcePath + ": " + activityTypeText,
                exception
            );
        }
    }

    private java.util.List<DialoguePage> parsePages(String resourcePath, DialoguePageDocument[] pages) {
        if (pages == null || pages.length == 0) {
            throw new IllegalStateException("Chapter pages must not be empty: " + resourcePath);
        }
        return Arrays.stream(pages)
            .map(page -> new DialoguePage(
                requireText(page == null ? null : page.speaker, "page.speaker", resourcePath),
                requireText(page == null ? null : page.text, "page.text", resourcePath)
            ))
            .toList();
    }

    private String requireText(String value, String fieldName, String resourcePath) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing or blank " + fieldName + " in " + resourcePath);
        }
        return value.strip();
    }

    private static final class ChapterScriptDocument {
        public String chapterId;
        public String title;
        public String subtitle;
        public String visualToken;
        public String activityType;
        public DialoguePageDocument[] pages;
    }

    private static final class DialoguePageDocument {
        public String speaker;
        public String text;
    }
}
