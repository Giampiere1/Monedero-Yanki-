package com.nttdata.monedero.infraestructure.entities;

import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

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


    public WalletEntity toEntity(WalletCreateDTO walletCreateDTO) {
        WalletEntity entity = new WalletEntity(
                null,
                walletCreateDTO.getTypeDocumentIdentification(),
                walletCreateDTO.getNumberIdentification(),
                walletCreateDTO.getNumberPhone(),
                walletCreateDTO.getImeiPhone(),
                walletCreateDTO.getEmail(),
                walletCreateDTO.getCardDebitAssociate(),
                walletCreateDTO.getTotalBalance());
        return entity;
    }
}
