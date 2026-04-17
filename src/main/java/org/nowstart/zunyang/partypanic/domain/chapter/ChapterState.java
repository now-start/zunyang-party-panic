package org.nowstart.zunyang.partypanic.domain.chapter;

public record ChapterState(
    ChapterScript script,
    int pageIndex,
    ChapterStage stage
) {

    public static ChapterState start(ChapterScript script) {
        return new ChapterState(script, 0, ChapterStage.DIALOGUE);
    }

    public DialoguePage currentPage() {
        return script.pages().get(pageIndex);
    }

    public int totalPages() {
        return script.pages().size();
    }

    public boolean completed() {
        return stage == ChapterStage.COMPLETED;
    }

    public boolean activityReady() {
        return stage == ChapterStage.ACTIVITY_READY;
    }

    public ChapterState advance() {
        if (completed()) {
            return this;
        }
        if (pageIndex >= script.pages().size() - 1) {
            return script.hasActivity()
                ? new ChapterState(script, pageIndex, ChapterStage.ACTIVITY_READY)
                : new ChapterState(script, pageIndex, ChapterStage.COMPLETED);
        }
        return new ChapterState(script, pageIndex + 1, ChapterStage.DIALOGUE);
    }

    public ChapterState skipDialogue() {
        if (completed() || activityReady()) {
            return this;
        }

        int lastPageIndex = script.pages().size() - 1;
        return script.hasActivity()
            ? new ChapterState(script, lastPageIndex, ChapterStage.ACTIVITY_READY)
            : new ChapterState(script, lastPageIndex, ChapterStage.COMPLETED);
    }

    public ChapterState completeActivity() {
        if (!activityReady()) {
            throw new IllegalStateException("Activity can only be completed from ACTIVITY_READY stage");
        }
        return new ChapterState(script, pageIndex, ChapterStage.COMPLETED);
    }
}
