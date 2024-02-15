package com.nttdata.monedero.infraestructure.repositories;

import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface WalletRepository extends ReactiveMongoRepository<WalletCreateDTO, String> {


}
