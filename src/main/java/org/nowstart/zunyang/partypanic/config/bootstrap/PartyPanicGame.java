package org.nowstart.zunyang.partypanic.config.bootstrap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import lombok.Getter;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.adapter.in.screen.CakeTableScreen;
import org.nowstart.zunyang.partypanic.adapter.in.screen.HubScreen;
import org.nowstart.zunyang.partypanic.adapter.in.screen.PartyPanicScreen;
import org.nowstart.zunyang.partypanic.adapter.in.screen.PhotoTimeScreen;
import org.nowstart.zunyang.partypanic.adapter.in.screen.StorySequenceScreen;
import org.nowstart.zunyang.partypanic.adapter.in.screen.TitleScreen;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.config.GameConfig;
import org.nowstart.zunyang.partypanic.config.GameModule;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.domain.story.StoryChapterFactory;

public final class PartyPanicGame extends Game implements GameNavigator {
    @Getter
    private final GameProgress progress = new GameProgress();
    private final boolean operationalUi;
    private final StoryChapterFactory storyChapterFactory = new StoryChapterFactory();
    private final GameModule gameModule = new GameModule();
    private GameAssets assets;

    public PartyPanicGame(GameConfig config) {
        this.operationalUi = config.showsOperationalUi();
    }

    @Override
    public void create() {
        assets = GameAssets.load();
        showTitle();
    }

    @Override
    public boolean showsOperationalUi() {
        return operationalUi;
    }

    @Override
    public void showTitle() {
        switchTo(new TitleScreen(this, assets));
    }

    @Override
    public void showHub(String notice) {
        switchTo(new HubScreen(this, progress, notice, gameModule.createHubContext(progress), assets));
    }

    @Override
    public void openActivity(ActivityId activityId) {
        switch (activityId) {
            case BROADCAST_DESK -> switchTo(new PartyPanicScreen(this, progress, assets));
            case CAKE_TABLE -> switchTo(new CakeTableScreen(this, progress, assets));
            case PHOTO_TIME -> switchTo(new PhotoTimeScreen(this, progress, assets));
            case STORAGE_ROOM, BACKSTAGE, FAN_LETTER, FINALE_STAGE ->
                    switchTo(new StorySequenceScreen(this, progress, storyChapterFactory.create(activityId, progress), assets));
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

    @Override
    public void dispose() {
        super.dispose();
        if (assets != null) {
            assets.dispose();
        }
    }
}
