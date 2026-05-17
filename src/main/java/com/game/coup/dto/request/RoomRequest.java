package com.game.coup.dto.request;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    public enum Action {
        CREATE,
        JOIN,
        START
    }
    @NonNull
    private Action action;
    @NotBlank
    private String playerName;
    private String roomId;
}