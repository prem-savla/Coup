package com.game.coup.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameContextDTO {

    private final String roomId;

    private final PlayerPoolDTO playerPool;

    private final DeckDTO deck;

    private final TreasuryDTO treasury;

    private final Integer currentTurnIndex;

    private final String phase;
}