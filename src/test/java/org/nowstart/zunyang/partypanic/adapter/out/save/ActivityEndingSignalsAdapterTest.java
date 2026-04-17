package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;
import org.nowstart.zunyang.partypanic.domain.session.EndingSignals;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

class ActivityEndingSignalsAdapterTest {

    @Test
    void aggregates_optional_reviews_and_signal_inspection_into_scores() {
        InMemorySignalConsoleStateAdapter signalAdapter = new InMemorySignalConsoleStateAdapter();
        InMemoryPropsArchiveStateAdapter propsAdapter = new InMemoryPropsArchiveStateAdapter();
        InMemoryCenterpieceTableStateAdapter centerpieceAdapter = new InMemoryCenterpieceTableStateAdapter();
        InMemoryPhotoBayStateAdapter photoAdapter = new InMemoryPhotoBayStateAdapter();
        InMemoryHandoverCorridorStateAdapter handoverAdapter = new InMemoryHandoverCorridorStateAdapter();
        InMemoryMessageWallStateAdapter messageAdapter = new InMemoryMessageWallStateAdapter();
        InMemoryFinaleStageStateAdapter finaleAdapter = new InMemoryFinaleStageStateAdapter();

        signalAdapter.save(allControlsInspected());
        propsAdapter.save(PropsArchiveState.initial().move(Direction.LEFT).move(Direction.LEFT).move(Direction.DOWN).inspect());
        centerpieceAdapter.save(CenterpieceTableState.initial().move(Direction.DOWN).inspect());
        photoAdapter.save(PhotoBayState.initial().move(Direction.DOWN).inspect());
        handoverAdapter.save(
            HandoverCorridorState.initial()
                .move(Direction.LEFT)
                .move(Direction.LEFT)
                .move(Direction.DOWN)
                .inspect()
                .move(Direction.RIGHT)
                .move(Direction.RIGHT)
                .move(Direction.DOWN)
                .inspect()
        );
        messageAdapter.save(
            MessageWallState.initial()
                .move(Direction.LEFT)
                .move(Direction.LEFT)
                .move(Direction.DOWN)
                .inspect()
                .move(Direction.RIGHT)
                .move(Direction.RIGHT)
                .move(Direction.DOWN)
                .inspect()
        );
        finaleAdapter.save(FinaleStageState.initial().move(Direction.LEFT).move(Direction.LEFT).move(Direction.DOWN).inspect());

        ActivityEndingSignalsAdapter adapter = new ActivityEndingSignalsAdapter(
            signalAdapter,
            propsAdapter,
            centerpieceAdapter,
            photoAdapter,
            handoverAdapter,
            messageAdapter,
            finaleAdapter
        );

        EndingSignals signals = adapter.load();

        assertEquals(5, signals.setupCareScore());
        assertEquals(2, signals.archiveDepthScore());
        assertEquals(2, signals.messageWarmthScore());
    }

    private static SignalConsoleState allControlsInspected() {
        SignalConsoleState state = SignalConsoleState.initial();
        state = state.move(Direction.LEFT).move(Direction.UP).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.UP).inspect();
        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.DOWN).inspect();
        return state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.DOWN).inspect();
    }
}
