package org.nowstart.zunyang.partypanic.application.smoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.adapter.out.content.PreviewHubLayoutAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.content.ResourceChapterScriptAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryChapterStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryHubStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryRunProgressAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemorySignalConsoleStateAdapter;
import org.nowstart.zunyang.partypanic.application.chapter.AdvanceChapterInteractor;
import org.nowstart.zunyang.partypanic.application.chapter.CompleteChapterInteractor;
import org.nowstart.zunyang.partypanic.application.chapter.StartChapterInteractor;
import org.nowstart.zunyang.partypanic.application.dto.command.AdjustSignalSettingCommand;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHubActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveSignalActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.HubHotspotView;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.hub.InteractHubInteractor;
import org.nowstart.zunyang.partypanic.application.hub.LoadHubInteractor;
import org.nowstart.zunyang.partypanic.application.hub.MoveHubActorInteractor;
import org.nowstart.zunyang.partypanic.application.session.StartGameInteractor;
import org.nowstart.zunyang.partypanic.application.signal.AdjustSignalSettingInteractor;
import org.nowstart.zunyang.partypanic.application.signal.InspectSignalControlInteractor;
import org.nowstart.zunyang.partypanic.application.signal.MoveSignalActorInteractor;
import org.nowstart.zunyang.partypanic.application.signal.StartSignalConsoleInteractor;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.session.EndingSignals;

class PreviewFlowSmokeTest {

    @Test
    void first_hub_slice_runs_from_start_to_next_unlock_using_authored_content() {
        PreviewHubLayoutAdapter hubLayoutAdapter = new PreviewHubLayoutAdapter();
        ResourceChapterScriptAdapter chapterScriptAdapter = new ResourceChapterScriptAdapter();
        InMemoryHubStateAdapter hubStateAdapter = new InMemoryHubStateAdapter();
        InMemoryChapterStateAdapter chapterStateAdapter = new InMemoryChapterStateAdapter();
        InMemoryRunProgressAdapter runProgressAdapter = new InMemoryRunProgressAdapter();
        InMemorySignalConsoleStateAdapter signalStateAdapter = new InMemorySignalConsoleStateAdapter();

        StartGameInteractor startGameInteractor = new StartGameInteractor(runProgressAdapter, runProgressAdapter);
        LoadHubInteractor loadHubInteractor = new LoadHubInteractor(
            hubLayoutAdapter,
            hubStateAdapter,
            hubStateAdapter,
            runProgressAdapter
        );
        MoveHubActorInteractor moveHubActorInteractor = new MoveHubActorInteractor(
            hubLayoutAdapter,
            hubStateAdapter,
            hubStateAdapter,
            runProgressAdapter
        );
        InteractHubInteractor interactHubInteractor = new InteractHubInteractor(
            hubLayoutAdapter,
            hubStateAdapter,
            hubStateAdapter,
            runProgressAdapter
        );
        StartChapterInteractor startChapterInteractor = new StartChapterInteractor(
            chapterScriptAdapter,
            chapterStateAdapter
        );
        AdvanceChapterInteractor advanceChapterInteractor = new AdvanceChapterInteractor(
            chapterStateAdapter,
            chapterStateAdapter,
            runProgressAdapter,
            runProgressAdapter
        );
        CompleteChapterInteractor completeChapterInteractor = new CompleteChapterInteractor(
            chapterStateAdapter,
            chapterStateAdapter,
            runProgressAdapter,
            runProgressAdapter,
            () -> new EndingSignals(0, 0, 0)
        );
        StartSignalConsoleInteractor startSignalConsoleInteractor = new StartSignalConsoleInteractor(signalStateAdapter);
        MoveSignalActorInteractor moveSignalActorInteractor = new MoveSignalActorInteractor(
            signalStateAdapter,
            signalStateAdapter
        );
        InspectSignalControlInteractor inspectSignalControlInteractor = new InspectSignalControlInteractor(
            signalStateAdapter,
            signalStateAdapter
        );
        AdjustSignalSettingInteractor adjustSignalSettingInteractor = new AdjustSignalSettingInteractor(
            signalStateAdapter,
            signalStateAdapter
        );

        startGameInteractor.start();
        HubViewResult hubView = loadHubInteractor.load();
        assertEquals("PREP_CALL", hubView.sessionPhase());

        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.LEFT));
        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.LEFT));
        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.UP));
        hubView = interactHubInteractor.interact();

        assertEquals("SIGNAL", hubView.activeHotspotId());
        assertTrue(findHotspot(hubView, ChapterId.SIGNAL).unlocked());

        ChapterViewResult chapterView = startChapterInteractor.start(ChapterId.SIGNAL);
        assertEquals("SIGNAL", chapterView.chapterId());

        chapterView = advanceChapterInteractor.advance();
        chapterView = advanceChapterInteractor.advance();
        chapterView = advanceChapterInteractor.advance();
        assertTrue(chapterView.activityReady());
        assertEquals("SIGNAL_CONSOLE", chapterView.activityType());

        SignalConsoleViewResult signalView = startSignalConsoleInteractor.start();
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.UP));
        signalView = inspectSignalControlInteractor.inspect();
        signalView = adjustSignalSettingInteractor.adjust(new AdjustSignalSettingCommand(1));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.UP));
        signalView = inspectSignalControlInteractor.inspect();
        signalView = adjustSignalSettingInteractor.adjust(new AdjustSignalSettingCommand(-1));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.DOWN));
        signalView = inspectSignalControlInteractor.inspect();
        signalView = adjustSignalSettingInteractor.adjust(new AdjustSignalSettingCommand(1));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        signalView = moveSignalActorInteractor.move(new MoveSignalActorCommand(Direction.DOWN));
        signalView = inspectSignalControlInteractor.inspect();
        signalView = adjustSignalSettingInteractor.adjust(new AdjustSignalSettingCommand(-1));

        assertTrue(signalView.stabilized());

        ChapterViewResult completedChapter = completeChapterInteractor.complete();
        assertTrue(completedChapter.completed());
        assertTrue(runProgressAdapter.load().orElseThrow().isCompleted(ChapterId.SIGNAL));

        hubView = loadHubInteractor.load();
        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.RIGHT));
        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.RIGHT));
        hubView = moveHubActorInteractor.move(new MoveHubActorCommand(Direction.UP));
        hubView = interactHubInteractor.interact();

        assertEquals("PROPS", hubView.activeHotspotId());
        assertTrue(findHotspot(hubView, ChapterId.PROPS).unlocked());
    }

    private static HubHotspotView findHotspot(HubViewResult hubView, ChapterId chapterId) {
        return hubView.hotspots().stream()
            .filter(hotspot -> hotspot.id().equals(chapterId.name()))
            .findFirst()
            .orElseThrow();
    }
}
