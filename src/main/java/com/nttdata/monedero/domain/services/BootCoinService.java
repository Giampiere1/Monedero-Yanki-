package com.nttdata.monedero.domain.services;

import com.nttdata.monedero.domain.dto.BootCoinCreateDTO;
import reactor.core.publisher.Mono;

public interface BootCoinService {

    Mono<BootCoinCreateDTO> create(BootCoinCreateDTO bootCoinCreateDTO);

}
