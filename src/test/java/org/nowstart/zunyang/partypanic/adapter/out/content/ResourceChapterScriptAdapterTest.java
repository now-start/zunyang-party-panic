package org.nowstart.zunyang.partypanic.adapter.out.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;

class ResourceChapterScriptAdapterTest {

    private final ResourceChapterScriptAdapter adapter = new ResourceChapterScriptAdapter();

    @Test
    void loads_signal_script_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.SIGNAL);

        assertEquals(ChapterId.SIGNAL, script.chapterId());
        assertEquals("첫 신호를 맞추다", script.title());
        assertEquals("signal", script.visualToken());
        assertEquals(ChapterActivityType.SIGNAL_CONSOLE, script.activityType());
        assertEquals(3, script.pages().size());
        assertEquals("조력자", script.pages().getFirst().speaker());
    }

    @Test
    void loads_props_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.PROPS);

        assertEquals(ChapterActivityType.PROPS_ARCHIVE, script.activityType());
        assertEquals("소품 아카이브", script.subtitle());
    }

    @Test
    void loads_centerpiece_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.CENTERPIECE);

        assertEquals(ChapterActivityType.CENTERPIECE_TABLE, script.activityType());
        assertEquals("중앙 테이블", script.subtitle());
    }

    @Test
    void loads_photo_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.PHOTO);

        assertEquals(ChapterActivityType.PHOTO_BAY, script.activityType());
        assertEquals("포토 베이", script.subtitle());
    }

    @Test
    void loads_handover_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.HANDOVER);

        assertEquals(ChapterActivityType.HANDOVER_CORRIDOR, script.activityType());
        assertEquals("기록 복도", script.subtitle());
    }

    @Test
    void loads_message_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.MESSAGE);

        assertEquals(ChapterActivityType.MESSAGE_WALL, script.activityType());
        assertEquals("메시지 월", script.subtitle());
    }

    @Test
    void loads_finale_activity_type_from_authored_resource() {
        ChapterScript script = adapter.load(ChapterId.FINALE);

        assertEquals(ChapterActivityType.FINALE_STAGE, script.activityType());
        assertEquals("메인 스테이지", script.subtitle());
    }

    @Test
    void loads_all_authored_chapters_without_validation_errors() {
        for (ChapterId chapterId : ChapterId.values()) {
            assertNotNull(adapter.load(chapterId));
        }
    }

    @Test
    void rejects_mismatched_chapter_id_in_authored_resource() {
        String invalidDocument = """
            {
              "chapterId": "PROPS",
              "title": "첫 신호를 맞추다",
              "subtitle": "큐 부스 정비",
              "visualToken": "signal",
              "activityType": "SIGNAL_CONSOLE",
              "pages": [
                { "speaker": "조력자", "text": "큐를 먼저 확인한다." }
              ]
            }
            """;

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> adapter.parse("content/chapters/signal.json", invalidDocument, ChapterId.SIGNAL)
        );

        assertTrue(exception.getMessage().contains("Chapter id mismatch"));
    }

    @Test
    void rejects_blank_page_text_in_authored_resource() {
        String invalidDocument = """
            {
              "chapterId": "SIGNAL",
              "title": "첫 신호를 맞추다",
              "subtitle": "큐 부스 정비",
              "visualToken": "signal",
              "activityType": "SIGNAL_CONSOLE",
              "pages": [
                { "speaker": "조력자", "text": "   " }
              ]
            }
            """;

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> adapter.parse("content/chapters/signal.json", invalidDocument, ChapterId.SIGNAL)
        );

        assertTrue(exception.getMessage().contains("page.text"));
    }
}
