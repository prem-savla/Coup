package com.game.coup.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TreasuryDTO {

    private int coins;
}