package com.game.coup.dto.request;

import lombok.NoArgsConstructor;
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

    private Action action;
    private String playerName;
    private String roomId;
}