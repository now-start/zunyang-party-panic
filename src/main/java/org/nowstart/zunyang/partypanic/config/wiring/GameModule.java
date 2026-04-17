package org.nowstart.zunyang.partypanic.config.wiring;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.nowstart.zunyang.partypanic.adapter.in.chapter.screen.ChapterPreviewScreen;
import org.nowstart.zunyang.partypanic.adapter.in.centerpiece.screen.CenterpieceTableScreen;
import org.nowstart.zunyang.partypanic.adapter.in.finale.screen.FinaleStageScreen;
import org.nowstart.zunyang.partypanic.adapter.in.handover.screen.HandoverCorridorScreen;
import org.nowstart.zunyang.partypanic.adapter.in.hub.screen.HubPreviewScreen;
import org.nowstart.zunyang.partypanic.adapter.in.message.screen.MessageWallScreen;
import org.nowstart.zunyang.partypanic.adapter.in.photo.screen.PhotoBayScreen;
import org.nowstart.zunyang.partypanic.adapter.in.props.screen.PropsArchiveScreen;
import org.nowstart.zunyang.partypanic.adapter.in.signal.screen.SignalConsoleScreen;
import org.nowstart.zunyang.partypanic.adapter.out.content.ResourceChapterScriptAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.content.ResourceGridActivityLayoutAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.content.ResourceHubLayoutAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.ActivityEndingSignalsAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryCenterpieceTableStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryFinaleStageStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryHandoverCorridorStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryMessageWallStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryPhotoBayStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemoryPropsArchiveStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.InMemorySignalConsoleStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.RuntimeChapterStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.RuntimeHubStateAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.RuntimeSessionSnapshotAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.save.SessionPreferencesSchemaManager;
import org.nowstart.zunyang.partypanic.application.chapter.AdvanceChapterInteractor;
import org.nowstart.zunyang.partypanic.application.chapter.CompleteChapterInteractor;
import org.nowstart.zunyang.partypanic.application.chapter.SkipChapterInteractor;
import org.nowstart.zunyang.partypanic.application.chapter.StartChapterInteractor;
import org.nowstart.zunyang.partypanic.application.centerpiece.InspectCenterpiecePlacementInteractor;
import org.nowstart.zunyang.partypanic.application.centerpiece.MoveCenterpieceActorInteractor;
import org.nowstart.zunyang.partypanic.application.centerpiece.StartCenterpieceTableInteractor;
import org.nowstart.zunyang.partypanic.application.finale.InspectFinaleCheckpointInteractor;
import org.nowstart.zunyang.partypanic.application.finale.MoveFinaleActorInteractor;
import org.nowstart.zunyang.partypanic.application.finale.StartFinaleStageInteractor;
import org.nowstart.zunyang.partypanic.application.handover.InspectHandoverClueInteractor;
import org.nowstart.zunyang.partypanic.application.handover.MoveHandoverActorInteractor;
import org.nowstart.zunyang.partypanic.application.handover.StartHandoverCorridorInteractor;
import org.nowstart.zunyang.partypanic.application.message.InspectMessageNoteInteractor;
import org.nowstart.zunyang.partypanic.application.message.MoveMessageActorInteractor;
import org.nowstart.zunyang.partypanic.application.message.StartMessageWallInteractor;
import org.nowstart.zunyang.partypanic.application.port.in.RestartSessionUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.SkipChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartGameUseCase;
import org.nowstart.zunyang.partypanic.application.photo.InspectPhotoFocusInteractor;
import org.nowstart.zunyang.partypanic.application.photo.MovePhotoActorInteractor;
import org.nowstart.zunyang.partypanic.application.photo.StartPhotoBayInteractor;
import org.nowstart.zunyang.partypanic.application.props.InspectPropsItemInteractor;
import org.nowstart.zunyang.partypanic.application.props.MovePropsActorInteractor;
import org.nowstart.zunyang.partypanic.application.props.StartPropsArchiveInteractor;
import org.nowstart.zunyang.partypanic.application.session.RestartSessionInteractor;
import org.nowstart.zunyang.partypanic.application.session.StartGameInteractor;
import org.nowstart.zunyang.partypanic.application.signal.AdjustSignalSettingInteractor;
import org.nowstart.zunyang.partypanic.application.signal.InspectSignalControlInteractor;
import org.nowstart.zunyang.partypanic.application.hub.InteractHubInteractor;
import org.nowstart.zunyang.partypanic.application.hub.LoadHubInteractor;
import org.nowstart.zunyang.partypanic.application.hub.MoveHubActorInteractor;
import org.nowstart.zunyang.partypanic.application.signal.MoveSignalActorInteractor;
import org.nowstart.zunyang.partypanic.application.signal.StartSignalConsoleInteractor;
import org.nowstart.zunyang.partypanic.config.bootstrap.PartyPanicGame;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpiecePlacementId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleCheckpointId;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverClueId;
import org.nowstart.zunyang.partypanic.domain.message.MessageNoteId;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoFocusId;
import org.nowstart.zunyang.partypanic.domain.props.PropsItemId;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;

