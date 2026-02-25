package com.game.coup.dto;

import com.game.coup.domain.definitions.Role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardDTO {

    private String id;
    private Role type;
}