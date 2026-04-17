package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;

public interface LoadChapterScriptPort {

    ChapterScript load(ChapterId chapterId);
}
