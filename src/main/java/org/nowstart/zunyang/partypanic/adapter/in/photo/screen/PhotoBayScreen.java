package org.nowstart.zunyang.partypanic.adapter.in.photo.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MovePhotoActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoFocusView;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPhotoFocusUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePhotoActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartPhotoBayUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class PhotoBayScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

    private final StartPhotoBayUseCase startPhotoBayUseCase;
    private final MovePhotoActorUseCase movePhotoActorUseCase;
    private final InspectPhotoFocusUseCase inspectPhotoFocusUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();

    private PhotoBayViewResult photoView;

    public PhotoBayScreen(
        StartPhotoBayUseCase startPhotoBayUseCase,
        MovePhotoActorUseCase movePhotoActorUseCase,
        InspectPhotoFocusUseCase inspectPhotoFocusUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startPhotoBayUseCase = startPhotoBayUseCase;
        this.movePhotoActorUseCase = movePhotoActorUseCase;
        this.inspectPhotoFocusUseCase = inspectPhotoFocusUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
    }

    @Override
    public void show() {
        photoView = startPhotoBayUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.05f, 0.08f, 0.09f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.PHOTO_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, photoView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, photoView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 프레임, 의자, 빛을 맞춰 남길 장면을 고정한다", MARGIN, worldHeight - 108f);

        drawRoom(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            photoView = movePhotoActorUseCase.move(new MovePhotoActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            photoView = movePhotoActorUseCase.move(new MovePhotoActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            photoView = movePhotoActorUseCase.move(new MovePhotoActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            photoView = movePhotoActorUseCase.move(new MovePhotoActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            photoView = inspectPhotoFocusUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && photoView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawRoom(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / photoView.width();
        float cellHeight = GRID_HEIGHT / photoView.height();
        float roomTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("EAF9F8"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (PhotoFocusView focus : photoView.focuses()) {
            float x = MARGIN + (focus.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (focus.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = textureLibrary.region(
                focus.required() ? SampleTextureId.PHOTO_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(focus.active() ? Color.WHITE : Color.valueOf(focus.locked() ? "D9F7F1" : "C2D3D1"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, focus.label(), x + 10f, y + height - 18f);
            String marker = focus.locked()
                ? "고정 완료"
                : focus.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, x + 10f, y + 26f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + photoView.facing(), MARGIN, roomTop + 28f);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (photoView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (photoView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            photoView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "frame: " + photoView.lockedRequiredCount() + " / " + photoView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = photoView.readyToReturn()
            ? "enter로 포토 베이를 확정한다"
            : "포인트 앞에서 z로 확인해 프레임을 고정한다";
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
