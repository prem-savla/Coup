package com.game.coup.dto.response.gamestate.option;

import java.util.List;

import com.game.coup.domain.model.Card;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevealOption {
    private final List<Card> validCards; 
}