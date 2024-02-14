package com.nttdata.monedero.domain.util;

import com.google.gson.Gson;
import com.nttdata.monedero.config.KafkaConsumerListener;
import com.nttdata.monedero.infraestructure.entities.MovementEntity;
import com.nttdata.monedero.infraestructure.entities.WalletEntity;
import com.nttdata.monedero.infraestructure.repositories.MovementRepository;
import com.nttdata.monedero.infraestructure.repositories.WalletRepository;

import static com.nttdata.monedero.domain.util.Constantes.EX_ERROR_BALANCE_INSUFICIENT;
import static com.nttdata.monedero.domain.util.Utilitarios.generateUuid;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

/**
 * Estrategia implementada cuando lo asocia a trarjeta de debito.
 */

public class InsertDebitCard extends InsertWhenDebitCard {

  @Override
  Mono<Void> verifyBalanceSuficient(WalletEntity wallet, MovementEntity movementWallet,
      KafkaTemplate<String, String> kafkaTemplate, KafkaConsumerListener kafkaConsumerListener) {
    Map<String, String> requestVerifyBalance = new HashMap<>();
    requestVerifyBalance.put("cardDebitId", wallet.getCardDebitAssociate());
    requestVerifyBalance.put("mount", movementWallet.getAmount().toString());
    Gson gson = new Gson();
    String requestStr = gson.toJson(requestVerifyBalance);
    kafkaTemplate.send("verify-balance-carddeb", requestStr);

    return kafkaConsumerListener.getDebCardVerificationBalanceResponseSink()
        .flatMap(s -> {
          if (s.contains("error")) {
            return Mono.error(new ErrorResponseException(EX_ERROR_BALANCE_INSUFICIENT,
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT));
          } else {
            return Mono.empty();
          }
        });
  }

  @Override
  Mono<Void> insertMovement(MovementEntity movementWallet, MovementRepository movementWalletRepository) {
    movementWallet.setId(generateUuid());
    return movementWalletRepository.save(movementWallet)
        .then();
  }

  @Override
  Mono<Void> updateWalletOrigin(WalletEntity wallet, MovementEntity movementWallet,
      KafkaTemplate<String, String> kafkaTemplate) {
    return Mono.just(wallet)
        .flatMap(walletFlujo -> {
          Map<String, String> requestUpdateMoney = new HashMap<>();
          requestUpdateMoney.put("cardDebitId", walletFlujo.getCardDebitAssociate());
          requestUpdateMoney.put("mount", movementWallet.getAmount().toString());
          requestUpdateMoney.put("type", "decrease");
          Gson gson = new Gson();
          String requestStr = gson.toJson(requestUpdateMoney);
          kafkaTemplate.send("update-amount-carddeb", requestStr);
          return Mono.empty();
        });
  }

  @Override
  Mono<Void> updateWalletDestin(WalletEntity wallet, MovementEntity movementWallet, WalletRepository walletRepository,
      KafkaTemplate<String, String> kafkaTemplate) {
    return Mono.just(wallet)
        .then(walletRepository.findByNumberPhone(movementWallet.getDestinationFor()))
        .flatMap(walletFlujo -> {
          if (walletFlujo.getCardDebitAssociate() == null) {
            walletFlujo.setTotalBalance(walletFlujo.getTotalBalance().add(movementWallet.getAmount()));
            return walletRepository.save(walletFlujo);
          } else {
            Map<String, String> requestUpdateMoney = new HashMap<>();
            requestUpdateMoney.put("cardDebitId", walletFlujo.getCardDebitAssociate());
            requestUpdateMoney.put("mount", movementWallet.getAmount().toString());
            requestUpdateMoney.put("type", "increase");
            Gson gson = new Gson();
            String requestStr = gson.toJson(requestUpdateMoney);
            kafkaTemplate.send("update-amount-carddeb", requestStr);
            return Mono.empty();
          }
        }).then();
  }
}
