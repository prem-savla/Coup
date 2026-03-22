package com.game.coup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStateRequest {

    @NotBlank
    private String roomId;

    @NotBlank
    private String playerName;
}