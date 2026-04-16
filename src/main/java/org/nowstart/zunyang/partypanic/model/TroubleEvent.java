package org.nowstart.zunyang.partypanic.model;

public record TroubleEvent(
        String id,
        String title,
        String instruction,
        int requiredResponses,
        String successText,
        String failureText
) {
}
