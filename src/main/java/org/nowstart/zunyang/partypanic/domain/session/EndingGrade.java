package org.nowstart.zunyang.partypanic.domain.session;

public enum EndingGrade {
    STEADY_START("무사 출발 엔딩", "실수 없이 출발했다. 오늘은 그걸로도 충분했다."),
    WARM_NIGHT("다정한 밤 엔딩", "정리해 둔 자리 덕분에, 첫 인사부터 온도가 있었다."),
    SHARED_STAGE("함께 만든 무대 엔딩", "보이는 건 한 사람이었지만, 오늘 무대는 혼자 완성된 게 아니었다.");

    private final String title;
    private final String summary;

    EndingGrade(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public String title() {
        return title;
    }

    public String summary() {
        return summary;
    }
}
