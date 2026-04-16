package org.nowstart.zunyang.partypanic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.nowstart.zunyang.partypanic.application.port.GameNavigator;
import org.nowstart.zunyang.partypanic.application.story.StoryChapterFactory;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.infrastructure.config.GameConfig;
import org.nowstart.zunyang.partypanic.infrastructure.config.GameConfigLoader;
import org.nowstart.zunyang.partypanic.presentation.screen.CakeTableScreen;
import org.nowstart.zunyang.partypanic.presentation.screen.HubScreen;
import org.nowstart.zunyang.partypanic.presentation.screen.PartyPanicScreen;
import org.nowstart.zunyang.partypanic.presentation.screen.PhotoTimeScreen;
import org.nowstart.zunyang.partypanic.presentation.screen.StorySequenceScreen;
import org.nowstart.zunyang.partypanic.presentation.screen.TitleScreen;

public final class PartyPanicGame extends Game implements GameNavigator {
    private final GameConfig config;
    private final GameProgress progress = new GameProgress();
    private final StoryChapterFactory storyChapterFactory = new StoryChapterFactory();

    public PartyPanicGame() {
        this(GameConfigLoader.load());
    }

    public PartyPanicGame(GameConfig config) {
        this.config = config;
    }

    @Override
    public void create() {
        showTitle();
    }

    @Override
    public boolean showsOperationalUi() {
        return config.showsOperationalUi();
    }

    @Override
    public void showTitle() {
        switchTo(new TitleScreen(this));
    }

    @Override
    public void showHub(String notice) {
        switchTo(new HubScreen(this, progress, notice));
    }

    @Override
    public void openActivity(ActivityId activityId) {
        switch (activityId) {
            case BROADCAST_DESK -> switchTo(new PartyPanicScreen(this, progress));
            case CAKE_TABLE -> switchTo(new CakeTableScreen(this, progress));
            case PHOTO_TIME -> switchTo(new PhotoTimeScreen(this, progress));
            case STORAGE_ROOM, BACKSTAGE, FAN_LETTER, FINALE_STAGE ->
                    switchTo(new StorySequenceScreen(this, progress, storyChapterFactory.create(activityId, progress)));
        }
    }

    @Override
    public void completeScoredActivity(ActivityId activityId, int score) {
        progress.recordScore(activityId, score);
        showHub(resolveScoredCompletionNotice(activityId));
    }

    @Override
    public void completeStoryActivity(ActivityId activityId, String notice) {
        progress.markCompleted(activityId);
        showHub(notice);
    }

    public GameProgress getProgress() {
        return progress;
    }

    public GameConfig getConfig() {
        return config;
    }

    private String resolveScoredCompletionNotice(ActivityId activityId) {
        return switch (activityId) {
            case BROADCAST_DESK ->
                    "방송 책상 정리 완료. 램프가 켜졌습니다. 최고 점수 " + progress.getBestScore(ActivityId.BROADCAST_DESK) + "점";
            case CAKE_TABLE ->
                    "케이크 테이블 정리 완료. 오늘의 중심 장면이 잡혔습니다. 최고 점수 " + progress.getBestScore(ActivityId.CAKE_TABLE) + "점";
            case PHOTO_TIME ->
                    "포토존 촬영 완료. 남길 장면이 생겼습니다. 최고 점수 " + progress.getBestScore(ActivityId.PHOTO_TIME) + "점";
            default -> throw new IllegalArgumentException("Scored activity not supported: " + activityId);
        };
    }

    private void switchTo(Screen nextScreen) {
        Screen currentScreen = getScreen();
        setScreen(nextScreen);
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
