package com.nttdata.monedero.domain.util;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

/**
 * Context de la estrategia.
 */
public class ContextInsert {
  private IStrategyInsertMovement iStrategyInsertMovement;

  public ContextInsert(IStrategyInsertMovement iStrategyInsertMovement) {
    this.iStrategyInsertMovement = iStrategyInsertMovement;
  }

  public Mono<Void> executeInsert(WalletEntity wallet, MovementEntity movementWallet,
      MovementRepository movementWalletRepository,
      WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate,
      KafkaConsumerListener kafkaConsumerListener) {
    return iStrategyInsertMovement.insert(wallet, movementWallet,
        movementWalletRepository, walletRepository, kafkaTemplate, kafkaConsumerListener);
  }
}
