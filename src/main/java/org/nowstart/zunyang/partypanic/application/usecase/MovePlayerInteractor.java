package org.nowstart.zunyang.partypanic.application.usecase;

import lombok.RequiredArgsConstructor;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerCommand;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerResult;
import org.nowstart.zunyang.partypanic.application.port.in.MovePlayerUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.GameStatePort;
import org.nowstart.zunyang.partypanic.application.service.MovementPolicy;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.model.Player;
import org.nowstart.zunyang.partypanic.domain.model.Position;

@RequiredArgsConstructor
public final class MovePlayerInteractor implements MovePlayerUseCase {
    private final GameStatePort gameStatePort;
    private final MovementPolicy movementPolicy;

    @Override
    public MovePlayerResult move(MovePlayerCommand command) {
        GameState state = gameStatePort.load();
        Player player = state.player().face(command.direction());
        Position targetPosition = player.position().translate(command.direction());
        boolean moved = command.attemptMove() && movementPolicy.canMove(state.gameMap(), targetPosition);
        Player updatedPlayer = moved ? player.moveTo(targetPosition) : player;
        GameState updatedState = state.withPlayer(updatedPlayer);
        gameStatePort.save(updatedState);
        return new MovePlayerResult(updatedState, moved);
    }
}
