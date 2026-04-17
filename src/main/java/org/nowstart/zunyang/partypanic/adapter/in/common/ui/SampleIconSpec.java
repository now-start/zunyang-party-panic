package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.graphics.Color;

public record SampleIconSpec(
    int size,
    Color fill,
    Color panel,
    Color border,
    Color accent
) {

    public static SampleIconSpec forId(SampleIconId id) {
        return switch (id) {
            case MOVE -> new SampleIconSpec(
                64,
                Color.valueOf("29465B"),
                Color.valueOf("4E6E81"),
                Color.valueOf("F2F7FF"),
                Color.valueOf("FFE082")
            );
            case INTERACT -> new SampleIconSpec(
                64,
                Color.valueOf("4E2F5D"),
                Color.valueOf("7A4D94"),
                Color.valueOf("FFF4FF"),
                Color.valueOf("FFD8A8")
            );
            case OPEN_CHAPTER -> new SampleIconSpec(
                64,
                Color.valueOf("365B43"),
                Color.valueOf("5F8B6B"),
                Color.valueOf("F2FFF5"),
                Color.valueOf("FFF3BF")
            );
            case NEXT -> new SampleIconSpec(
                64,
                Color.valueOf("334155"),
                Color.valueOf("64748B"),
                Color.valueOf("F8FAFC"),
                Color.valueOf("F59E0B")
            );
            case SKIP -> new SampleIconSpec(
                64,
                Color.valueOf("5B3A29"),
                Color.valueOf("8C5A3C"),
                Color.valueOf("FFF7ED"),
                Color.valueOf("FED7AA")
            );
            case DEBUG -> new SampleIconSpec(
                64,
                Color.valueOf("3F3F46"),
                Color.valueOf("71717A"),
                Color.valueOf("FAFAFA"),
                Color.valueOf("C4B5FD")
            );
            case RESTART -> new SampleIconSpec(
                64,
                Color.valueOf("4C1D95"),
                Color.valueOf("7C3AED"),
                Color.valueOf("F5F3FF"),
                Color.valueOf("DDD6FE")
            );
            case HUB -> new SampleIconSpec(
                64,
                Color.valueOf("14532D"),
                Color.valueOf("15803D"),
                Color.valueOf("F0FDF4"),
                Color.valueOf("BBF7D0")
            );
        };
    }
}
