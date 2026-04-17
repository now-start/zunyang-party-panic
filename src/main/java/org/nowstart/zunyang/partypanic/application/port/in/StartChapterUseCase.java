package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

public interface StartChapterUseCase {

    ChapterViewResult start(ChapterId chapterId);
}
