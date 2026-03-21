package com.game.coup.dto;

import com.game.coup.domain.definitions.ActionType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameFlowRequest {

    @NotBlank
    private String roomId;

    private String playerName;

    private ActionType actionType;

    private String targetName;
}