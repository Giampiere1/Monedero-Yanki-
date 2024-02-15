package com.nttdata.monedero.domain.services.impl;


import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.domain.dto.mappers.WalletMapper;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;
import com.nttdata.monedero.producer.KafkaStringProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static ch.qos.logback.core.util.AggregationType.NOT_FOUND;

@Service
@Component
public class WalletServiceImpl implements WalletService {

    @Autowired()
    private WalletMapper walletMapper;

    @Autowired
    private WalletRepository walletRepository;



    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStringProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public WalletServiceImpl(@Qualifier("kafkaStringTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    @Qualifier("walletReactiveRedisTemplate")
    private ReactiveRedisTemplate<String, WalletEntity> redisTemplate;

    @Override
    public Mono<WalletCreateDTO> create(WalletCreateDTO wallet) {

        this.kafkaTemplate.send("TOPIC", wallet.toString());
        return walletRepository.save(wallet) ;


    }
    @Override
    public Mono<Void> delete(String id) {

        this.kafkaTemplate.send("TOPIC", "Delete"+id);
        return getDetail(id)
                .flatMap(wallet -> walletRepository.delete(wallet))
                .then();
    }
    @Override
    @Cacheable(value = "WalletCache")
    public Mono<WalletCreateDTO> getDetail(String id) {

        this.kafkaTemplate.send("TOPIC", "Detail"+id);
        return walletRepository.findById(id)
                .switchIfEmpty(Mono.error(new Exception(
                )));
    }

}
