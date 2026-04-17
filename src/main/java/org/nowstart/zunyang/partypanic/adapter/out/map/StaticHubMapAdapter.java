package org.nowstart.zunyang.partypanic.adapter.out.map;

import org.nowstart.zunyang.partypanic.application.port.out.LoadMapPort;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.event.DialogueEvent;
import org.nowstart.zunyang.partypanic.domain.event.EventVisual;
import org.nowstart.zunyang.partypanic.domain.event.GameEvent;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.GameMap;
import org.nowstart.zunyang.partypanic.domain.model.Position;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StaticHubMapAdapter implements LoadMapPort {
    private static final String[] RAW_LAYOUT = {
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

    @Override
    public GameMap loadMap() {
        ParsedLayout parsedLayout = parseLayout();
        return new GameMap(parsedLayout.layout(), createEvents(parsedLayout.anchors()), parsedLayout.startingPosition());
    }

    private ParsedLayout parseLayout() {
        Map<Character, Position> anchors = new LinkedHashMap<>();
        List<String> normalizedLayout = new ArrayList<>();
        Position startingPosition = null;

        for (int row = 0; row < RAW_LAYOUT.length; row += 1) {
            String source = RAW_LAYOUT[row];
            StringBuilder normalizedRow = new StringBuilder(source.length());
            for (int column = 0; column < source.length(); column += 1) {
                char symbol = source.charAt(column);
                if (symbol == '@') {
                    startingPosition = new Position(column, row);
                    normalizedRow.append('.');
                    continue;
                }
                if (isAnchorSymbol(symbol)) {
                    anchors.put(symbol, new Position(column, row));
                    normalizedRow.append('.');
                    continue;
                }
                normalizedRow.append(symbol);
            }
            normalizedLayout.add(normalizedRow.toString());
        }

        if (startingPosition == null) {
            throw new IllegalStateException("Missing starting position in static hub map.");
        }
        return new ParsedLayout(normalizedLayout, anchors, startingPosition);
    }

    private boolean isAnchorSymbol(char symbol) {
        return switch (symbol) {
            case 'd', 's', 'c', 'p', 'b', 'l', 'f' -> true;
            default -> false;
        };
    }

    private List<GameEvent> createEvents(Map<Character, Position> anchors) {
        return List.of(
                dialogueEvent(
                        ActivityId.BROADCAST_DESK,
                        "방송 책상",
                        "방송 책상은 처음부터 사용할 수 있습니다.",
                        "책상 앞이다. 오늘 방송 첫 화면부터 맞춰 보자.",
                        EventVisual.DESK,
                        anchorOf(anchors, 'd')
                ),
                dialogueEvent(
                        ActivityId.STORAGE_ROOM,
                        "장식 창고",
                        "먼저 방송 책상을 정리해야 장식 창고 문이 열립니다.",
                        "창고 문이 열렸다. 오늘 쓸 장식을 추려 보자.",
                        EventVisual.DOOR,
                        anchorOf(anchors, 's')
                ),
                dialogueEvent(
                        ActivityId.CAKE_TABLE,
                        "케이크 테이블",
                        "장식 창고를 정리해야 케이크 테이블을 만질 수 있습니다.",
                        "케이크 자리다. 이제 오늘 방송의 중심 장면을 맞춰 보자.",
                        EventVisual.CAKE,
                        anchorOf(anchors, 'c')
                ),
                dialogueEvent(
                        ActivityId.PHOTO_TIME,
                        "포토존",
                        "케이크 테이블을 정리해야 포토존이 열립니다.",
                        "포토존이다. 오늘 남길 장면을 정리하러 가자.",
                        EventVisual.PHOTO,
                        anchorOf(anchors, 'p')
                ),
                dialogueEvent(
                        ActivityId.BACKSTAGE,
                        "백스테이지 복도",
                        "포토존을 마쳐야 복도 문이 열립니다.",
                        "조명이 약한 복도다. 남아 있던 기억 조각을 보러 가자.",
                        EventVisual.DOOR,
                        anchorOf(anchors, 'b')
                ),
                dialogueEvent(
                        ActivityId.FAN_LETTER,
                        "팬레터 우편함",
                        "백스테이지 복도에서 기억 조각을 확인해야 우편함이 열립니다.",
                        "우편함 앞에 서면 괜히 호흡부터 달라진다. 예전 편지를 열어 보자.",
                        EventVisual.MAILBOX,
                        anchorOf(anchors, 'l')
                ),
                dialogueEvent(
                        ActivityId.FINALE_STAGE,
                        "생일 방송 무대",
                        "팬레터를 확인해야 생일 방송 무대 문이 열립니다.",
                        "여기까지 왔다. 마지막 무대에 들어가 오늘 방송을 시작하자.",
                        EventVisual.STAGE,
                        anchorOf(anchors, 'f')
                )
        );
    }

    private DialogueEvent dialogueEvent(
            ActivityId activityId,
            String title,
            String lockedNotice,
            String interactionLine,
            EventVisual visual,
            Position position
    ) {
        return new DialogueEvent(
                activityId,
                title,
                Dialogue.singleSpeaker("치즈냥", List.of(lockedNotice)),
                Dialogue.singleSpeaker("치즈냥", List.of(interactionLine)),
                visual,
                position
        );
    }

    private Position anchorOf(Map<Character, Position> anchors, char symbol) {
        Position anchor = anchors.get(symbol);
        if (anchor == null) {
            throw new IllegalStateException("Missing map anchor for symbol: " + symbol);
        }
        return anchor;
    }

    private record ParsedLayout(List<String> layout, Map<Character, Position> anchors, Position startingPosition) {
    }
}
