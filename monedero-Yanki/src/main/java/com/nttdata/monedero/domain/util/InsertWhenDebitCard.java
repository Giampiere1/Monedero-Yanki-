package com.nttdata.monedero.domain.util;

import org.springframework.kafka.core.KafkaTemplate;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

/**
 * Clase abstracta de la estrategia.
 */
public abstract class InsertWhenDebitCard implements IStrategyInsertMovement {
  @Override
  public Mono<Void> insert(WalletEntity wallet, MovementEntity movementWallet,
      MovementRepository movementWalletRepository,
      WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate,
      KafkaConsumerListener kafkaConsumerListener) {
    return verifyBalanceSuficient(wallet, movementWallet, kafkaTemplate, kafkaConsumerListener)
        .then(insertMovement(movementWallet, movementWalletRepository))
        .then(updateWalletOrigin(wallet, movementWallet, kafkaTemplate))
        .then(updateWalletDestin(wallet, movementWallet, walletRepository, kafkaTemplate));
  }

  abstract Mono<Void> verifyBalanceSuficient(WalletEntity wallet, MovementEntity movementWallet,
      KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener);

  abstract Mono<Void> insertMovement(MovementEntity movementWallet, MovementRepository movementWalletRepository);

  abstract Mono<Void> updateWalletOrigin(WalletEntity wallet, MovementEntity movementWallet,
      KafkaTemplate<String, String> kafkaTemplate);

  abstract Mono<Void> updateWalletDestin(WalletEntity wallet, MovementEntity movementWallet,
      WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate);
}
