package com.nttdata.monedero.domain.services;

import com.nttdata.monedero.domain.dtos.MovementCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletDTO;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

    Mono<Void> create(WalletCreateDTO wallet);

    Mono<Void> delete(String id);

    Flux<WalletEntity> getList();

    Mono<WalletEntity> getDetail(String id);

    Mono<Void> createTransaction(MovementCreateDTO movement);

}
