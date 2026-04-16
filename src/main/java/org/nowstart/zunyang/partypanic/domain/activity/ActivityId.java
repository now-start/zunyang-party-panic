package org.nowstart.zunyang.partypanic.domain.activity;

public enum ActivityId {
    BROADCAST_DESK("broadcast-desk", true),
    STORAGE_ROOM("storage-room", false),
    CAKE_TABLE("cake-table", true),
    PHOTO_TIME("photo-time", true),
    BACKSTAGE("backstage", false),
    FAN_LETTER("fan-letter", false),
    FINALE_STAGE("finale-stage", false);

    private final String code;
    private final boolean scored;

    ActivityId(String code, boolean scored) {
        this.code = code;
        this.scored = scored;
    }

    public String code() {
        return code;
    }

    public boolean isScored() {
        return scored;
    }
}
