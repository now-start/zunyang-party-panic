package org.nowstart.zunyang.partypanic.adapter.in.handover.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHandoverActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverClueView;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectHandoverClueUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveHandoverActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartHandoverCorridorUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class HandoverCorridorScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

    private final StartHandoverCorridorUseCase startHandoverCorridorUseCase;
    private final MoveHandoverActorUseCase moveHandoverActorUseCase;
    private final InspectHandoverClueUseCase inspectHandoverClueUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();

    private HandoverCorridorViewResult handoverView;

    public HandoverCorridorScreen(
        StartHandoverCorridorUseCase startHandoverCorridorUseCase,
        MoveHandoverActorUseCase moveHandoverActorUseCase,
        InspectHandoverClueUseCase inspectHandoverClueUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startHandoverCorridorUseCase = startHandoverCorridorUseCase;
        this.moveHandoverActorUseCase = moveHandoverActorUseCase;
        this.inspectHandoverClueUseCase = inspectHandoverClueUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
    }

    @Override
    public void show() {
        handoverView = startHandoverCorridorUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.06f, 0.09f, 0.07f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.HANDOVER_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, handoverView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, handoverView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 이어진 기록 단서 세 개를 확인해 메시지 월로 넘긴다", MARGIN, worldHeight - 108f);

        drawRoom(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            handoverView = moveHandoverActorUseCase.move(new MoveHandoverActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            handoverView = moveHandoverActorUseCase.move(new MoveHandoverActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            handoverView = moveHandoverActorUseCase.move(new MoveHandoverActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            handoverView = moveHandoverActorUseCase.move(new MoveHandoverActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            handoverView = inspectHandoverClueUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && handoverView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawRoom(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / handoverView.width();
        float cellHeight = GRID_HEIGHT / handoverView.height();
        float roomTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("ECF6EA"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (HandoverClueView clue : handoverView.clues()) {
            float x = MARGIN + (clue.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (clue.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = textureLibrary.region(
                clue.required() ? SampleTextureId.HANDOVER_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(clue.active() ? Color.WHITE : Color.valueOf(clue.collected() ? "DDF0D5" : "C7D5C2"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, clue.label(), x + 10f, y + height - 18f);
            String marker = clue.collected()
                ? "확인 완료"
                : clue.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, x + 10f, y + 26f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + handoverView.facing(), MARGIN, roomTop + 28f);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (handoverView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (handoverView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            handoverView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "handover: " + handoverView.collectedRequiredCount() + " / " + handoverView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = handoverView.readyToReturn()
            ? "enter로 인수인계 정리를 확정한다"
            : "기록 앞에서 z로 확인해 단서를 이어 붙인다";
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
