package com.game.coup.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PlayerDTO {

    private String name;
    private List<CardDTO> playingCards;
    private List<CardDTO> revealedCards;
    private int coins;
    private boolean alive;
}