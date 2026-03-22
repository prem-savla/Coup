package com.game.coup.dto.response.gamestate.option;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeOption {
    private final List<String> drawnCards; 
    private final List<String> playingCards; 
    private final int selectCount;
}