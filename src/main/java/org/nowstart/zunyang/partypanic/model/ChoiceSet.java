package org.nowstart.zunyang.partypanic.model;

import java.util.List;

public record ChoiceSet(
        String id,
        String prompt,
        String roundLabel,
        String resolutionText,
        List<PartyAction> actions
) {
    public ChoiceSet {
        actions = List.copyOf(actions);
    }
}
