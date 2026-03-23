package com.game.coup.dto.request;

import java.util.List;

import com.game.coup.domain.model.Card;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GameMoveRequest {

    @NotBlank
    private String playerName;

    @NotBlank
    private String choice; // action or response

    private String target;

    private List<Card> drawnCards;
    private List<Card> returnedCards;

    private Card revealCard;

}
