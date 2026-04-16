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
import org.nowstart.zunyang.partypanic.world.GameProgress;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class StorySequenceScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.74f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.48f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);

    private final PartyPanicGame game;
    private final GameProgress progress;
    private final String activityId;
    private final String title;
    private final String subtitle;
    private final String returnNotice;
    private final String completionNotice;
    private final List<String> pages;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture hostTexture;

    private int pageIndex;

    public StorySequenceScreen(
            PartyPanicGame game,
            GameProgress progress,
            String activityId,
            String title,
            String subtitle,
            String backgroundPath,
            String returnNotice,
            String completionNotice,
            List<String> pages
    ) {
        this.game = game;
        this.progress = progress;
        this.activityId = activityId;
        this.title = title;
        this.subtitle = subtitle;
        this.returnNotice = returnNotice;
        this.completionNotice = completionNotice;
        this.pages = List.copyOf(pages);
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture(backgroundPath);
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
        drawStoryCard();
        if (showsOperationalUi()) {
            drawSidePanel();
            drawCommandBar();
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showHub(returnNotice);
            return;
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) && pageIndex > 0) {
            pageIndex -= 1;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (pageIndex < pages.size() - 1) {
                pageIndex += 1;
                return;
            }
            game.finishStoryChapter(activityId, completionNotice);
        }
    }

    private void drawStoryCard() {
        boolean operationalUi = showsOperationalUi();
        float cardX = operationalUi ? 72f : 112f;
        float cardY = 98f;
        float cardWidth = operationalUi ? 984f : 1376f;
        float cardHeight = 724f;
        float illustrationHeight = 342f;
        float illustrationWidth = illustrationHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float illustrationX = cardX + cardWidth - illustrationWidth - 48f;
        float illustrationY = cardY + 48f;
        float textWidth = operationalUi ? 520f : 760f;

        drawPanel(cardX, cardY, cardWidth, cardHeight, PANEL_STRONG);
        drawPanelOutline(cardX, cardY, cardWidth, cardHeight, BORDER_COLOR);

        drawLine("CHAPTER " + progress.getChapterNumber(activityId), cardX + 28f, cardY + cardHeight - 28f, 0.92f, TEXT_ACCENT);
        drawLine(title, cardX + 28f, cardY + cardHeight - 70f, 1.34f, TEXT_MINT);
        drawLine(subtitle, cardX + 28f, cardY + cardHeight - 104f, 0.90f, TEXT_MUTED);

        drawPageDots(cardX + 28f, cardY + cardHeight - 154f);
        drawParagraph(pages.get(pageIndex), cardX + 28f, cardY + 488f, textWidth, 1.06f, TEXT_PRIMARY);

        drawPanel(illustrationX - 16f, illustrationY - 16f, illustrationWidth + 32f, illustrationHeight + 32f, PANEL_COLOR);
        drawTextureFit(hostTexture, illustrationX, illustrationY, illustrationWidth, illustrationHeight);

        drawPanel(cardX + 28f, cardY + 46f, cardWidth - 56f, 92f, PANEL_COLOR);
        drawPanelOutline(cardX + 28f, cardY + 46f, cardWidth - 56f, 92f, BORDER_COLOR);
        drawParagraph(resolveBottomText(), cardX + 48f, cardY + 94f, cardWidth - 96f, 0.90f, TEXT_PRIMARY);
    }

    private void drawSidePanel() {
        float panelX = 1100f;
        float panelY = 98f;
        float panelWidth = 424f;
        float panelHeight = 724f;
        float left = panelX + 22f;
        float top = panelY + panelHeight - 28f;

        drawPanel(panelX, panelY, panelWidth, panelHeight, PANEL_STRONG);
        drawPanelOutline(panelX, panelY, panelWidth, panelHeight, BORDER_COLOR);

        drawLine("진행 상태", left, top, 1.10f, TEXT_ACCENT);
        drawLine("메인 루트 " + progress.getCompletedCount() + " / " + progress.getTotalActivityCount(), left, top - 34f, 0.92f, TEXT_PRIMARY);
        drawLine("예상 엔딩 " + progress.getEndingTitle(), left, top - 66f, 0.92f, TEXT_MINT);

        drawLine("현재 페이지", left, top - 126f, 0.98f, TEXT_ACCENT);
        drawLine((pageIndex + 1) + " / " + pages.size(), left, top - 162f, 1.50f, TEXT_PRIMARY);

        drawLine("현재 목표", left, top - 232f, 0.98f, TEXT_ACCENT);
        drawParagraph(progress.getNextObjective(), left, top - 270f, panelWidth - 44f, 0.86f, TEXT_PRIMARY);

        float noteY = panelY + 134f;
        drawPanel(left - 2f, noteY, panelWidth - 44f, 154f, PANEL_COLOR);
        drawPanelOutline(left - 2f, noteY, panelWidth - 44f, 154f, BORDER_COLOR);
        drawLine("메모", left + 14f, noteY + 124f, 0.96f, TEXT_ACCENT);
        drawParagraph("이 화면은 챕터 흐름과 대사 톤을 한 번에 확인하기 위한 샘플 서사 화면입니다. 마지막 페이지까지 넘기면 허브에 반영됩니다.", left + 14f, noteY + 82f, panelWidth - 74f, 0.82f, TEXT_PRIMARY);
        drawParagraph(progress.isCompleted(activityId) ? "이미 한 번 완료한 챕터입니다." : "아직 완료되지 않은 챕터입니다.", left + 14f, noteY + 30f, panelWidth - 74f, 0.82f, TEXT_MUTED);
    }

    private void drawCommandBar() {
        drawPanel(72f, 42f, 1452f, 38f, PANEL_STRONG);
        drawPanelOutline(72f, 42f, 1452f, 38f, BORDER_COLOR);
        drawLine(resolveCommandText(), 94f, 68f, 0.84f, TEXT_PRIMARY);
    }

    private void drawPageDots(float startX, float baselineY) {
        for (int index = 0; index < pages.size(); index += 1) {
            Color color = index == pageIndex ? TEXT_ACCENT : BORDER_COLOR;
            drawPanel(startX + (index * 20f), baselineY, 12f, 12f, color);
        }
    }

    private String resolveBottomText() {
        if (pageIndex == pages.size() - 1) {
            return "이 페이지에서 ENTER 또는 SPACE를 누르면 챕터가 완료 처리되고 허브로 돌아갑니다.";
        }
        return "ENTER 또는 SPACE로 다음 페이지, LEFT 또는 A로 이전 페이지, ESC로 허브 복귀";
    }

    private String resolveCommandText() {
        if (pageIndex == pages.size() - 1) {
            return "ENTER / SPACE 완료 후 허브 복귀 | LEFT / A 이전 페이지 | ESC 허브 복귀";
        }
        return "ENTER / SPACE 다음 페이지 | LEFT / A 이전 페이지 | ESC 허브 복귀";
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
        List<String> lines = wrapText(text, width, scale);
        float cursorY = y;
        for (String line : lines) {
            drawLine(line, x, cursorY, scale, color);
            cursorY -= 28f * scale;
        }
    }

    private List<String> wrapText(String text, float width, float scale) {
        String[] words = text.split(" ");
        List<String> lines = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder();

        for (String word : words) {
            String candidate = lineBuilder.length() == 0 ? word : lineBuilder + " " + word;
            if (estimateWidth(candidate, scale) > width && lineBuilder.length() > 0) {
                lines.add(lineBuilder.toString());
                lineBuilder.setLength(0);
                lineBuilder.append(word);
                continue;
            }
            lineBuilder.setLength(0);
            lineBuilder.append(candidate);
        }

        if (!lineBuilder.isEmpty()) {
            lines.add(lineBuilder.toString());
        }

        return lines;
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

        List<String> fontTexts = new ArrayList<>(pages);
        fontTexts.add(title);
        fontTexts.add(subtitle);
        fontTexts.add(returnNotice);
        fontTexts.add(completionNotice);
        fontTexts.add(progress.getNextObjective());
        fontTexts.add(progress.getEndingTitle());
        fontTexts.add(progress.getEndingLine());
        fontTexts.addAll(List.of(
                "CHAPTER",
                "진행 상태",
                "메인 루트",
                "예상 엔딩",
                "현재 페이지",
                "현재 목표",
                "메모",
                "이 화면은 챕터 흐름과 대사 톤을 한 번에 확인하기 위한 샘플 서사 화면입니다. 마지막 페이지까지 넘기면 허브에 반영됩니다.",
                "이미 한 번 완료한 챕터입니다.",
                "아직 완료되지 않은 챕터입니다.",
                "이 페이지에서 ENTER 또는 SPACE를 누르면 챕터가 완료 처리되고 허브로 돌아갑니다.",
                "ENTER 또는 SPACE로 다음 페이지, LEFT 또는 A로 이전 페이지, ESC로 허브 복귀",
                "ENTER / SPACE 완료 후 허브 복귀 | LEFT / A 이전 페이지 | ESC 허브 복귀",
                "ENTER / SPACE 다음 페이지 | LEFT / A 이전 페이지 | ESC 허브 복귀"
        ));

        for (String text : fontTexts) {
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

    private boolean showsOperationalUi() {
        return game.getConfig().showsOperationalUi();
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
