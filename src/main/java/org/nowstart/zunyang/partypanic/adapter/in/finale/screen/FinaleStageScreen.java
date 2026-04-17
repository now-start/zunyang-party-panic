package org.nowstart.zunyang.partypanic.adapter.in.finale.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.ActivityScreenScaffold;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureLibrary;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveFinaleActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleCheckpointView;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectFinaleCheckpointUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveFinaleActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartFinaleStageUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class FinaleStageScreen extends ScreenAdapter {

    private final StartFinaleStageUseCase startFinaleStageUseCase;
    private final MoveFinaleActorUseCase moveFinaleActorUseCase;
    private final InspectFinaleCheckpointUseCase inspectFinaleCheckpointUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

    private FinaleStageViewResult finaleView;

    public FinaleStageScreen(
        StartFinaleStageUseCase startFinaleStageUseCase,
        MoveFinaleActorUseCase moveFinaleActorUseCase,
        InspectFinaleCheckpointUseCase inspectFinaleCheckpointUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startFinaleStageUseCase = startFinaleStageUseCase;
        this.moveFinaleActorUseCase = moveFinaleActorUseCase;
        this.inspectFinaleCheckpointUseCase = inspectFinaleCheckpointUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
        this.bodyFont.getData().setScale(1.0f);
    }

    @Override
    public void show() {
        finaleView = startFinaleStageUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.07f, 0.05f, 0.1f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, finaleView.width(), finaleView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.FINALE_STAGE,
            finaleView.title(),
            finaleView.instructions(),
            "target: 개장 직전 핵심 점검 셋을 마치고 마지막 신호를 보낸다"
        );
        drawStage(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            finaleView.statusMessage(),
            "checks: " + finaleView.checkedRequiredCount() + " / " + finaleView.requiredCount(),
            finaleView.readyToReturn()
                ? "enter로 마지막 개장 신호를 확정한다"
                : "지점 앞에서 z로 최종 점검을 진행한다"
        );
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            finaleView = moveFinaleActorUseCase.move(new MoveFinaleActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            finaleView = moveFinaleActorUseCase.move(new MoveFinaleActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            finaleView = moveFinaleActorUseCase.move(new MoveFinaleActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            finaleView = moveFinaleActorUseCase.move(new MoveFinaleActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            finaleView = inspectFinaleCheckpointUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && finaleView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawStage(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("F3E9FF"));

        for (FinaleCheckpointView checkpoint : finaleView.checkpoints()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, checkpoint.x(), checkpoint.y());

            TextureRegion region = resolveCheckpointTexture(checkpoint);
            batch.setColor(checkpoint.active() ? Color.WHITE : Color.valueOf(checkpoint.checked() ? "E9F7DB" : "D2C9DB"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(
                batch,
                checkpoint.label(),
                bounds.x() + 10f,
                bounds.y() + bounds.height() - 18f,
                bounds.width() - 20f,
                com.badlogic.gdx.utils.Align.left,
                true
            );
            String marker = checkpoint.checked()
                ? "점검 완료"
                : checkpoint.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 10f, bounds.y() + 22f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, finaleView.actorX(), finaleView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, finaleView.facing());
    }

    private TextureRegion resolveCheckpointTexture(FinaleCheckpointView checkpoint) {
        if ("STREAMER_MARK".equals(checkpoint.id())) {
            return textureLibrary.region(SampleTextureId.STREAMER_NPC);
        }
        return textureLibrary.region(
            checkpoint.required() ? SampleTextureId.FINALE_STAGE : SampleTextureId.LOCKED_CARD
        );
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
