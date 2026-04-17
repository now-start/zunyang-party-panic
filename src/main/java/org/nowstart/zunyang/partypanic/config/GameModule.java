package org.nowstart.zunyang.partypanic.config;

import org.nowstart.zunyang.partypanic.adapter.out.map.StaticHubMapAdapter;
import org.nowstart.zunyang.partypanic.adapter.out.state.InMemoryGameStateAdapter;
import org.nowstart.zunyang.partypanic.application.port.in.AdvanceDialogueUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InteractUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePlayerUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMapPort;
import org.nowstart.zunyang.partypanic.application.usecase.AdvanceDialogueInteractor;
import org.nowstart.zunyang.partypanic.application.usecase.InteractInteractor;
import org.nowstart.zunyang.partypanic.application.usecase.MovePlayerInteractor;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.policy.EventResolver;
import org.nowstart.zunyang.partypanic.domain.policy.MovementPolicy;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

public final class GameModule {
    private final LoadMapPort loadMapPort = new StaticHubMapAdapter();

    public HubContext createHubContext(GameProgress progress) {
        GameState initialState = GameState.initial(loadMapPort.loadMap());
        InMemoryGameStateAdapter gameStatePort = new InMemoryGameStateAdapter(initialState);
        EventResolver eventResolver = new EventResolver();

        MovePlayerUseCase movePlayerUseCase = new MovePlayerInteractor(gameStatePort, new MovementPolicy());
        InteractUseCase interactUseCase = new InteractInteractor(gameStatePort, eventResolver, progress);
        AdvanceDialogueUseCase advanceDialogueUseCase = new AdvanceDialogueInteractor(gameStatePort);

        return new HubContext(initialState, eventResolver, movePlayerUseCase, interactUseCase, advanceDialogueUseCase);
    }

    public record HubContext(
            GameState initialState,
            EventResolver eventResolver,
            MovePlayerUseCase movePlayerUseCase,
            InteractUseCase interactUseCase,
            AdvanceDialogueUseCase advanceDialogueUseCase
    ) {
    }
}
