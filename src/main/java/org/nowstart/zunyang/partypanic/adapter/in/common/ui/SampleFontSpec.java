package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public record SampleFontSpec(
    int pixelSize,
    float fallbackScale
) {

    public static SampleFontSpec forId(SampleFontId id) {
        return switch (id) {
            case TITLE -> new SampleFontSpec(34, 2.0f);
            case BODY -> new SampleFontSpec(20, 1.1f);
            case COMPACT -> new SampleFontSpec(18, 1.05f);
        };
    }
}
