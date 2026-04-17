package org.nowstart.zunyang.partypanic.adapter.in.message.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveMessageActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageNoteView;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectMessageNoteUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveMessageActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartMessageWallUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class MessageWallScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

    private final StartMessageWallUseCase startMessageWallUseCase;
    private final MoveMessageActorUseCase moveMessageActorUseCase;
    private final InspectMessageNoteUseCase inspectMessageNoteUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();

    private MessageWallViewResult messageView;

    public MessageWallScreen(
        StartMessageWallUseCase startMessageWallUseCase,
        MoveMessageActorUseCase moveMessageActorUseCase,
        InspectMessageNoteUseCase inspectMessageNoteUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startMessageWallUseCase = startMessageWallUseCase;
        this.moveMessageActorUseCase = moveMessageActorUseCase;
        this.inspectMessageNoteUseCase = inspectMessageNoteUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
        this.bodyFont.getData().setScale(0.95f);
    }

    @Override
    public void show() {
        messageView = startMessageWallUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.08f, 0.07f, 0.09f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, messageView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, messageView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 오늘 곁에 둘 핵심 문장 두 개를 정리해 마지막 점검으로 넘긴다", MARGIN, worldHeight - 108f);

        drawWall(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            messageView = moveMessageActorUseCase.move(new MoveMessageActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            messageView = moveMessageActorUseCase.move(new MoveMessageActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            messageView = moveMessageActorUseCase.move(new MoveMessageActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            messageView = moveMessageActorUseCase.move(new MoveMessageActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            messageView = inspectMessageNoteUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && messageView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawWall(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / messageView.width();
        float cellHeight = GRID_HEIGHT / messageView.height();
        float wallTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("F5EDF6"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (MessageNoteView note : messageView.notes()) {
            float x = MARGIN + (note.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (note.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = textureLibrary.region(
                note.required() ? SampleTextureId.MESSAGE_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(note.active() ? Color.WHITE : Color.valueOf(note.selected() ? "F2F7D9" : "D7CFD8"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, note.label(), x + 8f, y + height - 14f);
            bodyFont.draw(batch, note.excerpt(), x + 8f, y + height - 32f, width - 16f, Align.left, true);
            String marker = note.selected()
                ? "선택됨"
                : note.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, x + 8f, y + 14f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + messageView.facing(), MARGIN, wallTop + 28f);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (messageView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (messageView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            messageView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "selected: " + messageView.selectedRequiredCount() + " / " + messageView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = messageView.readyToReturn()
            ? "enter로 오늘 벽에 남길 문장을 확정한다"
            : "문장 앞에서 z로 확인해 오늘 곁에 둘 말을 고른다";
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
