package com.nttdata.monedero.domain.util;

import org.springframework.kafka.core.KafkaTemplate;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

public interface IStrategyInsertMovement {
  Mono<Void> insert(WalletEntity wallet, MovementEntity movementWallet,
      MovementRepository movementWalletRepository, WalletRepository walletRepository,
      KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener);
}
