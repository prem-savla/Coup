package com.game.coup.dto.response.gamestate.option;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevealOption {
    private final List<String> validCards; 
}