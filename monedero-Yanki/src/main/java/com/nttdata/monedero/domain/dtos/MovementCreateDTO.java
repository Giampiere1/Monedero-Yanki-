package com.nttdata.monedero.domain.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementCreateDTO {

    private String walletId;

    private String destinationFor;

    private String dateMovement;

    private BigDecimal amount;
    
}
