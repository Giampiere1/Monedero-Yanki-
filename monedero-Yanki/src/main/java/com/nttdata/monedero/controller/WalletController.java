package com.nttdata.monedero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.nttdata.monedero.domain.dtos.MovementCreateDTO;
import com.nttdata.monedero.domain.dtos.WalletCreateDTO;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/monedero")
@RestController
@Slf4j
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<Mono<Void>> createWallet(@RequestBody WalletCreateDTO wallet, ServerWebExchange exchange) {
        return new ResponseEntity<>(walletService.create(wallet)
                .doOnSubscribe(unused -> log.info("createWallet:: iniciando"))
                .doOnError(throwable -> log.error("createWallet:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("createWallet:: finalizado con exito")), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Mono<Void>> deleteWallet(@PathVariable("id") String walletId, ServerWebExchange exchange) {
        return new ResponseEntity<>(walletService.delete(walletId)
                .doOnSubscribe(unused -> log.info("deleteWallet:: iniciando"))
                .doOnError(throwable -> log.error("deleteWallet:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("deleteWallet:: finalizado con exito")), HttpStatus.CREATED);
    }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Flux<WalletEntity>> getAllWallets(ServerWebExchange exchange) {
        return ResponseEntity.ok(
                walletService.getList()
                        .doOnSubscribe(unused -> log.info("getAllWallets:: iniciando"))
                        .doOnError(throwable -> log.error("getAllWallets:: error " + throwable.getMessage()))
                        .doOnComplete(() -> log.info("getAllWallets:: completadoo")));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Mono<WalletEntity>> getWalletById(@PathVariable("id") String walletId,
            ServerWebExchange exchange) {
        return ResponseEntity.ok(
                walletService.getDetail(walletId)
                        .doOnSubscribe(unused -> log.info("getWalletById:: iniciando"))
                        .doOnError(throwable -> log.error("getWalletById:: error " + throwable.getMessage()))
                        .doOnSuccess((e) -> log.info("getWalletById:: completadoo")));
    }

    @PostMapping("/movement")
    @ResponseBody
    public ResponseEntity<Mono<Void>> createMovementWallet(@RequestBody MovementCreateDTO movementWallet,
            ServerWebExchange exchange) {
        return new ResponseEntity<>(walletService.createTransaction(movementWallet)
                .doOnSubscribe(unused -> log.info("createMovementWallet:: iniciando"))
                .doOnError(throwable -> log.error("createMovementWallet:: error " + throwable.getMessage()))
                .doOnSuccess(ignored -> log.info("createMovementWallet:: finalizado con exito")),
                HttpStatus.CREATED);
    }

}
