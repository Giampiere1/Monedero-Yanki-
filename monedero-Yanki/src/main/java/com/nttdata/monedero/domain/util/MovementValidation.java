package com.nttdata.monedero.domain.util;

import static com.nttdata.monedero.domain.util.Constantes.*;

import org.springframework.http.HttpStatus;

import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

public class MovementValidation {

  public static Mono<Void> validateCentralFacade(MovementEntity movementWallet, WalletRepository walletRepository) {
    return validateMovementNoNulls(movementWallet)
        .then(validateMovementEmpty(movementWallet))
        .then(verifyValues(movementWallet))
        .then(verifyNumberDestination(movementWallet, walletRepository));
  }

  public static Mono<Void> validateMovementNoNulls(MovementEntity movementWallet) {
    return Mono.just(movementWallet)
        .filter(c -> c.getWalletId() != null)
        .filter(c -> c.getDestinationFor() != null)
        .filter(c -> c.getAmount() != null)
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_ERROR_REQUEST,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  public static Mono<Void> validateMovementEmpty(MovementEntity movementWallet) {
    return Mono.just(movementWallet)
        .filter(c -> !c.getWalletId().isEmpty())
        .filter(c -> !c.getDestinationFor().isBlank())
        .filter(c -> !c.getAmount().toString().isBlank())
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_VALUE_EMPTY,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  public static Mono<Void> verifyValues(MovementEntity movementWallet) {
    return Mono.just(movementWallet)
        .filter(c -> c.getAmount().doubleValue() > VALUE_MIN_ACCOUNT_BANK)
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_ERROR_VALUE_MIN,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  public static Mono<Void> verifyNumberDestination(MovementEntity movementWallet, WalletRepository walletRepository) {
    return walletRepository.findByNumberPhone(movementWallet.getDestinationFor())
        .hasElement()
        .flatMap(aBoolean -> {
          if (aBoolean) {
            return Mono.empty();
          } else {
            return Mono.error(new ErrorResponseException(EX_ERROR_NOT_FOUND_DESTINATION, HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND));
          }
        });
  }

}
