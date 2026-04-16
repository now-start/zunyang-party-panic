package org.nowstart.zunyang.partypanic.world;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class GameProgress {
    public static final String BROADCAST_DESK = "broadcast-desk";
    public static final String FAN_LETTER = "fan-letter";
    public static final String PHOTO_TIME = "photo-time";
    public static final String CAKE_TABLE = "cake-table";

    private final Set<String> completedActivities = new LinkedHashSet<>();
    private final Map<String, Integer> bestScores = new LinkedHashMap<>();

    public void recordScore(String activityId, int score) {
        completedActivities.add(activityId);
        bestScores.merge(activityId, score, Math::max);
    }

    public boolean isCompleted(String activityId) {
        return completedActivities.contains(activityId);
    }

    public boolean isUnlocked(String activityId) {
        return switch (activityId) {
            case BROADCAST_DESK -> true;
            case CAKE_TABLE -> isCompleted(BROADCAST_DESK);
            case PHOTO_TIME, FAN_LETTER -> isCompleted(BROADCAST_DESK) && isCompleted(CAKE_TABLE);
            default -> false;
        };
    }

    public int getBestScore(String activityId) {
        return bestScores.getOrDefault(activityId, 0);
    }

    public int getCompletedCount() {
        return completedActivities.size();
    }

    public String getNextObjective() {
        if (!isCompleted(BROADCAST_DESK)) {
            return "방송 책상부터 정리해 오늘의 생일 방송 무드를 고정하세요.";
        }
        if (!isCompleted(CAKE_TABLE)) {
            return "다음은 케이크 테이블을 정리해 피날레 무대를 안정화하세요.";
        }
        if (!isCompleted(PHOTO_TIME)) {
            return "포토존을 손봐서 생일 기념 장면을 준비하세요.";
        }
        if (!isCompleted(FAN_LETTER)) {
            return "마지막으로 팬레터를 읽고 감성 포인트를 채우세요.";
        }
        return "모든 준비가 끝났습니다. 이제 피날레 연출을 열 수 있습니다.";
    }
}
