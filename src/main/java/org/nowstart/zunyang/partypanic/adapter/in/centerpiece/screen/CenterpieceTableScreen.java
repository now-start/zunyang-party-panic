package org.nowstart.zunyang.partypanic.adapter.in.centerpiece.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveCenterpieceActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpiecePlacementView;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectCenterpiecePlacementUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveCenterpieceActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartCenterpieceTableUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class CenterpieceTableScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

    private final StartCenterpieceTableUseCase startCenterpieceTableUseCase;
    private final MoveCenterpieceActorUseCase moveCenterpieceActorUseCase;
    private final InspectCenterpiecePlacementUseCase inspectCenterpiecePlacementUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();

    private CenterpieceTableViewResult centerpieceView;

    public CenterpieceTableScreen(
        StartCenterpieceTableUseCase startCenterpieceTableUseCase,
        MoveCenterpieceActorUseCase moveCenterpieceActorUseCase,
        InspectCenterpiecePlacementUseCase inspectCenterpiecePlacementUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startCenterpieceTableUseCase = startCenterpieceTableUseCase;
        this.moveCenterpieceActorUseCase = moveCenterpieceActorUseCase;
        this.inspectCenterpiecePlacementUseCase = inspectCenterpiecePlacementUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
    }

    @Override
    public void show() {
        centerpieceView = startCenterpieceTableUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.09f, 0.06f, 0.08f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.CENTERPIECE_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, centerpieceView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, centerpieceView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 중심 배치 세 개를 맞춰 화면 중앙을 세운다", MARGIN, worldHeight - 108f);

        drawRoom(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            centerpieceView = moveCenterpieceActorUseCase.move(new MoveCenterpieceActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            centerpieceView = moveCenterpieceActorUseCase.move(new MoveCenterpieceActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            centerpieceView = moveCenterpieceActorUseCase.move(new MoveCenterpieceActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            centerpieceView = moveCenterpieceActorUseCase.move(new MoveCenterpieceActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            centerpieceView = inspectCenterpiecePlacementUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && centerpieceView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawRoom(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / centerpieceView.width();
        float cellHeight = GRID_HEIGHT / centerpieceView.height();
        float roomTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("FFF2F2"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (CenterpiecePlacementView placement : centerpieceView.placements()) {
            float x = MARGIN + (placement.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (placement.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = textureLibrary.region(
                placement.required() ? SampleTextureId.CENTERPIECE_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(placement.active() ? Color.WHITE : Color.valueOf(placement.placed() ? "FFE5B4" : "DCC7C7"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, placement.label(), x + 10f, y + height - 18f);
            String marker = placement.placed()
                ? "배치 완료"
                : placement.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, x + 10f, y + 26f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + centerpieceView.facing(), MARGIN, roomTop + 28f);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (centerpieceView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (centerpieceView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            centerpieceView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "layout: " + centerpieceView.placedRequiredCount() + " / " + centerpieceView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = centerpieceView.readyToReturn()
            ? "enter로 테이블 배치를 확정한다"
            : "배치 포인트 앞에서 z로 확인해 중심 장면을 완성한다";
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
