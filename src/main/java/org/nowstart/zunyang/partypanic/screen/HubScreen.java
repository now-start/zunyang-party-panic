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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HubScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;
    private static final float STAGE_X = 36f;
    private static final float STAGE_Y = 116f;
    private static final float STAGE_WIDTH = 1096f;
    private static final float STAGE_HEIGHT = 644f;
    private static final float PANEL_X = 1160f;
    private static final float PANEL_Y = 116f;
    private static final float PANEL_WIDTH = 404f;
    private static final float PANEL_HEIGHT = 756f;
    private static final float COMMAND_X = STAGE_X;
    private static final float COMMAND_Y = 34f;
    private static final float COMMAND_WIDTH = STAGE_WIDTH;
    private static final float COMMAND_HEIGHT = 62f;

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.76f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color STAGE_FRAME = new Color(0.18f, 0.10f, 0.14f, 0.45f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.48f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.96f, 0.61f, 0.71f, 0.92f);
    private static final Color HIGHLIGHT_MINT = new Color(0.68f, 0.90f, 0.83f, 0.92f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);

    private final PartyPanicGame game;
    private final GameProgress progress;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture roomTexture;
    private final Texture hostTexture;
    private final Map<String, Texture> spotTextures = new LinkedHashMap<>();
    private final List<HubSpot> spots;

    private int selectedIndex;
    private String notice;

    public HubScreen(PartyPanicGame game, GameProgress progress, String notice) {
        this.game = game;
        this.progress = progress;
        this.notice = notice;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.roomTexture = ScreenSupport.loadTexture("images/backgrounds/desk-party-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("images/characters/zunyang-birthday-host.png");

        spotTextures.put(GameProgress.BROADCAST_DESK, ScreenSupport.loadTexture("images/backgrounds/desk-party-stage.png"));
        spotTextures.put(GameProgress.FAN_LETTER, ScreenSupport.loadTexture("images/choices/fan-letter-card.png"));
        spotTextures.put(GameProgress.PHOTO_TIME, ScreenSupport.loadTexture("images/choices/photo-time-card.png", "images/events/photo-time-card.png"));
        spotTextures.put(GameProgress.CAKE_TABLE, ScreenSupport.loadTexture("images/events/cake-balance-card.png"));

        this.spots = List.of(
                new HubSpot(GameProgress.BROADCAST_DESK, "방송 책상", "현재 구현됨", "오늘 생일 방송 무드를 고정하고 바로 플레이 가능한 메인 미니게임입니다.", 110f, 282f, 220f, 178f),
                new HubSpot(GameProgress.FAN_LETTER, "팬레터 우편함", "다음 구현 슬롯", "팬레터를 읽고 감성 포인트를 쌓는 짧은 대화형 미니게임 슬롯입니다.", 328f, 152f, 188f, 164f),
                new HubSpot(GameProgress.CAKE_TABLE, "케이크 테이블", "현재 구현됨", "기울어지는 장식과 케이크 밸런스를 정리하는 사고 수습형 미니게임입니다. 방송 책상 완료 후 잠금이 해제됩니다.", 514f, 316f, 202f, 170f),
                new HubSpot(GameProgress.PHOTO_TIME, "포토존 카메라", "현재 구현됨", "사진 구도와 포즈 타이밍을 맞추는 생일 포토타임 미니게임입니다. 방송 책상과 케이크 정리 완료 후 열립니다.", 760f, 246f, 206f, 178f)
        );
        this.selectedIndex = findFirstAvailableIndex();
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (game.getScreen() != this) {
            return;
        }

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawBackdrop();
        drawRoomFrame();
        drawRoomDecor();
        drawSpotCards();
        drawStatusPanel();
        drawCommandBar();
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex + spots.size() - 1) % spots.size();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            selectedIndex = (selectedIndex + 1) % spots.size();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectedIndex = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectedIndex = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectedIndex = 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            selectedIndex = 3;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = WINDOW_HEIGHT - Gdx.input.getY();
            for (int index = 0; index < spots.size(); index += 1) {
                if (spots.get(index).contains(mouseX, mouseY)) {
                    selectedIndex = index;
                    activateSelectedSpot();
                    return;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            activateSelectedSpot();
        }
    }

    private void activateSelectedSpot() {
        HubSpot selectedSpot = spots.get(selectedIndex);
        if (GameProgress.BROADCAST_DESK.equals(selectedSpot.id())) {
            game.showBroadcastDeskMinigame();
            return;
        }

        if (GameProgress.CAKE_TABLE.equals(selectedSpot.id())) {
            if (progress.isUnlocked(GameProgress.CAKE_TABLE)) {
                game.showCakeTableMinigame();
            } else {
                notice = "먼저 방송 책상 미니게임을 완료해야 케이크 테이블이 열립니다.";
            }
            return;
        }

        if (GameProgress.PHOTO_TIME.equals(selectedSpot.id())) {
            if (progress.isUnlocked(GameProgress.PHOTO_TIME)) {
                game.showPhotoTimeMinigame();
            } else {
                notice = "포토존은 방송 책상과 케이크 테이블을 모두 끝내야 열립니다.";
            }
            return;
        }

        notice = selectedSpot.title() + " 슬롯은 다음 구현 단계입니다.";
    }

    private void drawBackdrop() {
        drawTextureCover(roomTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
    }

    private void drawRoomFrame() {
        drawPanel(STAGE_X - 8f, STAGE_Y - 8f, STAGE_WIDTH + 16f, STAGE_HEIGHT + 16f, STAGE_FRAME);
        drawTextureCover(roomTexture, STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.04f, 0.03f, 0.03f, 0.16f));
        drawPanelOutline(STAGE_X - 1f, STAGE_Y - 1f, STAGE_WIDTH + 2f, STAGE_HEIGHT + 2f, BORDER_COLOR);

        drawPanel(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, PANEL_STRONG);
        drawPanelOutline(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, BORDER_COLOR);

        drawPanel(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, PANEL_STRONG);
        drawPanelOutline(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, BORDER_COLOR);
    }

    private void drawRoomDecor() {
        float titleX = STAGE_X + 28f;
        float titleY = STAGE_Y + STAGE_HEIGHT - 18f;
        float cardX = STAGE_X + 30f;
        float cardY = STAGE_Y + STAGE_HEIGHT - 132f;
        float cardWidth = 472f;
        float cardHeight = 92f;

        drawLine("치즈냥 생일 준비방", titleX, titleY, 1.28f, TEXT_ACCENT);
        drawPanel(cardX, cardY, cardWidth, cardHeight, PANEL_COLOR);
        drawPanelOutline(cardX, cardY, cardWidth, cardHeight, BORDER_COLOR);
        drawParagraph(progress.getNextObjective(), cardX + 20f, cardY + 54f, cardWidth - 40f, 0.94f, TEXT_PRIMARY);

        float drawHeight = 430f;
        float drawWidth = drawHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float drawX = STAGE_X + STAGE_WIDTH - drawWidth - 26f;
        float drawY = STAGE_Y + 30f;

        drawPanel(drawX - 18f, drawY - 16f, drawWidth + 36f, drawHeight + 32f, new Color(0.14f, 0.08f, 0.12f, 0.38f));
        drawTextureFit(hostTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawSpotCards() {
        for (int index = 0; index < spots.size(); index += 1) {
            HubSpot spot = spots.get(index);
            Texture iconTexture = spotTextures.get(spot.id());
            boolean selected = index == selectedIndex;
            boolean completed = progress.isCompleted(spot.id());
            Color outlineColor = completed ? HIGHLIGHT_MINT : selected ? HIGHLIGHT_COLOR : BORDER_COLOR;

            drawPanel(spot.x() - 10f, spot.y() - 10f, spot.width() + 20f, spot.height() + 68f, new Color(0.12f, 0.08f, 0.11f, 0.78f));
            drawTextureCover(iconTexture, spot.x(), spot.y() + 30f, spot.width(), spot.height());
            drawPanel(spot.x(), spot.y() + 30f, spot.width(), spot.height(), new Color(0.04f, 0.03f, 0.04f, 0.18f));
            drawPanelOutline(spot.x(), spot.y() + 30f, spot.width(), spot.height(), outlineColor);

            drawLine((index + 1) + ". " + spot.title(), spot.x(), spot.y() + 16f, 0.88f, selected ? TEXT_ACCENT : TEXT_PRIMARY);
            drawLine(resolveSpotStatus(spot, completed), spot.x(), spot.y() - 6f, 0.78f, completed ? TEXT_MINT : TEXT_MUTED);
        }
    }

    private void drawStatusPanel() {
        HubSpot selectedSpot = spots.get(selectedIndex);
        float left = PANEL_X + 22f;
        float top = PANEL_Y + PANEL_HEIGHT - 24f;

        drawLine("허브 진행도", left, top, 1.16f, TEXT_ACCENT);
        drawLine("완료한 준비 " + progress.getCompletedCount() + " / " + spots.size(), left, top - 34f, 0.94f, TEXT_PRIMARY);
        drawLine("선택 중 " + selectedSpot.title(), left, top - 68f, 0.96f, TEXT_PRIMARY);
        drawLine(selectedSpot.subtitle(), left, top - 98f, 0.88f, progress.isCompleted(selectedSpot.id()) ? TEXT_MINT : TEXT_MUTED);

        float detailY = top - 150f;
        drawLine("현재 선택 설명", left, detailY, 1.00f, TEXT_ACCENT);
        drawParagraph(selectedSpot.description(), left, detailY - 36f, PANEL_WIDTH - 44f, 0.92f, TEXT_PRIMARY);

        float statusCardY = PANEL_Y + 244f;
        drawPanel(left - 2f, statusCardY, PANEL_WIDTH - 44f, 118f, PANEL_COLOR);
        drawPanelOutline(left - 2f, statusCardY, PANEL_WIDTH - 44f, 118f, BORDER_COLOR);
        drawLine("방송 책상 최고 점수", left + 14f, statusCardY + 92f, 0.88f, TEXT_MUTED);
        drawLine(String.valueOf(progress.getBestScore(GameProgress.BROADCAST_DESK)), left + 14f, statusCardY + 54f, 1.70f, TEXT_MINT);
        drawLine("케이크 최고 점수 " + progress.getBestScore(GameProgress.CAKE_TABLE), left + 134f, statusCardY + 92f, 0.84f, TEXT_MUTED);
        drawLine("포토 최고 점수 " + progress.getBestScore(GameProgress.PHOTO_TIME), left + 134f, statusCardY + 70f, 0.84f, TEXT_MUTED);
        drawParagraph("현재 실제 진입 가능한 슬롯은 방송 책상, 케이크 테이블, 포토존 세 개입니다. 팬레터는 다음 단계에서 이어 붙입니다.", left + 14f, statusCardY + 18f, PANEL_WIDTH - 74f, 0.80f, TEXT_PRIMARY);

        float noticeY = PANEL_Y + 82f;
        drawLine("최근 알림", left, noticeY + 88f, 1.00f, TEXT_ACCENT);
        drawParagraph(resolveNotice(), left, noticeY + 48f, PANEL_WIDTH - 44f, 0.88f, TEXT_PRIMARY);
    }

    private void drawCommandBar() {
        drawLine("현재 입력: 방향키 또는 1 2 3 4 로 포인트 선택, ENTER/SPACE 또는 클릭으로 상호작용", COMMAND_X + 22f, COMMAND_Y + 38f, 0.92f, TEXT_PRIMARY);
    }

    private String resolveSpotStatus(HubSpot spot, boolean completed) {
        if (completed) {
            return "완료";
        }
        if (GameProgress.BROADCAST_DESK.equals(spot.id())) {
            return "플레이 가능";
        }
        if (GameProgress.CAKE_TABLE.equals(spot.id())) {
            return progress.isUnlocked(GameProgress.CAKE_TABLE) ? "플레이 가능" : "잠김";
        }
        if (GameProgress.PHOTO_TIME.equals(spot.id())) {
            return progress.isUnlocked(GameProgress.PHOTO_TIME) ? "플레이 가능" : "잠김";
        }
        return "준비중";
    }

    private String resolveNotice() {
        if (notice != null && !notice.isBlank()) {
            return notice;
        }
        if (!progress.isCompleted(GameProgress.BROADCAST_DESK)) {
            return "먼저 방송 책상을 정리해 현재 프로토타입 미니게임으로 들어가세요.";
        }
        if (!progress.isUnlocked(GameProgress.CAKE_TABLE)) {
            return "방송 책상 완료 후 케이크 테이블이 열립니다.";
        }
        if (!progress.isUnlocked(GameProgress.PHOTO_TIME)) {
            return "이제 케이크 테이블을 정리해 포토존 카메라를 열어야 합니다.";
        }
        if (!progress.isCompleted(GameProgress.PHOTO_TIME)) {
            return "포토존에 들어가 생일 기념 샷 3컷을 남기세요.";
        }
        return "이제 팬레터 우편함이나 피날레 구조를 붙일 단계입니다.";
    }

    private int findFirstAvailableIndex() {
        for (int index = 0; index < spots.size(); index += 1) {
            HubSpot spot = spots.get(index);
            if (!progress.isCompleted(spot.id()) && isPlayable(spot.id())) {
                return index;
            }
        }
        for (int index = 0; index < spots.size(); index += 1) {
            if (!progress.isCompleted(spots.get(index).id())) {
                return index;
            }
        }
        return 0;
    }

    private boolean isPlayable(String activityId) {
        return switch (activityId) {
            case GameProgress.BROADCAST_DESK -> true;
            case GameProgress.CAKE_TABLE -> progress.isUnlocked(GameProgress.CAKE_TABLE);
            case GameProgress.PHOTO_TIME -> progress.isUnlocked(GameProgress.PHOTO_TIME);
            default -> false;
        };
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
        return text.length() * 11.6f * scale;
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
                "치즈냥 생일 준비방",
                "방송 책상",
                "팬레터 우편함",
                "케이크 테이블",
                "포토존 카메라",
                "현재 구현됨",
                "다음 구현 슬롯",
                "오늘 생일 방송 무드를 고정하고 바로 플레이 가능한 메인 미니게임입니다.",
                "팬레터를 읽고 감성 포인트를 쌓는 짧은 대화형 미니게임 슬롯입니다.",
                "기울어지는 장식과 케이크 밸런스를 정리하는 사고 수습형 미니게임 슬롯입니다.",
                "기울어지는 장식과 케이크 밸런스를 정리하는 사고 수습형 미니게임입니다. 방송 책상 완료 후 잠금이 해제됩니다.",
                "사진 구도와 포즈 타이밍을 맞추는 생일 포토타임 미니게임입니다. 방송 책상과 케이크 정리 완료 후 열립니다.",
                "허브 진행도",
                "완료한 준비 ",
                "선택 중 ",
                "현재 선택 설명",
                "방송 책상 최고 점수",
                "케이크 최고 점수 ",
                "포토 최고 점수 ",
                "현재 실제 진입 가능한 슬롯은 방송 책상, 케이크 테이블, 포토존 세 개입니다. 팬레터는 다음 단계에서 이어 붙입니다.",
                "최근 알림",
                "먼저 방송 책상을 정리해 현재 프로토타입 미니게임으로 들어가세요.",
                "먼저 방송 책상 미니게임을 완료해야 케이크 테이블이 열립니다.",
                "포토존은 방송 책상과 케이크 테이블을 모두 끝내야 열립니다.",
                "이제 케이크 테이블을 정리해 포토존 카메라를 열어야 합니다.",
                "포토존에 들어가 생일 기념 샷 3컷을 남기세요.",
                "이제 팬레터 우편함이나 피날레 구조를 붙일 단계입니다.",
                "현재 입력: 방향키 또는 1 2 3 4 로 포인트 선택, ENTER/SPACE 또는 클릭으로 상호작용",
                "완료",
                "플레이 가능",
                "잠김",
                "준비중",
                "치즈냥의 생일 준비방에 들어왔습니다.",
                "방송 책상 슬롯은 현재 미니게임으로 연결됩니다.",
                "방송 책상 준비 완료. 최고 점수 ",
                "케이크 테이블 정리 완료. 최고 점수 ",
                "포토존 촬영 완료. 최고 점수 ",
                "허브로 복귀했습니다. H로 돌아가면 결과를 저장할 수 있습니다.",
                " 슬롯은 다음 구현 단계입니다.",
                "점",
                progress.getNextObjective()
        )) {
            appendCharacters(characters, text);
        }

        for (HubSpot spot : spotsForCharacters()) {
            appendCharacters(characters, spot.title());
            appendCharacters(characters, spot.subtitle());
            appendCharacters(characters, spot.description());
        }

        StringBuilder builder = new StringBuilder(characters.size());
        for (Character character : characters) {
            builder.append(character);
        }
        return builder.toString();
    }

    private List<HubSpot> spotsForCharacters() {
        return List.of(
                new HubSpot(GameProgress.BROADCAST_DESK, "방송 책상", "현재 구현됨", "오늘 생일 방송 무드를 고정하고 바로 플레이 가능한 메인 미니게임입니다.", 0f, 0f, 0f, 0f),
                new HubSpot(GameProgress.FAN_LETTER, "팬레터 우편함", "다음 구현 슬롯", "팬레터를 읽고 감성 포인트를 쌓는 짧은 대화형 미니게임 슬롯입니다.", 0f, 0f, 0f, 0f),
                new HubSpot(GameProgress.CAKE_TABLE, "케이크 테이블", "현재 구현됨", "기울어지는 장식과 케이크 밸런스를 정리하는 사고 수습형 미니게임입니다. 방송 책상 완료 후 잠금이 해제됩니다.", 0f, 0f, 0f, 0f),
                new HubSpot(GameProgress.PHOTO_TIME, "포토존 카메라", "현재 구현됨", "사진 구도와 포즈 타이밍을 맞추는 생일 포토타임 미니게임입니다. 방송 책상과 케이크 정리 완료 후 열립니다.", 0f, 0f, 0f, 0f)
        );
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
        roomTexture.dispose();
        hostTexture.dispose();
        for (Texture texture : spotTextures.values()) {
            texture.dispose();
        }
    }

    private record HubSpot(String id, String title, String subtitle, String description, float x, float y, float width, float height) {
        private boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y + 30f && py <= y + height + 30f;
        }
    }
}
