package org.nowstart.zunyang.partypanic.domain.model;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

public record GameState(
        GameMap gameMap,
        Player player,
        Dialogue activeDialogue,
        ActivityId pendingActivityId
) {
    public static GameState initial(GameMap gameMap) {
        return new GameState(
                gameMap,
                new Player(gameMap.startingPosition(), Direction.UP),
                null,
                null
        );
    }

    public GameState withPlayer(Player nextPlayer) {
        return new GameState(gameMap, nextPlayer, activeDialogue, pendingActivityId);
    }

    public GameState startDialogue(Dialogue dialogue, ActivityId nextActivityId) {
        return new GameState(gameMap, player, dialogue, nextActivityId);
    }

    public GameState clearDialogue() {
        return new GameState(gameMap, player, null, null);
    }
}
