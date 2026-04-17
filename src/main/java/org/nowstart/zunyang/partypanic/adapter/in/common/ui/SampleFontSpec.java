package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public record SampleFontSpec(
    String assetPath,
    int pixelSize,
    float fallbackScale
) {

    public static SampleFontSpec forId(SampleFontId id) {
        return switch (id) {
            case TITLE -> new SampleFontSpec("assets/fonts/ui-title.ttf", 34, 2.0f);
            case BODY -> new SampleFontSpec("assets/fonts/ui-body.ttf", 20, 1.1f);
            case COMPACT -> new SampleFontSpec("assets/fonts/ui-body.ttf", 18, 1.05f);
        };
    }
}
