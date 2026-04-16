package org.nowstart.zunyang.partypanic.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.application.port.GameNavigator;
import org.nowstart.zunyang.partypanic.application.story.StoryChapter;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.presentation.support.ScreenSupport;
import org.nowstart.zunyang.partypanic.presentation.ui.DialogueWindowRenderer;
import org.nowstart.zunyang.partypanic.presentation.ui.PixelUiRenderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class StorySequenceScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;

    private static final Color TEXT_PRIMARY = new Color(0.96f, 0.92f, 0.87f, 1f);
    private static final Color TEXT_MUTED = new Color(0.81f, 0.76f, 0.72f, 1f);
    private static final Color TEXT_ACCENT = new Color(0.98f, 0.77f, 0.61f, 1f);
    private static final Color TEXT_MINT = new Color(0.66f, 0.89f, 0.81f, 1f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.52f);
    private static final Color PLATE_BACKGROUND = new Color(0.13f, 0.09f, 0.11f, 0.86f);
    private static final Color WINDOW_EDGE = new Color(0.97f, 0.88f, 0.77f, 0.92f);

    private final GameNavigator navigator;
    private final GameProgress progress;
    private final ActivityId activityId;
    private final String title;
    private final String subtitle;
    private final String returnNotice;
    private final String completionNotice;
    private final List<String> pages;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture portraitTexture;
    private final PixelUiRenderer ui;
    private final DialogueWindowRenderer dialogueWindow;

    private int pageIndex;
    private float sceneTime;
    private float dialogueWindowProgress;

    public StorySequenceScreen(
            GameNavigator navigator,
            GameProgress progress,
            StoryChapter chapter
    ) {
        this.navigator = navigator;
        this.progress = progress;
        this.activityId = chapter.activityId();
        this.title = chapter.title();
        this.subtitle = chapter.subtitle();
        this.returnNotice = chapter.returnNotice();
        this.completionNotice = chapter.completionNotice();
        this.pages = List.copyOf(chapter.pages());
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture(chapter.backgroundPath());
        this.portraitTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
        this.ui = new PixelUiRenderer(batch, font, pixelTexture);
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

        batch.begin();
        ui.textureCover(backgroundTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        ui.panel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
        drawChapterPlate();
        if (navigator.showsOperationalUi()) {
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
        batch.end();
    }

    private boolean handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.showHub(returnNotice);
            return false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (pageIndex < pages.size() - 1) {
                pageIndex += 1;
                dialogueWindowProgress = 0f;
                return true;
            }
            navigator.completeStoryActivity(activityId, completionNotice);
            return false;
        }
        return true;
    }

    private void drawChapterPlate() {
        ui.panel(54f, WINDOW_HEIGHT - 84f, 328f, 56f, PLATE_BACKGROUND);
        ui.panelOutline(54f, WINDOW_HEIGHT - 84f, 328f, 56f, WINDOW_EDGE);
        ui.line("CHAPTER " + progress.getChapterNumber(activityId), 72f, WINDOW_HEIGHT - 48f, 0.74f, TEXT_ACCENT);
        ui.line(title, 224f, WINDOW_HEIGHT - 48f, 0.86f, TEXT_PRIMARY);
        ui.line(subtitle, 72f, WINDOW_HEIGHT - 68f, 0.56f, TEXT_MUTED);
    }

    private void drawDebugPlate() {
        float x = WINDOW_WIDTH - 422f;
        float y = WINDOW_HEIGHT - 162f;
        ui.panel(x, y, 368f, 120f, PLATE_BACKGROUND);
        ui.panelOutline(x, y, 368f, 120f, WINDOW_EDGE);
        ui.line("test mode", x + 18f, y + 94f, 0.74f, TEXT_ACCENT);
        ui.line("페이지 " + (pageIndex + 1) + " / " + pages.size(), x + 18f, y + 66f, 0.68f, TEXT_PRIMARY);
        ui.line("예상 엔딩 " + progress.getEndingTitle(), x + 18f, y + 40f, 0.68f, TEXT_MINT);
        ui.paragraph(progress.getNextObjective(), x + 18f, y + 18f, 332f, 0.54f, TEXT_MUTED);
    }

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        List<String> texts = new ArrayList<>(pages);
        texts.add(title);
        texts.add(subtitle);
        texts.add(returnNotice);
        texts.add(completionNotice);
        texts.add(progress.getNextObjective());
        texts.add(progress.getEndingTitle());
        texts.add(progress.getEndingLine());
        texts.addAll(List.of(
                "치즈냥",
                "CHAPTER",
                "test mode",
                "페이지",
                "예상 엔딩",
                "마지막 문장"
        ));

        for (String text : texts) {
            appendCharacters(characters, text);
        }

        StringBuilder builder = new StringBuilder(characters.size());
        for (Character character : characters) {
            builder.append(character);
        }
        return builder.toString();
    }

    private void appendCharacters(Set<Character> characters, String text) {
        for (int index = 0; index < text.length(); index += 1) {
            characters.add(text.charAt(index));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelTexture.dispose();
        backgroundTexture.dispose();
        portraitTexture.dispose();
    }
}
