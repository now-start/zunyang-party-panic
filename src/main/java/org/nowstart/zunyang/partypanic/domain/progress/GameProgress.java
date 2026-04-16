package org.nowstart.zunyang.partypanic.domain.progress;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GameProgress {
    private static final List<ActivityId> MAIN_ROUTE = List.of(
            ActivityId.BROADCAST_DESK,
            ActivityId.STORAGE_ROOM,
            ActivityId.CAKE_TABLE,
            ActivityId.PHOTO_TIME,
            ActivityId.BACKSTAGE,
            ActivityId.FAN_LETTER,
            ActivityId.FINALE_STAGE
    );

    private final Set<ActivityId> completedActivities = new LinkedHashSet<>();
    private final Map<ActivityId, Integer> bestScores = new LinkedHashMap<>();

    public void markCompleted(ActivityId activityId) {
        completedActivities.add(activityId);
    }

    public void recordScore(ActivityId activityId, int score) {
        markCompleted(activityId);
        bestScores.merge(activityId, score, Math::max);
    }

    public boolean isCompleted(ActivityId activityId) {
        return completedActivities.contains(activityId);
    }

    public boolean isUnlocked(ActivityId activityId) {
        return switch (activityId) {
            case BROADCAST_DESK -> true;
            case STORAGE_ROOM -> isCompleted(ActivityId.BROADCAST_DESK);
            case CAKE_TABLE -> isCompleted(ActivityId.STORAGE_ROOM);
            case PHOTO_TIME -> isCompleted(ActivityId.CAKE_TABLE);
            case BACKSTAGE -> isCompleted(ActivityId.PHOTO_TIME);
            case FAN_LETTER -> isCompleted(ActivityId.BACKSTAGE);
            case FINALE_STAGE -> isCompleted(ActivityId.FAN_LETTER);
            default -> false;
        };
    }

    public int getBestScore(ActivityId activityId) {
        return bestScores.getOrDefault(activityId, 0);
    }

    public int getCompletedCount() {
        return completedActivities.size();
    }

    public int getTotalActivityCount() {
        return MAIN_ROUTE.size();
    }

    public int getChapterNumber(ActivityId activityId) {
        int index = MAIN_ROUTE.indexOf(activityId);
        return index >= 0 ? index + 1 : 0;
    }

    public int getTotalBestScore() {
        return getBestScore(ActivityId.BROADCAST_DESK)
                + getBestScore(ActivityId.CAKE_TABLE)
                + getBestScore(ActivityId.PHOTO_TIME);
    }

    public String getNextObjective() {
        if (!isCompleted(ActivityId.BROADCAST_DESK)) {
            return "방송 책상부터 정리해 오늘 방송 첫 화면의 리듬을 맞추세요.";
        }
        if (!isCompleted(ActivityId.STORAGE_ROOM)) {
            return "장식 창고를 열어 케이크와 포토존에 쓸 소품을 챙기세요.";
        }
        if (!isCompleted(ActivityId.CAKE_TABLE)) {
            return "케이크 테이블을 완성해 오늘 방송의 중심 장면을 세우세요.";
        }
        if (!isCompleted(ActivityId.PHOTO_TIME)) {
            return "포토존을 정리해 오늘 방송에 남길 장면을 찍어 두세요.";
        }
        if (!isCompleted(ActivityId.BACKSTAGE)) {
            return "백스테이지 복도로 들어가 남아 있던 기억 조각을 확인하세요.";
        }
        if (!isCompleted(ActivityId.FAN_LETTER)) {
            return "팬레터 우편함에서 예전 편지를 다시 읽고 마지막 문을 여세요.";
        }
        if (!isCompleted(ActivityId.FINALE_STAGE)) {
            return "생일 방송 무대로 이동해 오늘 방송을 시작할 준비를 마무리하세요.";
        }
        return "샘플 루프 완료. 점수를 갱신하거나 다시 둘러보며 엔딩 톤을 바꿔 볼 수 있습니다.";
    }

    public String getEndingTitle() {
        int totalScore = getTotalBestScore();
        if (totalScore >= 240) {
            return "진심 엔딩";
        }
        if (totalScore >= 170) {
            return "따뜻한 엔딩";
        }
        return "조용한 엔딩";
    }

    public String getEndingLine() {
        return switch (getEndingTitle()) {
            case "진심 엔딩" -> "남아 있던 장면을 다시 보고 나니까, 오늘 방송 장면도 더 오래 남을 것 같다.";
            case "따뜻한 엔딩" -> "준비가 끝난 자리라는 게, 이렇게 든든할 수도 있네.";
            default -> "완벽하진 않아도, 오늘 방송은 시작됐다.";
        };
    }
}
