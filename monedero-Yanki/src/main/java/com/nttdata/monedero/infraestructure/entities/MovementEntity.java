package com.nttdata.monedero.infraestructure.entities;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Document(value = "movement")
public class MovementEntity {

    private String id;

    private String walletId;

    private String destinationFor;

    private String dateMovement;

    private BigDecimal amount;

}
