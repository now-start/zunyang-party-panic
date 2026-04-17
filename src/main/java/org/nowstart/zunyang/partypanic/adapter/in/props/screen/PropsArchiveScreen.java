package org.nowstart.zunyang.partypanic.adapter.in.props.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MovePropsActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsItemView;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPropsItemUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePropsActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartPropsArchiveUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class PropsArchiveScreen extends ScreenAdapter {

    private final StartPropsArchiveUseCase startPropsArchiveUseCase;
    private final MovePropsActorUseCase movePropsActorUseCase;
    private final InspectPropsItemUseCase inspectPropsItemUseCase;
    private final CompleteChapterUseCase completeChapterUseCase;
    private final Runnable onReturnToHub;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.COMPACT);
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();
    private final ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

    private PropsArchiveViewResult propsView;

    public PropsArchiveScreen(
        StartPropsArchiveUseCase startPropsArchiveUseCase,
        MovePropsActorUseCase movePropsActorUseCase,
        InspectPropsItemUseCase inspectPropsItemUseCase,
        CompleteChapterUseCase completeChapterUseCase,
        Runnable onReturnToHub
    ) {
        this.startPropsArchiveUseCase = startPropsArchiveUseCase;
        this.movePropsActorUseCase = movePropsActorUseCase;
        this.inspectPropsItemUseCase = inspectPropsItemUseCase;
        this.completeChapterUseCase = completeChapterUseCase;
        this.onReturnToHub = onReturnToHub;
    }

    @Override
    public void show() {
        propsView = startPropsArchiveUseCase.start();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.07f, 0.06f, 0.1f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        ActivityScreenScaffold.ActivityFrame frame =
            scaffold.frame(worldWidth, worldHeight, propsView.width(), propsView.height());

        batch.begin();
        scaffold.drawHeader(
            batch,
            titleFont,
            bodyFont,
            textureLibrary,
            frame,
            SampleTextureId.PROPS_CARD,
            propsView.title(),
            propsView.instructions(),
            "target: 필요한 상자 세 개만 골라 회수한다"
        );
        drawRoom(frame);
        scaffold.drawStatusPanel(
            batch,
            bodyFont,
            textureLibrary,
            frame,
            propsView.statusMessage(),
            "inventory: " + propsView.collectedRequiredCount() + " / " + propsView.requiredCount(),
            propsView.readyToReturn()
                ? "enter로 회수 결과를 확정한다"
                : "상자 앞에서 z로 조사해 필요한 소품만 챙긴다"
        );
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            propsView = movePropsActorUseCase.move(new MovePropsActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            propsView = movePropsActorUseCase.move(new MovePropsActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            propsView = movePropsActorUseCase.move(new MovePropsActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            propsView = movePropsActorUseCase.move(new MovePropsActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.X)
            || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            propsView = inspectPropsItemUseCase.inspect();
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && propsView.readyToReturn()) {
            completeChapterUseCase.complete();
            onReturnToHub.run();
        }
    }

    private void drawRoom(ActivityScreenScaffold.ActivityFrame frame) {
        scaffold.drawGridPanel(batch, textureLibrary, frame, Color.valueOf("F0E5FA"));

        for (PropsItemView item : propsView.items()) {
            ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, item.x(), item.y());

            com.badlogic.gdx.graphics.g2d.TextureRegion region =
                textureLibrary.region(item.required() ? SampleTextureId.PROPS_CARD : SampleTextureId.LOCKED_CARD);
            batch.setColor(item.active() ? Color.WHITE : Color.valueOf(item.collected() ? "D9F7BE" : "B8B0C8"));
            batch.draw(region, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, item.label(), bounds.x() + 10f, bounds.y() + bounds.height() - 18f);
            String marker = item.collected()
                ? "회수 완료"
                : item.required() ? "필수" : "보류";
            bodyFont.draw(batch, marker, bounds.x() + 10f, bounds.y() + 26f);
        }

        scaffold.drawActor(batch, bodyFont, textureLibrary, frame, propsView.actorX(), propsView.actorY());
        scaffold.drawFacing(batch, bodyFont, frame, propsView.facing());
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
