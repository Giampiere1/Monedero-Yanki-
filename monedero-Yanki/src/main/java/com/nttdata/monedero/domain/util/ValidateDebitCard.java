package com.nttdata.monedero.domain.util;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;

import com.google.gson.Gson;
import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import reactor.core.publisher.Mono;

import static com.nttdata.monedero.domain.util.Constantes.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ValidateDebitCard extends ValidateWhenDebitCard {

  @Override
  public Mono<Void> validateWalletNoNulls(WalletEntity wallet) {
    return Mono.just(wallet)
        .filter(c -> c.getTypeDocumentIdentification() != null)
        .filter(c -> c.getNumberIdentification() != null)
        .filter(c -> c.getNumberPhone() != null)
        .filter(c -> c.getImeiPhone() != null)
        .filter(c -> c.getEmail() != null)
        .filter(c -> c.getCardDebitAssociate() != null)
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
        .filter(c -> !c.getCardDebitAssociate().isBlank())
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

  @Override
  public Mono<Void> verifyExistCardDeb(WalletEntity wallet, KafkaTemplate<String, String> kafkaTemplate,
      KafkaConsumerListener kafkaConsumerListener) {
    Map<String, String> request = new HashMap<>();
    request.put("cardDebitAssociate", wallet.getCardDebitAssociate());
    Gson gson = new Gson();
    String customerIdJson = gson.toJson(request);
    kafkaTemplate.send("verify-carddeb-exist", customerIdJson);

    return kafkaConsumerListener.getDebCardVerificationResponseSink()
        .delayElement(Duration.ofSeconds(1))
        .flatMap(s -> {
          if (s.contains("error")) {
            return Mono.error(new ErrorResponseException(EX_NOT_FOUND_RECURSO_CARD_DEB,
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
          } else {
            return Mono.empty();
          }
        });

  }
}
