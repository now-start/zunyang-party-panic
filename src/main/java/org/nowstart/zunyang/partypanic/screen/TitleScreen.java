package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.PartyPanicGame;

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

    public TitleScreen(PartyPanicGame game) {
        this.game = game;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture("images/backgrounds/finale-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("images/characters/zunyang-birthday-host.png");
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (game.getScreen() != this) {
            return;
        }

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawTextureCover(backgroundTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
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
        float drawY = 132f;

        drawPanel(drawX - 24f, drawY - 20f, drawWidth + 48f, drawHeight + 40f, new Color(0.14f, 0.08f, 0.12f, 0.38f));
        drawTextureFit(hostTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawTitleCard() {
        float cardX = 86f;
        float cardY = 168f;
        float cardWidth = 652f;
        float cardHeight = 434f;

        drawPanel(cardX, cardY, cardWidth, cardHeight, PANEL_COLOR);
        drawPanelOutline(cardX, cardY, cardWidth, cardHeight, BORDER_COLOR);

        drawLine("zunyang-party-panic", cardX + 34f, cardY + cardHeight - 38f, 1.58f, TEXT_ACCENT);
        drawLine("치즈냥 생일 팬게임", cardX + 34f, cardY + cardHeight - 86f, 1.16f, TEXT_MINT);
        drawParagraph("쯔꾸르풍 준비방을 돌아다니며 미니게임을 열고, 오늘의 생일 방송과 파티를 완성합니다.", cardX + 34f, cardY + 278f, cardWidth - 68f, 1.02f, TEXT_PRIMARY);
        drawParagraph("현재 구현: 허브, 방송 책상 미니게임, 케이크 테이블 미니게임.", cardX + 34f, cardY + 220f, cardWidth - 68f, 0.92f, TEXT_MUTED);

        float buttonX = cardX + 34f;
        float buttonY = cardY + 92f;
        float buttonWidth = 280f;
        float buttonHeight = 54f;
        drawPanel(buttonX, buttonY, buttonWidth, buttonHeight, TEXT_ACCENT);
        drawPanelOutline(buttonX, buttonY, buttonWidth, buttonHeight, BORDER_COLOR);
        drawLine("ENTER / SPACE  시작", buttonX + 20f, buttonY + 34f, 0.98f, Color.BLACK);

        drawLine("ESC / Q  종료", cardX + 34f, cardY + 48f, 0.90f, TEXT_MUTED);
    }

    private void drawPanel(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixelTexture, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void drawPanelOutline(float x, float y, float width, float height, Color color) {
        drawPanel(x, y, width, 2f, color);
        drawPanel(x, y + height - 2f, width, 2f, color);
        drawPanel(x, y, 2f, height, color);
        drawPanel(x + width - 2f, y, 2f, height, color);
    }

    private void drawTextureCover(Texture texture, float x, float y, float width, float height) {
        float targetAspect = width / height;
        float textureAspect = texture.getWidth() / (float) texture.getHeight();
        int srcX = 0;
        int srcY = 0;
        int srcWidth = texture.getWidth();
        int srcHeight = texture.getHeight();

        if (textureAspect > targetAspect) {
            srcWidth = Math.round(texture.getHeight() * targetAspect);
            srcX = (texture.getWidth() - srcWidth) / 2;
        } else if (textureAspect < targetAspect) {
            srcHeight = Math.round(texture.getWidth() / targetAspect);
            srcY = (texture.getHeight() - srcHeight) / 2;
        }

        batch.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
    }

    private void drawTextureFit(Texture texture, float x, float y, float width, float height) {
        float scale = Math.min(width / texture.getWidth(), height / texture.getHeight());
        float drawWidth = texture.getWidth() * scale;
        float drawHeight = texture.getHeight() * scale;
        float drawX = x + ((width - drawWidth) * 0.5f);
        float drawY = y + ((height - drawHeight) * 0.5f);
        batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawParagraph(String text, float x, float y, float width, float scale, Color color) {
        String[] words = text.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        float cursorY = y;

        for (String word : words) {
            String candidate = lineBuilder.length() == 0 ? word : lineBuilder + " " + word;
            if (estimateWidth(candidate, scale) > width && lineBuilder.length() > 0) {
                drawLine(lineBuilder.toString(), x, cursorY, scale, color);
                lineBuilder.setLength(0);
                lineBuilder.append(word);
                cursorY -= 24f * scale;
                continue;
            }
            lineBuilder.setLength(0);
            lineBuilder.append(candidate);
        }

        if (!lineBuilder.isEmpty()) {
            drawLine(lineBuilder.toString(), x, cursorY, scale, color);
        }
    }

    private float estimateWidth(String text, float scale) {
        return text.length() * 11.4f * scale;
    }

    private void drawLine(String text, float x, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(TEXT_PRIMARY);
        font.getData().setScale(1f);
    }

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        for (String text : List.of(
                "zunyang-party-panic",
                "치즈냥 생일 팬게임",
                "쯔꾸르풍 준비방을 돌아다니며 미니게임을 열고, 오늘의 생일 방송과 파티를 완성합니다.",
                "현재 구현: 허브, 방송 책상 미니게임, 케이크 테이블 미니게임.",
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
