package org.nowstart.zunyang.partypanic.content;

import org.nowstart.zunyang.partypanic.model.ChoiceSet;
import org.nowstart.zunyang.partypanic.model.PartyAction;
import org.nowstart.zunyang.partypanic.model.TroubleEvent;

import java.util.List;

public final class GameContent {
    private final List<ChoiceSet> choiceSets;
    private final List<TroubleEvent> troubleEvents;

    public GameContent(List<ChoiceSet> choiceSets, List<TroubleEvent> troubleEvents) {
        this.choiceSets = List.copyOf(choiceSets);
        this.troubleEvents = List.copyOf(troubleEvents);
    }

    public static GameContent defaultContent() {
        return new GameContent(
                List.of(
                        new ChoiceSet(
                                "decor-stage",
                                "첫 순서입니다. 오늘의 메인 배경을 골라 주세요.",
                                "1차 투표",
                                "선택된 무대 테마로 방송 세트가 정리됩니다.",
                                List.of(
                                        new PartyAction("mint-cats", "민트 고양이 무드", "말랑한 고양이 풍선과 민트색 조명을 깝니다.", "!민트", "방송 첫인상을 가장 귀엽게 시작하는 안정적인 선택"),
                                        new PartyAction("cake-rush", "케이크 폭죽 무드", "케이크 오브제와 반짝이 폭죽을 전면에 배치합니다.", "!케이크", "초반 텐션을 빠르게 올리지만 후반 정리가 조금 빡빡함"),
                                        new PartyAction("desk-party", "책상 캠 파티 무드", "머그컵, 노트북, SD 일러스트 분위기를 강조합니다.", "!책상", "치즈냥의 평소 방송 분위기와 가장 닮아 있음")
                                )
                        ),
                        new ChoiceSet(
                                "gift-route",
                                "두 번째 순서입니다. 팬들이 준비할 생일 이벤트를 골라 주세요.",
                                "2차 투표",
                                "팬 선택 이벤트가 중반 진행 루트로 확정됩니다.",
                                List.of(
                                        new PartyAction("fan-letter", "팬레터 낭독", "고양이 우편함에서 팬레터를 하나씩 꺼내 읽습니다.", "!편지", "감성 점수가 높아 피날레 응원 연결에 유리"),
                                        new PartyAction("mini-game", "즉석 미니게임", "채팅으로 간단한 반응전을 열어 텐션을 올립니다.", "!게임", "중간 사고가 터져도 회복 속도가 빠름"),
                                        new PartyAction("photo-time", "생일 포토타임", "기념 포즈와 소품 컷을 빠르게 모읍니다.", "!포토", "화면이 화사해지지만 채팅 집중 유지가 중요")
                                )
                        )
                ),
                List.of(
                        new TroubleEvent(
                                "audio-pop",
                                "문제 1: 축하 BGM이 갑자기 끊깁니다.",
                                "채팅 반응으로 분위기를 유지해야 합니다. 테스트 채팅이나 스트리머의 긴급 정리 콜로 버텨 주세요.",
                                4,
                                "채팅이 빈 자리를 채워 BGM 공백을 무사히 넘겼습니다.",
                                "반응이 부족해 초반 분위기가 약간 처졌습니다."
                        ),
                        new TroubleEvent(
                                "cake-balance",
                                "문제 2: 케이크 장식이 한쪽으로 기웁니다.",
                                "시청자들이 재빨리 의견을 모으고 스트리머가 정리 콜을 넣어야 균형을 잡습니다.",
                                5,
                                "케이크가 무너지기 전에 균형을 되찾았습니다.",
                                "케이크가 살짝 망가져 복구 시간이 늘어났습니다."
                        ),
                        new TroubleEvent(
                                "camera-chaos",
                                "문제 3: 포토타임 카메라 구도가 흔들립니다.",
                                "마지막 위기입니다. 응원과 콜을 모아 무대 화면을 안정화하세요.",
                                5,
                                "구도가 정리되어 피날레 준비가 깔끔하게 끝났습니다.",
                                "정리는 되었지만 피날레 준비 시간이 조금 줄었습니다."
                        )
                )
        );
    }

    public List<ChoiceSet> choiceSets() {
        return choiceSets;
    }

    public List<TroubleEvent> troubleEvents() {
        return troubleEvents;
    }
}
