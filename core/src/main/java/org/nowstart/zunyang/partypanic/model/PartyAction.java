package org.nowstart.zunyang.partypanic.model;

public record PartyAction(
        String id,
        String title,
        String description,
        String chatCommand,
        String streamerNote
) {
}
