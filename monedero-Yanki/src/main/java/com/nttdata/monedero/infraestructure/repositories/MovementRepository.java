package com.nttdata.monedero.infraestructure.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.monedero.infraestructure.entities.MovementEntity;

public interface MovementRepository extends ReactiveMongoRepository<MovementEntity, String> {

}
