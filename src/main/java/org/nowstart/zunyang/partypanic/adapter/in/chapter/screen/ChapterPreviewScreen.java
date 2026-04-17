package org.nowstart.zunyang.partypanic.adapter.in.chapter.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;
import java.util.function.Consumer;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.DebugHudRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleIconId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleIconLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleVisualCatalog;
import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.AdvanceChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.SkipChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartChapterUseCase;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

public final class ChapterPreviewScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float HINT_ICON_SIZE = 26f;

    private final ChapterId chapterId;
    private final StartChapterUseCase startChapterUseCase;
    private final AdvanceChapterUseCase advanceChapterUseCase;
    private final SkipChapterUseCase skipChapterUseCase;
    private final Consumer<String> onOpenActivity;
    private final Runnable onReturnToHub;
    private final Runnable onRestartSession;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.BODY);
    private final SampleIconLibrary iconLibrary = new SampleIconLibrary();
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();
    private final DebugHudRenderer debugHudRenderer = new DebugHudRenderer();
    private final SampleVisualCatalog visualCatalog = SampleVisualCatalog.defaultCatalog();

    private ChapterViewResult chapterView;
    private boolean debugHudVisible = true;

    public ChapterPreviewScreen(
        ChapterId chapterId,
        StartChapterUseCase startChapterUseCase,
        AdvanceChapterUseCase advanceChapterUseCase,
        SkipChapterUseCase skipChapterUseCase,
        Consumer<String> onOpenActivity,
        Runnable onReturnToHub,
        Runnable onRestartSession
    ) {
        this.chapterId = chapterId;
        this.startChapterUseCase = startChapterUseCase;
        this.advanceChapterUseCase = advanceChapterUseCase;
        this.skipChapterUseCase = skipChapterUseCase;
        this.onOpenActivity = onOpenActivity;
        this.onReturnToHub = onReturnToHub;
        this.onRestartSession = onRestartSession;
    }

    @Override
    public void show() {
        chapterView = startChapterUseCase.start(chapterId);
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.07f, 0.08f, 0.12f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(resolveVisual()), 0f, 0f, worldWidth, worldHeight);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), 240f);

        titleFont.draw(batch, chapterView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, chapterView.subtitle(), MARGIN, worldHeight - 74f);
        drawControlHints(worldHeight);
        bodyFont.draw(batch, "semantic preview assets load by role and stop at activity start on skip", MARGIN, worldHeight - 134f);
        bodyFont.draw(
            batch,
            chapterView.pageNumber() + " / " + chapterView.totalPages(),
            worldWidth - 170f,
            worldHeight - 36f
        );

        bodyFont.draw(batch, chapterView.speaker(), MARGIN + 24f, MARGIN + 210f);
        bodyFont.draw(
            batch,
            chapterView.text(),
            MARGIN + 24f,
            MARGIN + 160f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        if (debugHudVisible) {
            drawDebugHud(worldWidth, worldHeight);
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugHudVisible = !debugHudVisible;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            onRestartSession.run();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            onReturnToHub.run();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            chapterView = skipChapterUseCase.skip();
            if (chapterView.activityReady()) {
                onOpenActivity.accept(chapterView.activityType());
                return;
            }
            if (chapterView.completed()) {
                onReturnToHub.run();
            }
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            chapterView = advanceChapterUseCase.advance();
            if (chapterView.activityReady()) {
                onOpenActivity.accept(chapterView.activityType());
                return;
            }
            if (chapterView.completed()) {
                onReturnToHub.run();
            }
        }
    }

    private void drawControlHints(float worldHeight) {
        float y = worldHeight - 112f;
        drawControlHint(MARGIN, y, SampleIconId.NEXT, "enter/space");
        drawControlHint(MARGIN + 200f, y, SampleIconId.SKIP, "tab");
        drawControlHint(MARGIN + 340f, y, SampleIconId.HUB, "esc");
        drawControlHint(MARGIN + 460f, y, SampleIconId.DEBUG, "f3");
        drawControlHint(MARGIN + 560f, y, SampleIconId.RESTART, "f5");
    }

    private SampleTextureId resolveVisual() {
        return visualCatalog.visualToken(chapterView.visualToken());
    }

    private void drawControlHint(float x, float y, SampleIconId iconId, String label) {
        TextureRegion icon = iconLibrary.region(iconId);
        batch.draw(icon, x, y, HINT_ICON_SIZE, HINT_ICON_SIZE);
        bodyFont.draw(batch, label, x + HINT_ICON_SIZE + 10f, y + 20f);
    }

    private void drawDebugHud(float worldWidth, float worldHeight) {
        SampleTextureId visual = resolveVisual();
        List<String> lines = List.of(
            "chapter=" + chapterView.chapterId() + " page=" + chapterView.pageNumber() + "/" + chapterView.totalPages(),
            "activity=" + chapterView.activityType() + " ready=" + chapterView.activityReady() + " done=" + chapterView.completed(),
            "speaker=" + chapterView.speaker(),
            "visual=" + visual.name() + ":" + textureLibrary.debugSource(visual),
            "panel=" + textureLibrary.debugSource(SampleTextureId.MESSAGE_PANEL),
            "font=title:" + fontLibrary.debugSource(SampleFontId.TITLE)
                + " body:" + fontLibrary.debugSource(SampleFontId.BODY),
            "icon=next:" + iconLibrary.debugSource(SampleIconId.NEXT)
                + " skip:" + iconLibrary.debugSource(SampleIconId.SKIP)
        );

        debugHudRenderer.draw(
            batch,
            bodyFont,
            textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
            worldWidth,
            worldHeight,
            "DEBUG CHAPTER",
            lines
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        textureLibrary.dispose();
        iconLibrary.dispose();
        fontLibrary.dispose();
        batch.dispose();
    }
}
