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
@Document(value = "wallet")
public class WalletEntity {

    private String id;

    private String typeDocumentIdentification;

    private String numberIdentification;

    private String numberPhone;

    private String imeiPhone;

    private String email;

    private String cardDebitAssociate;

    private BigDecimal totalBalance;

}
