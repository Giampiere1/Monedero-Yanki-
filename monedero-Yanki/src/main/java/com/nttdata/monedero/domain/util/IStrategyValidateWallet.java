package com.nttdata.monedero.domain.util;

import org.springframework.kafka.core.KafkaTemplate;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

public interface IStrategyValidateWallet {
    Mono<Void> validate(WalletEntity wallet, WalletRepository walletRepository,
            KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener);
}
