package org.nowstart.zunyang.partypanic.application.chapter;

import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;

final class ChapterViewMapper {

    private ChapterViewMapper() {
    }

    static ChapterViewResult toView(ChapterState state) {
        DialoguePage page = state.currentPage();
        return new ChapterViewResult(
            state.script().chapterId().name(),
            state.script().title(),
            state.script().subtitle(),
            state.script().visualToken(),
            state.script().activityType().name(),
            page.speaker(),
            page.text(),
            state.pageIndex() + 1,
            state.totalPages(),
            state.activityReady(),
            state.completed()
        );
    }
}
