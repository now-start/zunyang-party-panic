package org.nowstart.zunyang.partypanic.adapter.in.signal.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.ActivityScreenScaffold;
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
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

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
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, signalView.width(), signalView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.SIGNAL_CARD,
            signalView.title(),
            signalView.instructions(),
            "target: 룸을 돌며 장비 네 개를 모두 안정값으로 맞춘다"
        );
        drawRoom(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            signalView.statusMessage(),
            "active: " + valueOrDash(signalView.activeControlId()),
            signalView.stabilized()
                ? "enter로 첫 신호를 확정한다"
                : "장비 앞에서 z로 조사하고 x/c로 값을 내리거나 올린다"
        );
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

    private void drawRoom(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("E6E9EE"));

        for (SignalControlView control : signalView.controls()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, control.x(), control.y());

            batch.setColor(control.active() ? Color.WHITE : Color.valueOf("A7AFBC"));
            batch.draw(
                textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
                bounds.x(),
                bounds.y(),
                bounds.width(),
                bounds.height()
            );
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, control.label(), bounds.x() + 10f, bounds.y() + bounds.height() - 18f);
            bodyFont.draw(batch, control.currentDescriptor(), bounds.x() + 10f, bounds.y() + bounds.height() - 50f);
            drawLevelBar(control, bounds.x() + 10f, bounds.y() + 22f, bounds.width() - 20f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, signalView.actorX(), signalView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, signalView.facing());
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
