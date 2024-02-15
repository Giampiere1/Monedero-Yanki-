package com.nttdata.monedero.controller;

import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.producer.KafkaStringProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/kafka")
@Slf4j
public class KafkaController {

    private final KafkaStringProducer kafkaStringProducer;
    @Autowired
    private WalletService walletService;
    @Autowired
    KafkaController(KafkaStringProducer kafkaStringProducer) {
        this.kafkaStringProducer = kafkaStringProducer;
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<Mono<WalletCreateDTO>> create(@RequestBody WalletCreateDTO wallet) {
        return new ResponseEntity<>(walletService.create(wallet)
                .doOnSubscribe(unused -> log.info("create:: iniciando"))
                .doOnError(throwable -> log.error("create:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("create:: finalizado con exito")), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Mono<Void>> delete(@PathVariable("id") String walletId) {
        return new ResponseEntity<>(walletService.delete(walletId)
                .doOnSubscribe(unused -> log.info("delete:: iniciando"))
                .doOnError(throwable -> log.error("delete:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("delete:: finalizado con exito")), HttpStatus.CREATED);
    }
}