public final class GameModule {

    private static final String SESSION_PREFERENCES_NAME = "zunyang-party-panic-session";

    private final ResourceHubLayoutAdapter resourceHubLayoutAdapter = new ResourceHubLayoutAdapter();
    private final ResourceChapterScriptAdapter resourceChapterScriptAdapter = new ResourceChapterScriptAdapter();
    private final ResourceGridActivityLayoutAdapter<SignalControlId> signalConsoleLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/signal-console.json", SignalControlId.class);
    private final ResourceGridActivityLayoutAdapter<PropsItemId> propsArchiveLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/props-archive.json", PropsItemId.class);
    private final ResourceGridActivityLayoutAdapter<CenterpiecePlacementId> centerpieceTableLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/centerpiece-table.json", CenterpiecePlacementId.class);
    private final ResourceGridActivityLayoutAdapter<PhotoFocusId> photoBayLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/photo-bay.json", PhotoFocusId.class);
    private final ResourceGridActivityLayoutAdapter<HandoverClueId> handoverCorridorLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/handover-corridor.json", HandoverClueId.class);
    private final ResourceGridActivityLayoutAdapter<MessageNoteId> messageWallLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/message-wall.json", MessageNoteId.class);
    private final ResourceGridActivityLayoutAdapter<FinaleCheckpointId> finaleStageLayoutAdapter =
        new ResourceGridActivityLayoutAdapter<>("content/layouts/finale-stage.json", FinaleCheckpointId.class);
    private final RuntimeHubStateAdapter runtimeHubStateAdapter = new RuntimeHubStateAdapter();
    private final RuntimeChapterStateAdapter runtimeChapterStateAdapter = new RuntimeChapterStateAdapter();
    private final RuntimeSessionSnapshotAdapter runtimeSessionSnapshotAdapter = new RuntimeSessionSnapshotAdapter();
    private final SessionPreferencesSchemaManager sessionPreferencesSchemaManager =
        new SessionPreferencesSchemaManager();
    private final InMemorySignalConsoleStateAdapter inMemorySignalConsoleStateAdapter = new InMemorySignalConsoleStateAdapter();
    private final InMemoryPropsArchiveStateAdapter inMemoryPropsArchiveStateAdapter = new InMemoryPropsArchiveStateAdapter();
    private final InMemoryCenterpieceTableStateAdapter inMemoryCenterpieceTableStateAdapter =
        new InMemoryCenterpieceTableStateAdapter();
    private final InMemoryPhotoBayStateAdapter inMemoryPhotoBayStateAdapter =
        new InMemoryPhotoBayStateAdapter();
    private final InMemoryHandoverCorridorStateAdapter inMemoryHandoverCorridorStateAdapter =
        new InMemoryHandoverCorridorStateAdapter();
    private final InMemoryMessageWallStateAdapter inMemoryMessageWallStateAdapter =
        new InMemoryMessageWallStateAdapter();
    private final InMemoryFinaleStageStateAdapter inMemoryFinaleStageStateAdapter =
        new InMemoryFinaleStageStateAdapter();
    private final ActivityEndingSignalsAdapter activityEndingSignalsAdapter = new ActivityEndingSignalsAdapter(
        inMemorySignalConsoleStateAdapter,
        inMemoryPropsArchiveStateAdapter,
        inMemoryCenterpieceTableStateAdapter,
        inMemoryPhotoBayStateAdapter,
        inMemoryHandoverCorridorStateAdapter,
        inMemoryMessageWallStateAdapter,
        inMemoryFinaleStageStateAdapter
    );
    private final LoadHubInteractor loadHubInteractor = new LoadHubInteractor(
        resourceHubLayoutAdapter,
        runtimeHubStateAdapter,
        runtimeHubStateAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final MoveHubActorInteractor moveHubActorInteractor = new MoveHubActorInteractor(
        resourceHubLayoutAdapter,
        runtimeHubStateAdapter,
        runtimeHubStateAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final StartChapterInteractor startChapterInteractor = new StartChapterInteractor(
        resourceChapterScriptAdapter,
        runtimeChapterStateAdapter,
        runtimeChapterStateAdapter
    );
    private final AdvanceChapterInteractor advanceChapterInteractor = new AdvanceChapterInteractor(
        runtimeChapterStateAdapter,
        runtimeChapterStateAdapter,
        runtimeSessionSnapshotAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final SkipChapterUseCase skipChapterUseCase = new SkipChapterInteractor(
        runtimeChapterStateAdapter,
        runtimeChapterStateAdapter,
        runtimeSessionSnapshotAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final CompleteChapterInteractor completeChapterInteractor = new CompleteChapterInteractor(
        runtimeChapterStateAdapter,
        runtimeChapterStateAdapter,
        runtimeSessionSnapshotAdapter,
        runtimeSessionSnapshotAdapter,
        activityEndingSignalsAdapter
    );
    private final InteractHubInteractor interactHubInteractor = new InteractHubInteractor(
        resourceHubLayoutAdapter,
        runtimeHubStateAdapter,
        runtimeHubStateAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final StartGameUseCase startGameUseCase = new StartGameInteractor(
        runtimeSessionSnapshotAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final RestartSessionUseCase restartSessionUseCase = new RestartSessionInteractor(
        resourceHubLayoutAdapter,
        runtimeHubStateAdapter,
        runtimeChapterStateAdapter,
        runtimeSessionSnapshotAdapter
    );
    private final StartSignalConsoleInteractor startSignalConsoleInteractor = new StartSignalConsoleInteractor(
        signalConsoleLayoutAdapter,
        inMemorySignalConsoleStateAdapter
    );
    private final MoveSignalActorInteractor moveSignalActorInteractor = new MoveSignalActorInteractor(
        inMemorySignalConsoleStateAdapter,
        inMemorySignalConsoleStateAdapter
    );
    private final InspectSignalControlInteractor inspectSignalControlInteractor = new InspectSignalControlInteractor(
        inMemorySignalConsoleStateAdapter,
        inMemorySignalConsoleStateAdapter
    );
    private final AdjustSignalSettingInteractor adjustSignalSettingInteractor = new AdjustSignalSettingInteractor(
        inMemorySignalConsoleStateAdapter,
        inMemorySignalConsoleStateAdapter
    );
    private final StartPropsArchiveInteractor startPropsArchiveInteractor = new StartPropsArchiveInteractor(
        propsArchiveLayoutAdapter,
        inMemoryPropsArchiveStateAdapter
    );
    private final MovePropsActorInteractor movePropsActorInteractor = new MovePropsActorInteractor(
        inMemoryPropsArchiveStateAdapter,
        inMemoryPropsArchiveStateAdapter
    );
    private final InspectPropsItemInteractor inspectPropsItemInteractor = new InspectPropsItemInteractor(
        inMemoryPropsArchiveStateAdapter,
        inMemoryPropsArchiveStateAdapter
    );
    private final StartCenterpieceTableInteractor startCenterpieceTableInteractor = new StartCenterpieceTableInteractor(
        centerpieceTableLayoutAdapter,
        inMemoryCenterpieceTableStateAdapter
    );
    private final MoveCenterpieceActorInteractor moveCenterpieceActorInteractor = new MoveCenterpieceActorInteractor(
        inMemoryCenterpieceTableStateAdapter,
        inMemoryCenterpieceTableStateAdapter
    );
    private final InspectCenterpiecePlacementInteractor inspectCenterpiecePlacementInteractor =
        new InspectCenterpiecePlacementInteractor(
            inMemoryCenterpieceTableStateAdapter,
            inMemoryCenterpieceTableStateAdapter
        );
    private final StartPhotoBayInteractor startPhotoBayInteractor = new StartPhotoBayInteractor(
        photoBayLayoutAdapter,
        inMemoryPhotoBayStateAdapter
    );
    private final MovePhotoActorInteractor movePhotoActorInteractor = new MovePhotoActorInteractor(
        inMemoryPhotoBayStateAdapter,
        inMemoryPhotoBayStateAdapter
    );
    private final InspectPhotoFocusInteractor inspectPhotoFocusInteractor = new InspectPhotoFocusInteractor(
        inMemoryPhotoBayStateAdapter,
        inMemoryPhotoBayStateAdapter
    );
    private final StartHandoverCorridorInteractor startHandoverCorridorInteractor =
        new StartHandoverCorridorInteractor(handoverCorridorLayoutAdapter, inMemoryHandoverCorridorStateAdapter);
    private final MoveHandoverActorInteractor moveHandoverActorInteractor = new MoveHandoverActorInteractor(
        inMemoryHandoverCorridorStateAdapter,
        inMemoryHandoverCorridorStateAdapter
    );
    private final InspectHandoverClueInteractor inspectHandoverClueInteractor =
        new InspectHandoverClueInteractor(
            inMemoryHandoverCorridorStateAdapter,
            inMemoryHandoverCorridorStateAdapter
        );
    private final StartMessageWallInteractor startMessageWallInteractor =
        new StartMessageWallInteractor(messageWallLayoutAdapter, inMemoryMessageWallStateAdapter);
    private final MoveMessageActorInteractor moveMessageActorInteractor = new MoveMessageActorInteractor(
        inMemoryMessageWallStateAdapter,
        inMemoryMessageWallStateAdapter
    );
    private final InspectMessageNoteInteractor inspectMessageNoteInteractor =
        new InspectMessageNoteInteractor(
            inMemoryMessageWallStateAdapter,
            inMemoryMessageWallStateAdapter
        );
    private final StartFinaleStageInteractor startFinaleStageInteractor =
        new StartFinaleStageInteractor(finaleStageLayoutAdapter, inMemoryFinaleStageStateAdapter);
    private final MoveFinaleActorInteractor moveFinaleActorInteractor = new MoveFinaleActorInteractor(
        inMemoryFinaleStageStateAdapter,
        inMemoryFinaleStageStateAdapter
    );
    private final InspectFinaleCheckpointInteractor inspectFinaleCheckpointInteractor =
        new InspectFinaleCheckpointInteractor(
            inMemoryFinaleStageStateAdapter,
            inMemoryFinaleStageStateAdapter
        );

    public PartyPanicGame createGame() {
        return new PartyPanicGame(this);
    }

    public void startGame() {
        if (Gdx.app != null) {
            Preferences preferences = Gdx.app.getPreferences(SESSION_PREFERENCES_NAME);
            sessionPreferencesSchemaManager.ensureCurrentSchema(preferences);
            runtimeSessionSnapshotAdapter.bindToPreferences(preferences);
            runtimeHubStateAdapter.bindToPreferences(preferences, resourceHubLayoutAdapter);
            runtimeChapterStateAdapter.bindToPreferences(preferences, resourceChapterScriptAdapter);
        }
        startGameUseCase.start();
    }

    public void restartSession() {
        restartSessionUseCase.restart();
    }

    public HubPreviewScreen createHubScreen(PartyPanicGame game) {
        return new HubPreviewScreen(
            loadHubInteractor,
            moveHubActorInteractor,
            interactHubInteractor,
            game::openChapter,
            game::restartSession
        );
    }

    public ChapterPreviewScreen createChapterScreen(PartyPanicGame game, ChapterId chapterId) {
        return new ChapterPreviewScreen(
            chapterId,
            startChapterInteractor,
            advanceChapterInteractor,
            skipChapterUseCase,
            activityType -> openChapterActivity(game, activityType),
            game::openHub,
            game::restartSession
        );
    }

    public SignalConsoleScreen createSignalConsoleScreen(PartyPanicGame game) {
        return new SignalConsoleScreen(
            startSignalConsoleInteractor,
            moveSignalActorInteractor,
            inspectSignalControlInteractor,
            adjustSignalSettingInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public PropsArchiveScreen createPropsArchiveScreen(PartyPanicGame game) {
        return new PropsArchiveScreen(
            startPropsArchiveInteractor,
            movePropsActorInteractor,
            inspectPropsItemInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public CenterpieceTableScreen createCenterpieceTableScreen(PartyPanicGame game) {
        return new CenterpieceTableScreen(
            startCenterpieceTableInteractor,
            moveCenterpieceActorInteractor,
            inspectCenterpiecePlacementInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public PhotoBayScreen createPhotoBayScreen(PartyPanicGame game) {
        return new PhotoBayScreen(
            startPhotoBayInteractor,
            movePhotoActorInteractor,
            inspectPhotoFocusInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public HandoverCorridorScreen createHandoverCorridorScreen(PartyPanicGame game) {
        return new HandoverCorridorScreen(
            startHandoverCorridorInteractor,
            moveHandoverActorInteractor,
            inspectHandoverClueInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public MessageWallScreen createMessageWallScreen(PartyPanicGame game) {
        return new MessageWallScreen(
            startMessageWallInteractor,
            moveMessageActorInteractor,
            inspectMessageNoteInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    public FinaleStageScreen createFinaleStageScreen(PartyPanicGame game) {
        return new FinaleStageScreen(
            startFinaleStageInteractor,
            moveFinaleActorInteractor,
            inspectFinaleCheckpointInteractor,
            completeChapterInteractor,
            game::openHub
        );
    }

    private void openChapterActivity(PartyPanicGame game, String activityType) {
        if ("SIGNAL_CONSOLE".equals(activityType)) {
            game.openSignalConsole();
            return;
        }
        if ("PROPS_ARCHIVE".equals(activityType)) {
            game.openPropsArchive();
            return;
        }
        if ("CENTERPIECE_TABLE".equals(activityType)) {
            game.openCenterpieceTable();
            return;
        }
        if ("PHOTO_BAY".equals(activityType)) {
            game.openPhotoBay();
            return;
        }
        if ("HANDOVER_CORRIDOR".equals(activityType)) {
            game.openHandoverCorridor();
            return;
        }
        if ("MESSAGE_WALL".equals(activityType)) {
            game.openMessageWall();
            return;
        }
        if ("FINALE_STAGE".equals(activityType)) {
            game.openFinaleStage();
            return;
        }
        throw new IllegalArgumentException("Unsupported activity type: " + activityType);
    }
}
