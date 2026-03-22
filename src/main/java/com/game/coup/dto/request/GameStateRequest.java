package com.game.coup.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateRequest {
    
    @NotBlank
    private String playerName;
}