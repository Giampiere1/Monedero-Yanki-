package com.nttdata.monedero.domain.services.impl;

import com.nttdata.monedero.domain.dto.BootCoinCreateDTO;
import com.nttdata.monedero.domain.services.BootCoinService;
import com.nttdata.monedero.domain.services.WalletService;
import com.nttdata.monedero.infraestructure.repositories.BootCoinRepository;
import com.nttdata.monedero.producer.KafkaStringProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Component
public class BootCoinServiceImpl implements BootCoinService {



    @Autowired
    private BootCoinRepository bootCoinRepository;



    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStringProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BootCoinServiceImpl(@Qualifier("kafkaStringTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public   Mono<BootCoinCreateDTO> create(BootCoinCreateDTO bootCoinCreateDTO) {

        this.kafkaTemplate.send("TOPIC", bootCoinCreateDTO.toString());
        return bootCoinRepository.save(bootCoinCreateDTO) ;


    }
}
