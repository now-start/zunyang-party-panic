package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.PartyPanicGame;
import org.nowstart.zunyang.partypanic.world.GameProgress;

import java.util.ArrayList;
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
    private static final float STAGE_WIDTH = 1528f;
    private static final float STAGE_HEIGHT = 744f;
    private static final float MOVE_SPEED = 320f;
    private static final float INTERACTION_RADIUS = 118f;

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.74f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color STAGE_FRAME = new Color(0.18f, 0.10f, 0.14f, 0.45f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.48f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.96f, 0.61f, 0.71f, 0.92f);
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

    private float playerX;
    private float playerY;
    private String notice;

    public HubScreen(PartyPanicGame game, GameProgress progress, String notice) {
        this.game = game;
        this.progress = progress;
        this.notice = notice;
        this.spots = List.of(
                new HubSpot(GameProgress.BROADCAST_DESK, "방송 책상", "방송 첫 화면과 리듬을 맞춥니다.", "방송 책상은 처음부터 사용할 수 있습니다.", 132f, 512f, 176f, 118f),
                new HubSpot(GameProgress.STORAGE_ROOM, "장식 창고", "케이크와 포토존에 쓸 소품을 챙깁니다.", "먼저 방송 책상을 정리해야 장식 창고 문이 열립니다.", 404f, 624f, 162f, 98f),
                new HubSpot(GameProgress.CAKE_TABLE, "케이크 테이블", "오늘 방송의 중심 장면을 완성합니다.", "장식 창고를 정리해야 케이크 테이블이 열립니다.", 650f, 416f, 180f, 118f),
                new HubSpot(GameProgress.PHOTO_TIME, "포토존", "오늘 방송에 남길 장면을 찍습니다.", "케이크 테이블을 마쳐야 포토존이 열립니다.", 1008f, 396f, 184f, 118f),
                new HubSpot(GameProgress.BACKSTAGE, "백스테이지 복도", "기억 조각을 확인하는 구간입니다.", "포토존을 마쳐야 복도 문이 열립니다.", 1284f, 622f, 164f, 98f),
                new HubSpot(GameProgress.FAN_LETTER, "팬레터 우편함", "예전 편지를 다시 읽고 마지막 문을 엽니다.", "백스테이지 복도에서 기억 조각을 확인해야 우편함이 열립니다.", 280f, 200f, 162f, 98f),
                new HubSpot(GameProgress.FINALE_STAGE, "생일 방송 무대", "오늘 준비를 한 화면에 모아 보는 피날레입니다.", "팬레터를 확인해야 생일 방송 무대 문이 열립니다.", 1274f, 176f, 188f, 122f)
        );
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.roomTexture = ScreenSupport.loadTexture("images/backgrounds/desk-party-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("images/characters/zunyang-birthday-host.png");

        spotTextures.put(GameProgress.BROADCAST_DESK, ScreenSupport.loadTexture("images/backgrounds/desk-party-stage.png"));
        spotTextures.put(GameProgress.STORAGE_ROOM, ScreenSupport.loadTexture("images/backgrounds/cake-rush-stage.png"));
        spotTextures.put(GameProgress.CAKE_TABLE, ScreenSupport.loadTexture("images/events/cake-balance-card.png"));
        spotTextures.put(GameProgress.PHOTO_TIME, ScreenSupport.loadTexture("images/events/photo-time-card.png"));
        spotTextures.put(GameProgress.BACKSTAGE, ScreenSupport.loadTexture("images/backgrounds/mint-cats-stage.png"));
        spotTextures.put(GameProgress.FAN_LETTER, ScreenSupport.loadTexture("images/choices/fan-letter-card.png"));
        spotTextures.put(GameProgress.FINALE_STAGE, ScreenSupport.loadTexture("images/backgrounds/finale-stage.png"));

        HubSpot suggestedSpot = findSuggestedSpot();
        this.playerX = clampPlayerX(STAGE_X + suggestedSpot.centerX());
        this.playerY = clampPlayerY(STAGE_Y + suggestedSpot.centerY() - 92f);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        if (game.getScreen() != this) {
            return;
        }

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawBackdrop();
        drawFrames();
        drawHubMap();
        drawStatusPanel();
        batch.end();
    }

    private void handleInput(float delta) {
        Vector2 movement = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= 1f;
        }

        if (!movement.isZero()) {
            movement.nor().scl(MOVE_SPEED * delta);
            playerX = clampPlayerX(playerX + movement.x);
            playerY = clampPlayerY(playerY + movement.y);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showTitle();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)
                || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            HubSpot nearbySpot = findNearbySpot();
            if (nearbySpot == null) {
                notice = "상호작용할 오브젝트 가까이로 이동하세요.";
                return;
            }
            activateSpot(nearbySpot);
        }
    }

    private void activateSpot(HubSpot spot) {
        if (!progress.isUnlocked(spot.id())) {
            notice = spot.lockedNotice();
            return;
        }

        switch (spot.id()) {
            case GameProgress.BROADCAST_DESK -> game.showBroadcastDeskMinigame();
            case GameProgress.STORAGE_ROOM -> game.showStorageRoomScene();
            case GameProgress.CAKE_TABLE -> game.showCakeTableMinigame();
            case GameProgress.PHOTO_TIME -> game.showPhotoTimeMinigame();
            case GameProgress.BACKSTAGE -> game.showBackstageScene();
            case GameProgress.FAN_LETTER -> game.showFanLetterScene();
            case GameProgress.FINALE_STAGE -> game.showFinaleStage();
            default -> notice = "아직 연결되지 않은 오브젝트입니다.";
        }
    }

    private void drawBackdrop() {
        drawTextureCover(roomTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
    }

    private void drawFrames() {
        drawPanel(STAGE_X - 8f, STAGE_Y - 8f, STAGE_WIDTH + 16f, STAGE_HEIGHT + 16f, STAGE_FRAME);
        drawTextureCover(roomTexture, STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.04f, 0.03f, 0.03f, 0.18f));
        drawPanelOutline(STAGE_X - 1f, STAGE_Y - 1f, STAGE_WIDTH + 2f, STAGE_HEIGHT + 2f, BORDER_COLOR);
    }

    private void drawHubMap() {
        HubSpot nearbySpot = findNearbySpot();
        float infoX = STAGE_X + 28f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 138f;
        float infoWidth = 560f;
        float infoHeight = 102f;

        drawLine("치즈냥 생일 준비방", STAGE_X + 28f, STAGE_Y + STAGE_HEIGHT - 22f, 1.24f, TEXT_ACCENT);
        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawParagraph(progress.getNextObjective(), infoX + 20f, infoY + 58f, infoWidth - 40f, 0.92f, TEXT_PRIMARY);
        drawLine(resolvePrompt(nearbySpot), infoX + 20f, infoY + 22f, 0.82f, nearbySpot == null ? TEXT_MUTED : TEXT_MINT);

        for (HubSpot spot : spots) {
            drawSpot(spot, nearbySpot);
        }

        drawPlayer();
    }

    private void drawSpot(HubSpot spot, HubSpot nearbySpot) {
        Texture texture = spotTextures.get(spot.id());
        boolean completed = progress.isCompleted(spot.id());
        boolean unlocked = progress.isUnlocked(spot.id());
        boolean nearby = nearbySpot != null && nearbySpot.id().equals(spot.id());
        Color border = completed ? TEXT_MINT : nearby ? HIGHLIGHT_COLOR : BORDER_COLOR;
        float alpha = unlocked ? 1f : 0.45f;

        batch.setColor(1f, 1f, 1f, alpha);
        drawTextureCover(texture, STAGE_X + spot.x(), STAGE_Y + spot.y(), spot.width(), spot.height());
        batch.setColor(Color.WHITE);

        drawPanel(STAGE_X + spot.x(), STAGE_Y + spot.y(), spot.width(), spot.height(), new Color(0.05f, 0.03f, 0.04f, unlocked ? 0.16f : 0.46f));
        drawPanelOutline(STAGE_X + spot.x(), STAGE_Y + spot.y(), spot.width(), spot.height(), border);
        drawLine(spot.title(), STAGE_X + spot.x(), STAGE_Y + spot.y() - 10f, 0.82f, unlocked ? TEXT_PRIMARY : TEXT_MUTED);
        if (showsOperationalUi()) {
            drawLine(resolveSpotStatus(spot, unlocked, completed), STAGE_X + spot.x(), STAGE_Y + spot.y() - 30f, 0.74f, completed ? TEXT_MINT : unlocked ? TEXT_ACCENT : TEXT_MUTED);
        }
    }

    private void drawPlayer() {
        float drawHeight = 108f;
        float drawWidth = drawHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float drawX = playerX - (drawWidth * 0.5f);
        float drawY = playerY - 10f;

        drawPanel(playerX - 28f, playerY - 4f, 56f, 12f, new Color(0f, 0f, 0f, 0.32f));
        drawPanel(drawX - 10f, drawY - 8f, drawWidth + 20f, drawHeight + 16f, new Color(0.14f, 0.08f, 0.12f, 0.28f));
        drawTextureFit(hostTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawStatusPanel() {
        HubSpot nearbySpot = findNearbySpot();

        if (!showsOperationalUi()) {
            drawLiveStatusPanel(nearbySpot);
            return;
        }

        float progressX = STAGE_X + STAGE_WIDTH - 274f;
        float progressY = STAGE_Y + STAGE_HEIGHT - 118f;
        float progressWidth = 246f;
        float progressHeight = 90f;
        float noticeX = STAGE_X + 28f;
        float noticeY = STAGE_Y + 26f;
        float noticeWidth = 458f;
        float noticeHeight = 96f;
        float promptX = STAGE_X + STAGE_WIDTH - 422f;
        float promptY = STAGE_Y + 26f;
        float promptWidth = 394f;
        float promptHeight = 112f;

        drawPanel(progressX, progressY, progressWidth, progressHeight, PANEL_STRONG);
        drawPanelOutline(progressX, progressY, progressWidth, progressHeight, BORDER_COLOR);
        drawLine("루트 " + progress.getCompletedCount() + " / " + progress.getTotalActivityCount(), progressX + 18f, progressY + 62f, 0.94f, TEXT_PRIMARY);
        drawLine(progress.getEndingTitle(), progressX + 18f, progressY + 30f, 1.02f, TEXT_MINT);

        drawPanel(noticeX, noticeY, noticeWidth, noticeHeight, PANEL_COLOR);
        drawPanelOutline(noticeX, noticeY, noticeWidth, noticeHeight, BORDER_COLOR);
        drawParagraph(resolveNotice(), noticeX + 18f, noticeY + 58f, noticeWidth - 36f, 0.84f, TEXT_PRIMARY);
        drawLine("WASD 이동  E 상호작용  ESC 타이틀", noticeX + 18f, noticeY + 22f, 0.76f, TEXT_MUTED);

        if (nearbySpot == null) {
            return;
        }

        drawPanel(promptX, promptY, promptWidth, promptHeight, PANEL_STRONG);
        drawPanelOutline(promptX, promptY, promptWidth, promptHeight, nearbySpot.id().equals(findSuggestedSpot().id()) ? HIGHLIGHT_COLOR : BORDER_COLOR);
        drawLine(nearbySpot.title(), promptX + 18f, promptY + 82f, 0.98f, TEXT_ACCENT);
        drawParagraph(nearbySpot.description(), promptX + 18f, promptY + 50f, promptWidth - 36f, 0.80f, TEXT_PRIMARY);
        drawLine(resolvePrompt(nearbySpot), promptX + 18f, promptY + 20f, 0.76f, TEXT_MUTED);
    }

    private void drawLiveStatusPanel(HubSpot nearbySpot) {
        float chipX = STAGE_X + STAGE_WIDTH - 220f;
        float chipY = STAGE_Y + STAGE_HEIGHT - 84f;
        float chipWidth = 192f;
        float chipHeight = 56f;

        drawPanel(chipX, chipY, chipWidth, chipHeight, PANEL_STRONG);
        drawPanelOutline(chipX, chipY, chipWidth, chipHeight, BORDER_COLOR);
        drawLine(progress.getCompletedCount() + " / " + progress.getTotalActivityCount(), chipX + 18f, chipY + 36f, 0.90f, TEXT_PRIMARY);
        drawLine(progress.getEndingTitle(), chipX + 18f, chipY + 16f, 0.78f, TEXT_MINT);

        if (nearbySpot == null) {
            return;
        }

        float promptWidth = 420f;
        float promptHeight = 74f;
        float promptX = STAGE_X + ((STAGE_WIDTH - promptWidth) * 0.5f);
        float promptY = STAGE_Y + 24f;
        Color outlineColor = progress.isUnlocked(nearbySpot.id()) ? HIGHLIGHT_COLOR : BORDER_COLOR;

        drawPanel(promptX, promptY, promptWidth, promptHeight, PANEL_STRONG);
        drawPanelOutline(promptX, promptY, promptWidth, promptHeight, outlineColor);
        drawLine(nearbySpot.title(), promptX + 18f, promptY + 48f, 0.92f, TEXT_ACCENT);
        drawLine(resolvePrompt(nearbySpot), promptX + 18f, promptY + 22f, 0.78f, TEXT_PRIMARY);
    }

    private HubSpot findSuggestedSpot() {
        for (HubSpot spot : spots) {
            if (!progress.isCompleted(spot.id()) && progress.isUnlocked(spot.id())) {
                return spot;
            }
        }
        return spots.get(0);
    }

    private HubSpot findNearbySpot() {
        HubSpot bestSpot = null;
        float bestDistance = Float.MAX_VALUE;

        for (HubSpot spot : spots) {
            float distance = Vector2.dst(playerX, playerY, STAGE_X + spot.centerX(), STAGE_Y + spot.centerY());
            if (distance <= INTERACTION_RADIUS && distance < bestDistance) {
                bestDistance = distance;
                bestSpot = spot;
            }
        }

        return bestSpot;
    }

    private String resolveSpotStatus(HubSpot spot, boolean unlocked, boolean completed) {
        if (completed) {
            return "완료";
        }
        if (!unlocked) {
            return "잠김";
        }
        if (spot.id().equals(findSuggestedSpot().id())) {
            return "다음 목표";
        }
        return "진입 가능";
    }

    private String resolvePrompt(HubSpot nearbySpot) {
        if (nearbySpot == null) {
            return "오브젝트 가까이에서 E 또는 ENTER를 누르면 챕터가 시작됩니다.";
        }
        if (!progress.isUnlocked(nearbySpot.id())) {
            return nearbySpot.lockedNotice();
        }
        return nearbySpot.title() + " 상호작용 가능";
    }

    private String resolveNotice() {
        if (notice != null && !notice.isBlank()) {
            return notice;
        }
        return "샘플 허브입니다. 메인 루트를 따라가며 쯔꾸르풍 진행 구조를 한 바퀴 확인할 수 있습니다.";
    }

    private float clampPlayerX(float candidate) {
        return Math.max(STAGE_X + 48f, Math.min(STAGE_X + STAGE_WIDTH - 48f, candidate));
    }

    private float clampPlayerY(float candidate) {
        return Math.max(STAGE_Y + 38f, Math.min(STAGE_Y + STAGE_HEIGHT - 38f, candidate));
    }

    private boolean showsOperationalUi() {
        return game.getConfig().showsOperationalUi();
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

        List<String> fontTexts = new ArrayList<>();
        fontTexts.add(progress.getNextObjective());
        fontTexts.add(progress.getEndingTitle());
        fontTexts.add(resolveNotice());
        fontTexts.addAll(List.of(
                "치즈냥 생일 준비방",
                "허브 진행도",
                "메인 루트",
                "예상 엔딩",
                "현재 위치",
                "준비방 중앙",
                "다음 목표 쪽으로 걸어가 E 또는 ENTER로 상호작용하세요.",
                "루트",
                "최고 점수",
                "합계",
                "최근 알림",
                "WASD 이동  E 상호작용  ESC 타이틀",
                "상호작용할 오브젝트 가까이로 이동하세요.",
                "샘플 허브입니다. 메인 루트를 따라가며 쯔꾸르풍 진행 구조를 한 바퀴 확인할 수 있습니다.",
                "오브젝트 가까이에서 E 또는 ENTER를 누르면 챕터가 시작됩니다.",
                "완료",
                "잠김",
                "다음 목표",
                "진입 가능",
                "상호작용 가능"
        ));

        for (HubSpot spot : spots) {
            fontTexts.add(spot.title());
            fontTexts.add(spot.description());
            fontTexts.add(spot.lockedNotice());
        }

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

    private record HubSpot(
            String id,
            String title,
            String description,
            String lockedNotice,
            float x,
            float y,
            float width,
            float height
    ) {
        private float centerX() {
            return x + (width * 0.5f);
        }

        private float centerY() {
            return y + (height * 0.5f);
        }
    }
}
