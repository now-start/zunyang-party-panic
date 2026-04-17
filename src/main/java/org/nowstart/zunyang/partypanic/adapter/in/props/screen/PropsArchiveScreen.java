package org.nowstart.zunyang.partypanic.adapter.in.props.screen;

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
import org.nowstart.zunyang.partypanic.application.dto.command.MovePropsActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsItemView;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPropsItemUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePropsActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.StartPropsArchiveUseCase;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class PropsArchiveScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;

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

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.PROPS_CARD), 0f, 0f, worldWidth, worldHeight);

        titleFont.draw(batch, propsView.title(), MARGIN, worldHeight - 36f);
        bodyFont.draw(batch, propsView.instructions(), MARGIN, worldHeight - 76f);
        bodyFont.draw(batch, "target: 필요한 상자 세 개만 골라 회수한다", MARGIN, worldHeight - 108f);

        drawRoom(worldWidth, worldHeight);
        drawStatusPanel(worldWidth);
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

    private void drawRoom(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + STATUS_HEIGHT + 24f;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float cellWidth = gridWidth / propsView.width();
        float cellHeight = GRID_HEIGHT / propsView.height();
        float roomTop = gridBottom + GRID_HEIGHT;

        batch.setColor(Color.valueOf("F0E5FA"));
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, gridBottom, gridWidth, GRID_HEIGHT);
        batch.setColor(Color.WHITE);

        for (PropsItemView item : propsView.items()) {
            float x = MARGIN + (item.x() * cellWidth) + (cellWidth * 0.12f);
            float y = gridBottom + (item.y() * cellHeight) + (cellHeight * 0.12f);
            float width = cellWidth * 0.76f;
            float height = cellHeight * 0.76f;

            TextureRegion region = textureLibrary.region(item.required() ? SampleTextureId.PROPS_CARD : SampleTextureId.LOCKED_CARD);
            batch.setColor(item.active() ? Color.WHITE : Color.valueOf(item.collected() ? "D9F7BE" : "B8B0C8"));
            batch.draw(region, x, y, width, height);
            batch.setColor(Color.WHITE);

            bodyFont.draw(batch, item.label(), x + 10f, y + height - 18f);
            String marker = item.collected()
                ? "회수 완료"
                : item.required() ? "필수" : "보류";
            bodyFont.draw(batch, marker, x + 10f, y + 26f);
        }

        drawActor(MARGIN, gridBottom, cellWidth, cellHeight);
        bodyFont.draw(batch, "facing: " + propsView.facing(), MARGIN, roomTop + 28f);
    }

    private void drawActor(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = cellWidth * 0.46f;
        float actorHeight = cellHeight * 0.64f;
        float actorX = gridLeft + (propsView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (propsView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 2f, actorY - 6f);
    }

    private void drawStatusPanel(float worldWidth) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, MARGIN, worldWidth - (MARGIN * 2f), STATUS_HEIGHT);
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            propsView.statusMessage(),
            MARGIN + 24f,
            MARGIN + 116f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
        bodyFont.draw(
            batch,
            "inventory: " + propsView.collectedRequiredCount() + " / " + propsView.requiredCount(),
            MARGIN + 24f,
            MARGIN + 66f
        );
        String footer = propsView.readyToReturn()
            ? "enter로 회수 결과를 확정한다"
            : "상자 앞에서 z로 조사해 필요한 소품만 챙긴다";
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
