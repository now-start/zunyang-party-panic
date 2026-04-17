package org.nowstart.zunyang.partypanic.adapter.in.signal.screen;

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
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureLibrary;
import org.nowstart.zunyang.partypanic.application.dto.command.AdjustSignalSettingCommand;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveSignalActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalControlView;
import org.nowstart.zunyang.partypanic.application.port.in.AdjustSignalSettingUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectSignalControlUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveSignalActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartSignalConsoleUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class SignalConsoleScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

    private final StartSignalConsoleUseCase startSignalConsoleUseCase;
    private final MoveSignalActorUseCase moveSignalActorUseCase;
    private final InspectSignalControlUseCase inspectSignalControlUseCase;
    private final AdjustSignalSettingUseCase adjustSignalSettingUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();

    private SignalConsoleViewResult signalView;

    public SignalConsoleScreen(
        StartSignalConsoleUseCase startSignalConsoleUseCase,
        MoveSignalActorUseCase moveSignalActorUseCase,
        InspectSignalControlUseCase inspectSignalControlUseCase,
        AdjustSignalSettingUseCase adjustSignalSettingUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startSignalConsoleUseCase = startSignalConsoleUseCase;
        this.moveSignalActorUseCase = moveSignalActorUseCase;
        this.inspectSignalControlUseCase = inspectSignalControlUseCase;
        this.adjustSignalSettingUseCase = adjustSignalSettingUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
    }

    @Override
    public void show() {
        signalView = startSignalConsoleUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.06f, 0.07f, 0.1f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.SIGNAL_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, signalView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, signalView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 룸을 돌며 장비 네 개를 모두 안정값으로 맞춘다", MARGIN, worldHeight - 108f);

        drawRoom(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            signalView = moveSignalActorUseCase.move(new MoveSignalActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            signalView = moveSignalActorUseCase.move(new MoveSignalActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            signalView = moveSignalActorUseCase.move(new MoveSignalActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            signalView = moveSignalActorUseCase.move(new MoveSignalActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            signalView = inspectSignalControlUseCase.inspect();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            signalView = adjustSignalSettingUseCase.adjust(new AdjustSignalSettingCommand(-1));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            signalView = adjustSignalSettingUseCase.adjust(new AdjustSignalSettingCommand(1));
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && signalView.stabilized()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawRoom(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / signalView.width();
        float cellHeight = GRID_HEIGHT / signalView.height();
        float roomTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("E6E9EE"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (SignalControlView control : signalView.controls()) {
            float x = MARGIN + (control.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (control.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            batch.setColor(control.active() ? Color.WHITE : Color.valueOf("A7AFBC"));
            batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, control.label(), x + 10f, y + height - 18f);
            bodyFont.draw(batch, control.currentDescriptor(), x + 10f, y + height - 50f);
            drawLevelBar(control, x + 10f, y + 22f, width - 20f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + signalView.facing(), MARGIN, roomTop + 28f);
    }

    private void drawLevelBar(SignalControlView control, float x, float y, float width) {
        float segmentGap = 12f;
        float segmentWidth = (width - (segmentGap * 2f)) / 3f;
        for (int level = 0; level < 3; level++) {
            batch.setColor(level <= control.currentLevel() ? Color.valueOf("F6B17A") : Color.valueOf("525B6A"));
            batch.draw(
                textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
                x + (level * (segmentWidth + segmentGap)),
                y,
                segmentWidth,
                18f
            );
        }
        batch.setColor(Color.WHITE);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (signalView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (signalView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            signalView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(batch, "active: " + valueOrDash(signalView.activeControlId()), MARGIN + 24f, MARGIN + 66f);
        String footer = signalView.stabilized()
            ? "enter로 첫 신호를 확정한다"
            : "장비 앞에서 z로 조사하고 x/c로 값을 내리거나 올린다";
        bodyFont.draw(batch, footer, MARGIN + 24f, MARGIN + 34f);
    }

    private String valueOrDash(String value) {
        return value == null ? "-" : value;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        textureLibrary.dispose();
        fontLibrary.dispose();
        batch.dispose();
    }
}
