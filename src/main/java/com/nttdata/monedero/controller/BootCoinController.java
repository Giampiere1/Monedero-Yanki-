package com.nttdata.monedero.controller;

import com.nttdata.monedero.domain.dto.BootCoinCreateDTO;
import com.nttdata.monedero.domain.dto.WalletCreateDTO;
import com.nttdata.monedero.domain.services.BootCoinService;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.producer.KafkaStringProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/bootCoin")
@Slf4j
public class BootCoinController {

    private final KafkaStringProducer kafkaStringProducer;
    @Autowired
    private BootCoinService bootCoinService;
    @Autowired
    BootCoinController(KafkaStringProducer kafkaStringProducer) {
        this.kafkaStringProducer = kafkaStringProducer;
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<Mono<BootCoinCreateDTO>> create(@RequestBody BootCoinCreateDTO wallet) {
        return new ResponseEntity<>(bootCoinService.create(wallet)
                .doOnSubscribe(unused -> log.info("create:: iniciando"))
                .doOnError(throwable -> log.error("create:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("create:: finalizado con exito")), HttpStatus.CREATED);
    }
}
