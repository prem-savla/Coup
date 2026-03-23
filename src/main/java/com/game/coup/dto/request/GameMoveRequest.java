package com.game.coup.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GameMoveRequest {

    @NotBlank
    private String playerName;

    @NotBlank
    private String choice; // action or response

    private String target;

    private List<String> cards;
}
