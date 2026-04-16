package org.nowstart.zunyang.partypanic.presentation.hub;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HubMapModel {
    public static final int TILE_SIZE = 48;

    private static final String[] MAP_LAYOUT = {
            "#######################",
            "#..l...............f..#",
            "#.....................#",
            "#.....s.........b.....#",
            "#.....................#",
            "#..d......c......p....#",
            "#........=====........#",
            "#.....................#",
            "#.....................#",
            "#..........@..........#",
            "#.....................#",
            "#.....................#",
            "#######################"
    };

    private final char[][] tiles = new char[rowCount()][columnCount()];
    private final Map<Character, GridPoint> anchors = new LinkedHashMap<>();
    private final List<HubMapEvent> events;
    private int playerTileX;
    private int playerTileY;
    private HubDirection facing = HubDirection.UP;

    public HubMapModel() {
        parseMapLayout();
        this.events = createEvents();
    }

    public int rowCount() {
        return MAP_LAYOUT.length;
    }

    public int columnCount() {
        return MAP_LAYOUT[0].length();
    }

    public float mapWidth() {
        return columnCount() * TILE_SIZE;
    }

    public float mapHeight() {
        return rowCount() * TILE_SIZE;
    }

    public char tileAt(int row, int column) {
        return tiles[row][column];
    }

    public List<HubMapEvent> events() {
        return events;
    }

    public int playerTileX() {
        return playerTileX;
    }

    public int playerTileY() {
        return playerTileY;
    }

    public HubDirection facing() {
        return facing;
    }

    public void setFacing(HubDirection direction) {
        facing = direction;
    }

    public void attemptStep(HubDirection direction) {
        int nextX = playerTileX + direction.dx();
        int nextY = playerTileY + direction.dy();
        if (!isWalkable(nextX, nextY)) {
            return;
        }
        playerTileX = nextX;
        playerTileY = nextY;
    }

    public HubMapEvent findFacingEvent() {
        return findEventAt(playerTileX + facing.dx(), playerTileY + facing.dy());
    }

    public HubMapEvent findSuggestedEvent(GameProgress progress) {
        for (HubMapEvent event : events) {
            if (!progress.isCompleted(event.id()) && progress.isUnlocked(event.id())) {
                return event;
            }
        }
        return null;
    }

    private void parseMapLayout() {
        for (int row = 0; row < rowCount(); row += 1) {
            String source = MAP_LAYOUT[row];
            for (int column = 0; column < columnCount(); column += 1) {
                char symbol = source.charAt(column);
                if (symbol == '@') {
                    playerTileX = column;
                    playerTileY = row;
                    tiles[row][column] = '.';
                    continue;
                }
                if (isAnchorSymbol(symbol)) {
                    anchors.put(symbol, new GridPoint(column, row));
                    tiles[row][column] = '.';
                    continue;
                }
                tiles[row][column] = symbol;
            }
        }
    }

    private boolean isAnchorSymbol(char symbol) {
        return switch (symbol) {
            case 'd', 's', 'c', 'p', 'b', 'l', 'f' -> true;
            default -> false;
        };
    }

    private List<HubMapEvent> createEvents() {
        return List.of(
                new HubMapEvent(
                        ActivityId.BROADCAST_DESK,
                        "방송 책상",
                        "방송 책상은 처음부터 사용할 수 있습니다.",
                        List.of("책상 앞이다. 오늘 방송 첫 화면부터 맞춰 보자."),
                        HubEventVisual.DESK,
                        anchorOf('d').x(),
                        anchorOf('d').y()
                ),
                new HubMapEvent(
                        ActivityId.STORAGE_ROOM,
                        "장식 창고",
                        "먼저 방송 책상을 정리해야 장식 창고 문이 열립니다.",
                        List.of("창고 문이 열렸다. 오늘 쓸 장식을 추려 보자."),
                        HubEventVisual.DOOR,
                        anchorOf('s').x(),
                        anchorOf('s').y()
                ),
                new HubMapEvent(
                        ActivityId.CAKE_TABLE,
                        "케이크 테이블",
                        "장식 창고를 정리해야 케이크 테이블을 만질 수 있습니다.",
                        List.of("케이크 자리다. 이제 오늘 방송의 중심 장면을 맞춰 보자."),
                        HubEventVisual.CAKE,
                        anchorOf('c').x(),
                        anchorOf('c').y()
                ),
                new HubMapEvent(
                        ActivityId.PHOTO_TIME,
                        "포토존",
                        "케이크 테이블을 정리해야 포토존이 열립니다.",
                        List.of("포토존이다. 오늘 남길 장면을 정리하러 가자."),
                        HubEventVisual.PHOTO,
                        anchorOf('p').x(),
                        anchorOf('p').y()
                ),
                new HubMapEvent(
                        ActivityId.BACKSTAGE,
                        "백스테이지 복도",
                        "포토존을 마쳐야 복도 문이 열립니다.",
                        List.of("조명이 약한 복도다. 남아 있던 기억 조각을 보러 가자."),
                        HubEventVisual.DOOR,
                        anchorOf('b').x(),
                        anchorOf('b').y()
                ),
                new HubMapEvent(
                        ActivityId.FAN_LETTER,
                        "팬레터 우편함",
                        "백스테이지 복도에서 기억 조각을 확인해야 우편함이 열립니다.",
                        List.of("우편함 앞에 서면 괜히 호흡부터 달라진다. 예전 편지를 열어 보자."),
                        HubEventVisual.MAILBOX,
                        anchorOf('l').x(),
                        anchorOf('l').y()
                ),
                new HubMapEvent(
                        ActivityId.FINALE_STAGE,
                        "생일 방송 무대",
                        "팬레터를 확인해야 생일 방송 무대 문이 열립니다.",
                        List.of("여기까지 왔다. 마지막 무대에 들어가 오늘 방송을 시작하자."),
                        HubEventVisual.STAGE,
                        anchorOf('f').x(),
                        anchorOf('f').y()
                )
        );
    }

    private GridPoint anchorOf(char symbol) {
        GridPoint point = anchors.get(symbol);
        if (point == null) {
            throw new IllegalStateException("Missing map anchor for symbol: " + symbol);
        }
        return point;
    }

    private boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= columnCount() || tileY < 0 || tileY >= rowCount()) {
            return false;
        }
        if (tiles[tileY][tileX] == '#') {
            return false;
        }
        return findEventAt(tileX, tileY) == null;
    }

    private HubMapEvent findEventAt(int tileX, int tileY) {
        for (HubMapEvent event : events) {
            if (event.tileX() == tileX && event.tileY() == tileY) {
                return event;
            }
        }
        return null;
    }

    private record GridPoint(int x, int y) {
    }
}
