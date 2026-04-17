package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.adapter.in.input.MappedActionInputAdapter;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.DialogueWindowRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.PixelUiRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameViewportConfig;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.domain.story.StoryChapter;

import java.util.List;
import java.util.Map;

public final class StorySequenceScreen extends AbstractGameScreen {
    private static final Color TEXT_PRIMARY = new Color(0.96f, 0.92f, 0.87f, 1f);
    private static final Color TEXT_MUTED = new Color(0.81f, 0.76f, 0.72f, 1f);
    private static final Color TEXT_ACCENT = new Color(0.98f, 0.77f, 0.61f, 1f);
    private static final Color TEXT_MINT = new Color(0.66f, 0.89f, 0.81f, 1f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.52f);
    private static final Color PLATE_BACKGROUND = new Color(0.13f, 0.09f, 0.11f, 0.86f);
    private static final Color WINDOW_EDGE = new Color(0.97f, 0.88f, 0.77f, 0.92f);

    private final GameProgress progress;
    private final ActivityId activityId;
    private final String title;
    private final String subtitle;
    private final String returnNotice;
    private final String completionNotice;
    private final List<String> pages;
    private final BitmapFont bodyFont;
    private final BitmapFont titleFont;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture portraitTexture;
    private final PixelUiRenderer ui;
    private final DialogueWindowRenderer dialogueWindow;
    private final MappedActionInputAdapter<StoryAction> input = new MappedActionInputAdapter<>(Map.of(
            Input.Keys.ESCAPE, StoryAction.EXIT,
            Input.Keys.ENTER, StoryAction.ADVANCE,
            Input.Keys.SPACE, StoryAction.ADVANCE,
            Input.Keys.E, StoryAction.ADVANCE
    ));

    private int pageIndex;
    private float sceneTime;
    private float dialogueWindowProgress;

    public StorySequenceScreen(
            GameNavigator navigator,
            GameProgress progress,
            StoryChapter chapter,
            GameAssets assets
    ) {
        super(navigator, assets);
        this.progress = progress;
        this.activityId = chapter.activityId();
        this.title = chapter.title();
        this.subtitle = chapter.subtitle();
        this.returnNotice = chapter.returnNotice();
        this.completionNotice = chapter.completionNotice();
        this.pages = List.copyOf(chapter.pages());
        this.bodyFont = assets.bodyFont();
        this.titleFont = assets.titleFont();
        this.pixelTexture = assets.pixelTexture();
        this.backgroundTexture = assets.texture(chapter.backgroundPath());
        this.portraitTexture = assets.hostTexture();
        this.ui = new PixelUiRenderer(batch, bodyFont, titleFont, pixelTexture);
        this.dialogueWindow = new DialogueWindowRenderer(ui);
    }

    @Override
    public void render(float delta) {
        sceneTime += delta;
        dialogueWindowProgress = MathUtils.lerp(dialogueWindowProgress, 1f, Math.min(1f, delta * 10f));
        if (!handleInput()) {
            return;
        }

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        beginFrame();
        ui.textureCover(backgroundTexture, 0f, 0f, GameViewportConfig.WORLD_WIDTH, GameViewportConfig.WORLD_HEIGHT);
        ui.panel(0f, 0f, GameViewportConfig.WORLD_WIDTH, GameViewportConfig.WORLD_HEIGHT, OVERLAY_COLOR);
        drawChapterPlate();
        if (showsOperationalUi()) {
            drawDebugPlate();
        }
        dialogueWindow.draw(
                portraitTexture,
                sceneTime,
                dialogueWindowProgress,
                "치즈냥",
                pages.get(pageIndex),
                TEXT_PRIMARY,
                pageIndex == pages.size() - 1 ? "마지막 문장" : null,
                TEXT_ACCENT
        );
        endFrame();
    }

    @Override
    protected InputProcessor createInputProcessor() {
        return input;
    }

    private boolean handleInput() {
        StoryAction action;
        while ((action = input.pollAction()) != null) {
            switch (action) {
                case EXIT -> {
                    navigator.showHub(returnNotice);
                    return false;
                }
                case ADVANCE -> {
                    if (pageIndex < pages.size() - 1) {
                        pageIndex += 1;
                        dialogueWindowProgress = 0f;
                        return true;
                    }
                    navigator.completeStoryActivity(activityId, completionNotice);
                    return false;
                }
            }
        }
        return true;
    }

    private void drawChapterPlate() {
        ui.panel(54f, GameViewportConfig.WORLD_HEIGHT - 84f, 328f, 56f, PLATE_BACKGROUND);
        ui.panelOutline(54f, GameViewportConfig.WORLD_HEIGHT - 84f, 328f, 56f, WINDOW_EDGE);
        ui.line("CHAPTER " + progress.getChapterNumber(activityId), 72f, GameViewportConfig.WORLD_HEIGHT - 48f, 0.74f, TEXT_ACCENT);
        ui.titleLine(title, 224f, GameViewportConfig.WORLD_HEIGHT - 48f, 0.86f, TEXT_PRIMARY);
        ui.line(subtitle, 72f, GameViewportConfig.WORLD_HEIGHT - 68f, 0.56f, TEXT_MUTED);
    }

    private void drawDebugPlate() {
        float x = GameViewportConfig.WORLD_WIDTH - 422f;
        float y = GameViewportConfig.WORLD_HEIGHT - 162f;
        ui.panel(x, y, 368f, 120f, PLATE_BACKGROUND);
        ui.panelOutline(x, y, 368f, 120f, WINDOW_EDGE);
        ui.line("test mode", x + 18f, y + 94f, 0.74f, TEXT_ACCENT);
        ui.line("페이지 " + (pageIndex + 1) + " / " + pages.size(), x + 18f, y + 66f, 0.68f, TEXT_PRIMARY);
        ui.line("예상 엔딩 " + progress.getEndingTitle(), x + 18f, y + 40f, 0.68f, TEXT_MINT);
        ui.paragraph(progress.getNextObjective(), x + 18f, y + 18f, 332f, 0.54f, TEXT_MUTED);
    }

    private enum StoryAction {
        EXIT,
        ADVANCE
    }
}
