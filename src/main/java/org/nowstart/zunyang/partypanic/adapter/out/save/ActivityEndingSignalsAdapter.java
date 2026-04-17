package org.nowstart.zunyang.partypanic.adapter.out.save;

import org.nowstart.zunyang.partypanic.application.port.out.LoadCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadEndingSignalsPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMessageWallStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPhotoBayStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;
import org.nowstart.zunyang.partypanic.domain.session.EndingSignals;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class ActivityEndingSignalsAdapter implements LoadEndingSignalsPort {

    private final LoadSignalConsoleStatePort loadSignalConsoleStatePort;
    private final LoadPropsArchiveStatePort loadPropsArchiveStatePort;
    private final LoadCenterpieceTableStatePort loadCenterpieceTableStatePort;
    private final LoadPhotoBayStatePort loadPhotoBayStatePort;
    private final LoadHandoverCorridorStatePort loadHandoverCorridorStatePort;
    private final LoadMessageWallStatePort loadMessageWallStatePort;
    private final LoadFinaleStageStatePort loadFinaleStageStatePort;

    public ActivityEndingSignalsAdapter(
        LoadSignalConsoleStatePort loadSignalConsoleStatePort,
        LoadPropsArchiveStatePort loadPropsArchiveStatePort,
        LoadCenterpieceTableStatePort loadCenterpieceTableStatePort,
        LoadPhotoBayStatePort loadPhotoBayStatePort,
        LoadHandoverCorridorStatePort loadHandoverCorridorStatePort,
        LoadMessageWallStatePort loadMessageWallStatePort,
        LoadFinaleStageStatePort loadFinaleStageStatePort
    ) {
        this.loadSignalConsoleStatePort = loadSignalConsoleStatePort;
        this.loadPropsArchiveStatePort = loadPropsArchiveStatePort;
        this.loadCenterpieceTableStatePort = loadCenterpieceTableStatePort;
        this.loadPhotoBayStatePort = loadPhotoBayStatePort;
        this.loadHandoverCorridorStatePort = loadHandoverCorridorStatePort;
        this.loadMessageWallStatePort = loadMessageWallStatePort;
        this.loadFinaleStageStatePort = loadFinaleStageStatePort;
    }

    @Override
    public EndingSignals load() {
        SignalConsoleState signalState = loadSignalConsoleStatePort.load().orElseGet(SignalConsoleState::initial);
        PropsArchiveState propsState = loadPropsArchiveStatePort.load().orElseGet(PropsArchiveState::initial);
        CenterpieceTableState centerpieceState = loadCenterpieceTableStatePort.load().orElseGet(CenterpieceTableState::initial);
        PhotoBayState photoState = loadPhotoBayStatePort.load().orElseGet(PhotoBayState::initial);
        HandoverCorridorState handoverState = loadHandoverCorridorStatePort.load().orElseGet(HandoverCorridorState::initial);
        MessageWallState messageState = loadMessageWallStatePort.load().orElseGet(MessageWallState::initial);
        FinaleStageState finaleState = loadFinaleStageStatePort.load().orElseGet(FinaleStageState::initial);

        int setupCareScore = 0;
        if (signalState.inspectedCount() == signalControlCount()) {
            setupCareScore++;
        }
        setupCareScore += hasOptionalReview(propsState.reviewedOptionalCount());
        setupCareScore += hasOptionalReview(centerpieceState.reviewedOptionalCount());
        setupCareScore += hasOptionalReview(photoState.reviewedOptionalCount());
        setupCareScore += hasOptionalReview(finaleState.reviewedOptionalCount());

        return new EndingSignals(
            setupCareScore,
            handoverState.reviewedOptionalCount(),
            messageState.reviewedOptionalCount()
        );
    }

    private static int hasOptionalReview(int reviewedCount) {
        return reviewedCount > 0 ? 1 : 0;
    }

    private static int signalControlCount() {
        return SignalControlId.values().length;
    }
}
