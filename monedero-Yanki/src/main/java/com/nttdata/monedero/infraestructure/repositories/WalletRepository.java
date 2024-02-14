package com.nttdata.monedero.infraestructure.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.monedero.infraestructure.entities.WalletEntity;

import reactor.core.publisher.Mono;

public interface WalletRepository extends ReactiveMongoRepository<WalletEntity, String> {

    Mono<WalletEntity> findByNumberPhone(String numberPhone);

}
