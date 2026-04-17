package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public final class InMemoryChapterStateAdapter
    implements LoadChapterStatePort, SaveChapterStatePort, ResetChapterStatePort {

    private ChapterState chapterState;

    @Override
    public Optional<ChapterState> load() {
        return Optional.ofNullable(chapterState);
    }

    @Override
    public void save(ChapterState chapterState) {
        this.chapterState = chapterState;
    }

    @Override
    public void reset() {
        this.chapterState = null;
    }
}
