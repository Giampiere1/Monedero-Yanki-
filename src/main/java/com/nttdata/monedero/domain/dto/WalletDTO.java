package com.nttdata.monedero.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {

    private String id;

    private String typeDocumentIdentification;

    private String numberIdentification;

    private String numberPhone;

    private String imeiPhone;

    private String email;

    private String cardDebitAssociate;

}
