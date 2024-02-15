package com.nttdata.monedero.infraestructure.repositories;

import com.nttdata.monedero.domain.dto.BootCoinCreateDTO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BootCoinRepository extends ReactiveMongoRepository<BootCoinCreateDTO, String> {
}
