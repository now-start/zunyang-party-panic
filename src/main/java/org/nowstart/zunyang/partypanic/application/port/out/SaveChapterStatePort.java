package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public interface SaveChapterStatePort {

    void save(ChapterState chapterState);
}
