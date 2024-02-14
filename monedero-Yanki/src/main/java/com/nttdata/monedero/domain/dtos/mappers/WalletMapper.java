package com.nttdata.monedero.domain.dtos.mappers;

import org.springframework.stereotype.Component;

import com.nttdata.monedero.domain.dtos.MovementCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletCreateDTO;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;

@Component
public class WalletMapper {

    public WalletEntity toEntity(WalletCreateDTO dto) {
        WalletEntity entity = new WalletEntity(
                null,
                dto.getTypeDocumentIdentification(),
                dto.getNumberIdentification(),
                dto.getNumberPhone(),
                dto.getImeiPhone(),
                dto.getEmail(),
                dto.getCardDebitAssociate(),
                dto.getTotalBalance());
        return entity;
    }

    public MovementEntity toEntity(MovementCreateDTO dto) {
        MovementEntity entity = new MovementEntity(
                null,
                dto.getWalletId(),
                dto.getDestinationFor(),
                dto.getDateMovement(),
                dto.getAmount());
        return entity;
    }

}
