package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.PartyPanicGame;
import org.nowstart.zunyang.partypanic.screen.ui.PixelUiRenderer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TitleScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.70f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.52f);

    private final PartyPanicGame game;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture hostTexture;
    private final PixelUiRenderer ui;
    private float sceneTime;

    public TitleScreen(PartyPanicGame game) {
        this.game = game;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture("assets/images/backgrounds/finale-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
        this.ui = new PixelUiRenderer(batch, font, pixelTexture);
    }

    @Override
    public void render(float delta) {
        sceneTime += delta;
        handleInput();
        if (game.getScreen() != this) {
            return;
        }

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        ui.textureCover(backgroundTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        ui.panel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
        drawHero();
        drawTitleCard();
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.showHub("치즈냥의 생일 준비방에 들어왔습니다.");
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.exit();
        }
    }

    private void drawHero() {
        float drawHeight = 620f;
        float drawWidth = drawHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float drawX = 946f;
        float drawY = 132f + (MathUtils.sin(sceneTime * 1.6f) * 6f);

        ui.panel(drawX - 24f, drawY - 20f, drawWidth + 48f, drawHeight + 40f, new Color(0.14f, 0.08f, 0.12f, 0.38f));
        ui.textureFit(hostTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawTitleCard() {
        boolean operationalUi = game.getConfig().showsOperationalUi();
        float cardX = 86f;
        float cardY = 168f;
        float cardWidth = 652f;
        float cardHeight = 434f;

        ui.panel(cardX, cardY, cardWidth, cardHeight, PANEL_COLOR);
        ui.panelOutline(cardX, cardY, cardWidth, cardHeight, BORDER_COLOR);

        ui.line("zunyang-party-panic", cardX + 34f, cardY + cardHeight - 38f, 1.58f, TEXT_ACCENT);
        ui.line("치즈냥 생일 팬게임", cardX + 34f, cardY + cardHeight - 86f, 1.16f, TEXT_MINT);
        ui.paragraph("치즈냥이 직접 타일 기반 준비방을 걸으며 조사와 대사로 생일 방송 시작 직전의 장면을 정리하는 전통 2D 쯔꾸르 팬게임입니다.", cardX + 34f, cardY + 278f, cardWidth - 68f, 0.98f, TEXT_PRIMARY);
        ui.paragraph(
                operationalUi
                        ? "현재 test 모드: 타일 좌표와 진행 정보 같은 검증용 정보가 추가 표시됩니다."
                        : "현재 live 모드: 4방향 이동과 하단 대사창 중심의 실제 플레이 화면으로 표시됩니다.",
                cardX + 34f,
                cardY + 214f,
                cardWidth - 68f,
                0.88f,
                TEXT_MUTED
        );

        float buttonX = cardX + 34f;
        float buttonY = cardY + 92f;
        float buttonWidth = 280f;
        float buttonHeight = 54f;
        float buttonGlow = 0.84f + (0.16f * ((MathUtils.sin(sceneTime * 2.8f) * 0.5f) + 0.5f));
        ui.panel(buttonX, buttonY, buttonWidth, buttonHeight, withAlpha(TEXT_ACCENT, buttonGlow));
        ui.panelOutline(buttonX, buttonY, buttonWidth, buttonHeight, BORDER_COLOR);
        ui.line("ENTER / SPACE  시작", buttonX + 20f, buttonY + 34f, 0.98f, Color.BLACK);

        ui.line("ESC / Q  종료", cardX + 34f, cardY + 48f, 0.90f, TEXT_MUTED);
    }

    private Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        for (String text : List.of(
                "zunyang-party-panic",
                "치즈냥 생일 팬게임",
                "치즈냥이 직접 타일 기반 준비방을 걸으며 조사와 대사로 생일 방송 시작 직전의 장면을 정리하는 전통 2D 쯔꾸르 팬게임입니다.",
                "현재 test 모드: 타일 좌표와 진행 정보 같은 검증용 정보가 추가 표시됩니다.",
                "현재 live 모드: 4방향 이동과 하단 대사창 중심의 실제 플레이 화면으로 표시됩니다.",
                "ENTER / SPACE  시작",
                "ESC / Q  종료"
        )) {
            appendCharacters(characters, text);
        }

        StringBuilder builder = new StringBuilder(characters.size());
        for (Character character : characters) {
            builder.append(character);
        }
        return builder.toString();
    }

    private void appendCharacters(Set<Character> characters, String text) {
        for (int index = 0; index < text.length(); index += 1) {
            characters.add(text.charAt(index));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelTexture.dispose();
        backgroundTexture.dispose();
        hostTexture.dispose();
    }
}
