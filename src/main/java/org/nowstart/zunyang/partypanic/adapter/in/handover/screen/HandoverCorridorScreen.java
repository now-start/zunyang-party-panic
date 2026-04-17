package org.nowstart.zunyang.partypanic.adapter.in.handover.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHandoverActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverClueView;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectHandoverClueUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveHandoverActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartHandoverCorridorUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class HandoverCorridorScreen extends ScreenAdapter {

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
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

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
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, handoverView.width(), handoverView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.HANDOVER_CARD,
            handoverView.title(),
            handoverView.instructions(),
            "target: 이어진 기록 단서 세 개를 확인해 메시지 월로 넘긴다"
        );
        drawRoom(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            handoverView.statusMessage(),
            "handover: " + handoverView.collectedRequiredCount() + " / " + handoverView.requiredCount(),
            handoverView.readyToReturn()
                ? "enter로 인수인계 정리를 확정한다"
                : "기록 앞에서 z로 확인해 단서를 이어 붙인다"
        );
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

    private void drawRoom(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("ECF6EA"));

        for (HandoverClueView clue : handoverView.clues()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, clue.x(), clue.y());

            com.badlogic.gdx.graphics.g2d.TextureRegion region = textureLibrary.region(
                clue.required() ? SampleTextureId.HANDOVER_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(clue.active() ? Color.WHITE : Color.valueOf(clue.collected() ? "DDF0D5" : "C7D5C2"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, clue.label(), bounds.x() + 10f, bounds.y() + bounds.height() - 18f);
            String marker = clue.collected()
                ? "확인 완료"
                : clue.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 10f, bounds.y() + 26f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, handoverView.actorX(), handoverView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, handoverView.facing());
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
