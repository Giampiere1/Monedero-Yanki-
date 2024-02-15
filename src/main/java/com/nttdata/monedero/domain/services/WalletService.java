package com.nttdata.monedero.domain.services;

import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import reactor.core.publisher.Mono;

public interface WalletService {

    Mono<WalletCreateDTO> create(WalletCreateDTO wallet);

    Mono<Void> delete(String id);
    Mono<WalletCreateDTO> getDetail(String id);
}
