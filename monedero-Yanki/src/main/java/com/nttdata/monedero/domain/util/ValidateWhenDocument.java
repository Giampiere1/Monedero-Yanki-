package com.nttdata.monedero.domain.util;

import org.springframework.kafka.core.KafkaTemplate;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

public abstract class ValidateWhenDocument implements IStrategyValidateWallet {
  @Override
  public Mono<Void> validate(WalletEntity wallet, WalletRepository walletRepository,
      KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener) {
    return validateWalletNoNulls(wallet)
        .then(validateWalletEmpty(wallet))
        .then(verifyTypeAccount(wallet))
        .then(verifyDataDuplicated(wallet, walletRepository));
  }

  public abstract Mono<Void> validateWalletNoNulls(WalletEntity wallet);

  public abstract Mono<Void> validateWalletEmpty(WalletEntity wallet);

  public abstract Mono<Void> verifyTypeAccount(WalletEntity wallet);

  public abstract Mono<Void> verifyDataDuplicated(WalletEntity wallet, WalletRepository walletRepository);
}
