package org.nowstart.zunyang.partypanic.domain.signal;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum SignalControlId {
    MIC("마이크", new Position(1, 3), 1, new String[]{"작음", "안정", "강함"}),
    LAMP("데스크 램프", new Position(3, 3), 1, new String[]{"어두움", "부드러움", "강함"}),
    MONITOR("프리뷰 모니터", new Position(1, 1), 1, new String[]{"흐림", "또렷", "과다"}),
    CUE("큐 타이머", new Position(3, 1), 1, new String[]{"지연", "정시", "빠름"});

    private final String label;
    private final Position position;
    private final int targetLevel;
    private final String[] levelLabels;

    SignalControlId(String label, Position position, int targetLevel, String[] levelLabels) {
        this.label = label;
        this.position = position;
        this.targetLevel = targetLevel;
        this.levelLabels = levelLabels;
    }

    public String label() {
        return label;
    }

    public Position position() {
        return position;
    }

    public int targetLevel() {
        return targetLevel;
    }

    public String describeLevel(int level) {
        return levelLabels[level];
    }
}
