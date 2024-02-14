package com.nttdata.monedero.domain.util;

import org.springframework.http.HttpStatus;

import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

import static com.nttdata.monedero.domain.util.Constantes.*;

public class ValidateDocument extends ValidateWhenDocument {

  @Override
  public Mono<Void> validateWalletNoNulls(WalletEntity wallet) {
    return Mono.just(wallet)
        .filter(c -> c.getTypeDocumentIdentification() != null)
        .filter(c -> c.getNumberIdentification() != null)
        .filter(c -> c.getNumberPhone() != null)
        .filter(c -> c.getImeiPhone() != null)
        .filter(c -> c.getEmail() != null)
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_ERROR_REQUEST,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  @Override
  public Mono<Void> validateWalletEmpty(WalletEntity wallet) {
    return Mono.just(wallet)
        .filter(c -> !c.getTypeDocumentIdentification().isEmpty())
        .filter(c -> !c.getNumberIdentification().isBlank())
        .filter(c -> !c.getNumberPhone().isBlank())
        .filter(c -> !c.getImeiPhone().isBlank())
        .filter(c -> !c.getEmail().isBlank())
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_VALUE_EMPTY,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  @Override
  public Mono<Void> verifyTypeAccount(WalletEntity wallet) {
    return Mono.just(wallet)
        .filter(c -> {
          String typeAccount = c.getTypeDocumentIdentification();
          return typeAccount.equalsIgnoreCase(TypeDocumentIdentification.DNI.toString())
              || typeAccount.equalsIgnoreCase(TypeDocumentIdentification.CEX.toString())
              || typeAccount.equalsIgnoreCase(TypeDocumentIdentification.PASAPORTE.toString());
        })
        .switchIfEmpty(Mono.error(new ErrorResponseException(EX_ERROR_TYPE_DOCUMENT,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST)))
        .then();
  }

  @Override
  public Mono<Void> verifyDataDuplicated(WalletEntity wallet, WalletRepository walletRepository) {
    return walletRepository.findByNumberPhone(wallet.getNumberPhone())
        .hasElement()
        .flatMap(aBoolean -> {
          if (aBoolean) {
            return Mono.error(new ErrorResponseException(EX_ERROR_PHONE_NUMBER_DUPLICATE,
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT));
          } else {
            return Mono.empty();
          }
        });
  }
}
