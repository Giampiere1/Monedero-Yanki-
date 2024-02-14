package com.nttdata.monedero.domain.util;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

public abstract class InsertWhenDocument implements IStrategyInsertMovement {
  @Override
  public Mono<Void> insert(WalletEntity wallet, MovementEntity movementWallet,
      MovementRepository movementWalletRepository,
      WalletRepository walletRepository,
      KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener) {
    return verifyBalanceSuficien(wallet, movementWallet)
        .then(insertMovement(movementWallet, movementWalletRepository))
        .then(updateWalletOrigin(wallet, movementWallet, walletRepository))
        .then(updateWalletDestin(wallet, movementWallet, walletRepository, kafkaTemplate));
  }

  abstract Mono<Void> verifyBalanceSuficien(WalletEntity wallet, MovementEntity movementWallet);

  abstract Mono<Void> insertMovement(MovementEntity movementWallet, MovementRepository movementWalletRepository);

  abstract Mono<Void> updateWalletOrigin(WalletEntity wallet, MovementEntity movementWallet,
      WalletRepository walletRepository);

  abstract Mono<Void> updateWalletDestin(WalletEntity wallet, MovementEntity movementWallet,
      WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate);
}
