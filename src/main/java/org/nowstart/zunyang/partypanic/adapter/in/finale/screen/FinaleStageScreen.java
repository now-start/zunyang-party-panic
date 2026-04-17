package org.nowstart.zunyang.partypanic.adapter.in.finale.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveFinaleActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleCheckpointView;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectFinaleCheckpointUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveFinaleActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartFinaleStageUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class FinaleStageScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

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

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.FINALE_STAGE), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, finaleView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, finaleView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 개장 직전 핵심 점검 셋을 마치고 마지막 신호를 보낸다", MARGIN, worldHeight - 108f);

        drawStage(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
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

    private void drawStage(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / finaleView.width();
        float cellHeight = GRID_HEIGHT / finaleView.height();
        float stageTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("F3E9FF"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (FinaleCheckpointView checkpoint : finaleView.checkpoints()) {
            float x = MARGIN + (checkpoint.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (checkpoint.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = resolveCheckpointTexture(checkpoint);
            batch.setColor(checkpoint.active() ? Color.WHITE : Color.valueOf(checkpoint.checked() ? "E9F7DB" : "D2C9DB"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, checkpoint.label(), x + 10f, y + height - 18f, width - 20f, Align.left, true);
            String marker = checkpoint.checked()
                ? "점검 완료"
                : checkpoint.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, x + 10f, y + 22f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + finaleView.facing(), MARGIN, stageTop + 28f);
    }

    private TextureRegion resolveCheckpointTexture(FinaleCheckpointView checkpoint) {
        if ("STREAMER_MARK".equals(checkpoint.id())) {
            return textureLibrary.region(SampleTextureId.STREAMER_NPC);
        }
        return textureLibrary.region(
            checkpoint.required() ? SampleTextureId.FINALE_STAGE : SampleTextureId.LOCKED_CARD
        );
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (finaleView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (finaleView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            finaleView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "checks: " + finaleView.checkedRequiredCount() + " / " + finaleView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = finaleView.readyToReturn()
            ? "enter로 마지막 개장 신호를 확정한다"
            : "지점 앞에서 z로 최종 점검을 진행한다";
        bodyFont.draw(batch, footer, MARGIN + 24f, MARGIN + 34f);
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
