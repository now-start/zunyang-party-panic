package org.nowstart.zunyang.partypanic.adapter.in.centerpiece.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MoveCenterpieceActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpiecePlacementView;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectCenterpiecePlacementUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveCenterpieceActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartCenterpieceTableUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class CenterpieceTableScreen extends ScreenAdapter {

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
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

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
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, centerpieceView.width(), centerpieceView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.CENTERPIECE_CARD,
            centerpieceView.title(),
            centerpieceView.instructions(),
            "target: 중심 배치 세 개를 맞춰 화면 중앙을 세운다"
        );
        drawRoom(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            centerpieceView.statusMessage(),
            "layout: " + centerpieceView.placedRequiredCount() + " / " + centerpieceView.requiredCount(),
            centerpieceView.readyToReturn()
                ? "enter로 테이블 배치를 확정한다"
                : "배치 포인트 앞에서 z로 확인해 중심 장면을 완성한다"
        );
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

    private void drawRoom(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("FFF2F2"));

        for (CenterpiecePlacementView placement : centerpieceView.placements()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, placement.x(), placement.y());

            com.badlogic.gdx.graphics.g2d.TextureRegion region = textureLibrary.region(
                placement.required() ? SampleTextureId.CENTERPIECE_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(placement.active() ? Color.WHITE : Color.valueOf(placement.placed() ? "FFE5B4" : "DCC7C7"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, placement.label(), bounds.x() + 10f, bounds.y() + bounds.height() - 18f);
            String marker = placement.placed()
                ? "배치 완료"
                : placement.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 10f, bounds.y() + 26f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, centerpieceView.actorX(), centerpieceView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, centerpieceView.facing());
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
