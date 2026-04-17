package org.nowstart.zunyang.partypanic.config.bootstrap;

import com.badlogic.gdx.Game;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.config.wiring.GameModule;

public final class PartyPanicGame extends Game {

    private final GameModule gameModule;

    public PartyPanicGame(GameModule gameModule) {
        this.gameModule = gameModule;
    }

    @Override
    public void create() {
        gameModule.startGame();
        openHub();
    }

    public void openHub() {
        setScreen(gameModule.createHubScreen(this));
    }

    public void openChapter(ChapterId chapterId) {
        setScreen(gameModule.createChapterScreen(this, chapterId));
    }

    public void openSignalConsole() {
        setScreen(gameModule.createSignalConsoleScreen(this));
    }

    public void openPropsArchive() {
        setScreen(gameModule.createPropsArchiveScreen(this));
    }

    public void openCenterpieceTable() {
        setScreen(gameModule.createCenterpieceTableScreen(this));
    }

    public void openPhotoBay() {
        setScreen(gameModule.createPhotoBayScreen(this));
    }

    public void openHandoverCorridor() {
        setScreen(gameModule.createHandoverCorridorScreen(this));
    }

    public void openMessageWall() {
        setScreen(gameModule.createMessageWallScreen(this));
    }

    public void openFinaleStage() {
        setScreen(gameModule.createFinaleStageScreen(this));
    }

    public void restartSession() {
        gameModule.restartSession();
        openHub();
    }
}
