package org.nowstart.zunyang.partypanic.application.story;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.List;

public final class StoryChapterFactory {
    public StoryChapter create(ActivityId activityId, GameProgress progress) {
        return switch (activityId) {
            case STORAGE_ROOM -> new StoryChapter(
                    ActivityId.STORAGE_ROOM,
                    "장식 창고",
                    "오늘 쓸 소품을 고르는 파트",
                    "assets/images/backgrounds/cake-rush-stage.png",
                    "장식 창고에서 허브로 복귀했습니다.",
                    "장식 창고 정리 완료. 케이크 테이블을 만질 수 있습니다.",
                    List.of(
                            "장식 창고 문을 열자 리본이랑 전구 상자가 한꺼번에 보였다. 오늘 쓸 것만 고르면 된다.",
                            "안 쓰던 상자까지 전부 꺼내 놓으면 끝이 없다. 지금 필요한 건 케이크랑 포토존, 그리고 마지막 무대에 둘 것들이다.",
                            "좋아. 리본, 초, 작은 소품은 챙겼다. 이제 오늘 쓸 건 어느 정도 손에 잡힌다."
                    )
            );
            case BACKSTAGE -> new StoryChapter(
                    ActivityId.BACKSTAGE,
                    "백스테이지 복도",
                    "준비에서 기억으로 넘어가는 구간",
                    "assets/images/backgrounds/mint-cats-stage.png",
                    "백스테이지 복도에서 허브로 복귀했습니다.",
                    "복도에서 기억 조각을 확인했습니다. 팬레터 우편함이 열렸습니다.",
                    List.of(
                            "복도 조명이 약해서 그런지, 여기만 들어오면 방송 시작 직전보다 더 조용해진다.",
                            "안 버리고 남겨 둔 상자랑 메모가 꽤 많다. 완전한 기록은 아니어도 이런 조각은 오래 남는다.",
                            "오늘이 갑자기 생긴 건 아니었다는 건 알겠다. 이제 팬레터 우편함도 열어 봐야겠다."
                    )
            );
            case FAN_LETTER -> new StoryChapter(
                    ActivityId.FAN_LETTER,
                    "팬레터 우편함",
                    "예전 편지를 다시 읽는 파트",
                    "assets/images/backgrounds/desk-party-stage.png",
                    "팬레터 우편함에서 허브로 복귀했습니다.",
                    "팬레터를 확인했습니다. 마지막 무대 문이 열렸습니다.",
                    List.of(
                            "여긴 결국 보게 되는구나. 지금 열면 여러 생각이 들 것 같긴 한데, 방송 켜기 전에 한 번은 보고 가야 할 것 같기도 하다.",
                            "짧은 문장인데도 오래 남는 말이 있다. 늘 웃게 해 줘서 고마워요. 이번 생일도 따뜻한 하루였으면 좋겠어요.",
                            "예전 생일 방송도 아직 기억하고 있어요. 오늘만큼은 누구보다 많이 축하받았으면 좋겠어요.",
                            "읽기 전이랑 읽고 난 뒤 분위기가 다르다. 응. 이제 방송 켜기 전에 보고 갈 건 다 본 것 같다."
                    )
            );
            case FINALE_STAGE -> new StoryChapter(
                    ActivityId.FINALE_STAGE,
                    "생일 방송 무대",
                    progress.getEndingTitle() + " 예정",
                    "assets/images/backgrounds/finale-stage.png",
                    "생일 방송 무대에서 허브로 복귀했습니다.",
                    progress.getEndingTitle() + " 샘플 엔딩을 확인했습니다. 준비방에서 다시 둘러볼 수 있습니다.",
                    List.of(
                            "마지막 문이 열리자 준비방에서 만졌던 것들이 한 장면으로 모인다.",
                            "책상도, 케이크도, 포토존도 이제는 대충 임시로 놓인 것 같지 않다.",
                            "남아 있던 장면도 있었고, 남아 있던 말도 있었다. 그래서 지금 여기까지 이어진 거겠지.",
                            progress.getEndingLine() + " 좋아. 이제는 시작할 수 있겠다."
                    )
            );
            default -> throw new IllegalArgumentException("Story chapter not supported: " + activityId);
        };
    }
}
