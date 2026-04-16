package org.nowstart.zunyang.partypanic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.nowstart.zunyang.partypanic.screen.CakeTableScreen;
import org.nowstart.zunyang.partypanic.screen.HubScreen;
import org.nowstart.zunyang.partypanic.screen.PartyPanicScreen;
import org.nowstart.zunyang.partypanic.screen.PhotoTimeScreen;
import org.nowstart.zunyang.partypanic.screen.TitleScreen;
import org.nowstart.zunyang.partypanic.world.GameProgress;

public final class PartyPanicGame extends Game {
    private final GameProgress progress = new GameProgress();

    @Override
    public void create() {
        showTitle();
    }

    public void showTitle() {
        switchTo(new TitleScreen(this));
    }

    public void showHub() {
        showHub(null);
    }

    public void showHub(String notice) {
        switchTo(new HubScreen(this, progress, notice));
    }

    public void showBroadcastDeskMinigame() {
        switchTo(new PartyPanicScreen(this, progress));
    }

    public void finishBroadcastDeskMinigame(int score) {
        progress.recordScore(GameProgress.BROADCAST_DESK, score);
        showHub("방송 책상 준비 완료. 최고 점수 " + progress.getBestScore(GameProgress.BROADCAST_DESK) + "점");
    }

    public void showCakeTableMinigame() {
        switchTo(new CakeTableScreen(this, progress));
    }

    public void finishCakeTableMinigame(int score) {
        progress.recordScore(GameProgress.CAKE_TABLE, score);
        showHub("케이크 테이블 정리 완료. 최고 점수 " + progress.getBestScore(GameProgress.CAKE_TABLE) + "점");
    }

    public void showPhotoTimeMinigame() {
        switchTo(new PhotoTimeScreen(this, progress));
    }

    public void finishPhotoTimeMinigame(int score) {
        progress.recordScore(GameProgress.PHOTO_TIME, score);
        showHub("포토존 촬영 완료. 최고 점수 " + progress.getBestScore(GameProgress.PHOTO_TIME) + "점");
    }

    public GameProgress getProgress() {
        return progress;
    }

    private void switchTo(Screen nextScreen) {
        Screen currentScreen = getScreen();
        setScreen(nextScreen);
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
