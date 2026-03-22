package com.game.coup.dto.response.gamestate.option;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActionOption {
    private final List<String> validActions;
    private final List<String> validTargets;
}