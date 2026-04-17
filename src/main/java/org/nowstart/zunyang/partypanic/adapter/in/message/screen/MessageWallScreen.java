package org.nowstart.zunyang.partypanic.adapter.in.message.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.ActivityScreenScaffold;
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
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

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
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, messageView.width(), messageView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.MESSAGE_CARD,
            messageView.title(),
            messageView.instructions(),
            "target: 오늘 곁에 둘 핵심 문장 두 개를 정리해 마지막 점검으로 넘긴다"
        );
        drawWall(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            messageView.statusMessage(),
            "selected: " + messageView.selectedRequiredCount() + " / " + messageView.requiredCount(),
            messageView.readyToReturn()
                ? "enter로 오늘 벽에 남길 문장을 확정한다"
                : "문장 앞에서 z로 확인해 오늘 곁에 둘 말을 고른다"
        );
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

    private void drawWall(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("F5EDF6"));

        for (MessageNoteView note : messageView.notes()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, note.x(), note.y());

            com.badlogic.gdx.graphics.g2d.TextureRegion region = textureLibrary.region(
                note.required() ? SampleTextureId.MESSAGE_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(note.active() ? Color.WHITE : Color.valueOf(note.selected() ? "F2F7D9" : "D7CFD8"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, note.label(), bounds.x() + 8f, bounds.y() + bounds.height() - 14f);
            bodyFont.draw(
                batch,
                note.excerpt(),
                bounds.x() + 8f,
                bounds.y() + bounds.height() - 32f,
                bounds.width() - 16f,
                Align.left,
                true
            );
            String marker = note.selected()
                ? "선택됨"
                : note.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 8f, bounds.y() + 14f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, messageView.actorX(), messageView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, messageView.facing());
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
