package org.nowstart.zunyang.partypanic.adapter.in.photo.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MovePhotoActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoFocusView;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPhotoFocusUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePhotoActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartPhotoBayUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class PhotoBayScreen extends ScreenAdapter {

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
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

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
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, photoView.width(), photoView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.PHOTO_CARD,
            photoView.title(),
            photoView.instructions(),
            "target: 프레임, 의자, 빛을 맞춰 남길 장면을 고정한다"
        );
        drawRoom(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            photoView.statusMessage(),
            "frame: " + photoView.lockedRequiredCount() + " / " + photoView.requiredCount(),
            photoView.readyToReturn()
                ? "enter로 포토 베이를 확정한다"
                : "포인트 앞에서 z로 확인해 프레임을 고정한다"
        );
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

    private void drawRoom(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("EAF9F8"));

        for (PhotoFocusView focus : photoView.focuses()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, focus.x(), focus.y());

            com.badlogic.gdx.graphics.g2d.TextureRegion region = textureLibrary.region(
                focus.required() ? SampleTextureId.PHOTO_CARD : SampleTextureId.LOCKED_CARD
            );
            batch.setColor(focus.active() ? Color.WHITE : Color.valueOf(focus.locked() ? "D9F7F1" : "C2D3D1"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, focus.label(), bounds.x() + 10f, bounds.y() + bounds.height() - 18f);
            String marker = focus.locked()
                ? "고정 완료"
                : focus.required() ? "핵심" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 10f, bounds.y() + 26f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, photoView.actorX(), photoView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, photoView.facing());
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
