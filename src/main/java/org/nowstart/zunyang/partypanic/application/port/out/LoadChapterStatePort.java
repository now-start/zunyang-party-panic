package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public interface LoadChapterStatePort {

    Optional<ChapterState> load();
}
