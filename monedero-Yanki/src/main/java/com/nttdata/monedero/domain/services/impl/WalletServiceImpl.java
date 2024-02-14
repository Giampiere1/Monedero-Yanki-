package com.nttdata.monedero.domain.services.impl;

import static com.nttdata.monedero.domain.util.Constantes.EX_NOT_FOUND_RECURSO;
import static com.nttdata.monedero.domain.util.Constantes.VALUE_INIT_CREATE_WALLET;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;

import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.domain.dtos.MovementCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletDTO;
import com.nttdata.monedero.domain.dtos.mappers.WalletMapper;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.domain.util.ContextInsert;
import com.nttdata.monedero.domain.util.ContextValidation;
import com.nttdata.monedero.domain.util.ErrorResponseException;
import com.nttdata.monedero.domain.util.InsertDebitCard;
import com.nttdata.monedero.domain.util.InsertDocument;
import com.nttdata.monedero.domain.util.Utilitarios;
import com.nttdata.monedero.domain.util.ValidateDebitCard;
import com.nttdata.monedero.domain.util.ValidateDocument;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.nttdata.monedero.domain.util.MovementValidation.validateCentralFacade;

public class WalletServiceImpl implements WalletService {

    @Autowired()
    private WalletMapper walletMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaConsumerListener kafkaConsumerListener;

    @Autowired
    @Qualifier("walletReactiveRedisTemplate")
    private ReactiveRedisTemplate<String, WalletEntity> redisTemplate;

    @Override
    public Mono<Void> create(WalletCreateDTO wallet) {
        WalletEntity walletEntity = walletMapper.toEntity(wallet);
        ContextValidation ctxValidation = null;
        if (!Objects.isNull(wallet.getCardDebitAssociate())) {
            ctxValidation = new ContextValidation(new ValidateDebitCard());
        } else {
            ctxValidation = new ContextValidation(new ValidateDocument());
        }
        return ctxValidation.executeValidation(walletEntity, walletRepository, kafkaTemplate, kafkaConsumerListener)
                .then(Mono.just(walletEntity))
                .flatMap(walletMono -> {
                    walletMono.setId(Utilitarios.generateUuid());
                    walletMono.setTotalBalance(BigDecimal.valueOf(VALUE_INIT_CREATE_WALLET));
                    return walletRepository.save(walletMono);
                }).then();
    }

    @Override
    public Mono<Void> delete(String id) {
        return getDetail(id)
                .flatMap(wallet -> walletRepository.delete(wallet))
                .then();
    }

    @Override
    public Flux<WalletEntity> getList() {
        String cacheKey = "wallets";
        Duration cacheDuration = Duration.ofMinutes(5);
        return redisTemplate.opsForList().range(cacheKey, 0, -1)
                .switchIfEmpty(walletRepository.findAll()
                        .flatMap(wallet -> redisTemplate.opsForList().leftPushAll(cacheKey, wallet)
                                .thenMany(Flux.just(wallet))))
                .cache(cacheDuration)
                .doOnSubscribe(subscription -> redisTemplate.expire(cacheKey, cacheDuration).subscribe());
    }

    @Override
    public Mono<WalletEntity> getDetail(String id) {
        return walletRepository.findById(id)
                .switchIfEmpty(Mono.error(new ErrorResponseException(EX_NOT_FOUND_RECURSO,
                        HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<Void> createTransaction(MovementCreateDTO movement) {
        MovementEntity movementEntity = walletMapper.toEntity(movement);
        return validateCentralFacade(movementEntity, walletRepository)
                .then(getDetail(movement.getWalletId()))
                .flatMap(walletFlujo -> {
                    ContextInsert ctxInsert = null;
                    if (walletFlujo.getCardDebitAssociate() == null) {
                        ctxInsert = new ContextInsert(new InsertDocument());
                    } else {
                        ctxInsert = new ContextInsert(new InsertDebitCard());
                    }
                    return ctxInsert.executeInsert(walletFlujo, movementEntity, movementRepository,
                            walletRepository, kafkaTemplate, kafkaConsumerListener);
                });
    }

}
