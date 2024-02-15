package com.nttdata.monedero.domain.dto.mappers;

import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

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
