package no.nav.melosys.eessi.integration.sak;

import lombok.Data;

@Data
public class SakDto {

    private Long id;

    private String tema; // https://kodeverkviewer.adeo.no/kodeverk/xml/fagomrade.xml

    private String applikasjon; // Fagsystemkode for applikasjon

    private String fagsakNr; // Fagsaknr for den aktuelle saken

    private String aktoerId; // Id til aktøren saken gjelder

    private String orgnr; // Orgnr til foretaket saken gjelder

    private String opprettetAv;// Brukerident til den som opprettet saken

    private String opprettetTidspunkt; // Lagres som LocalDateTime i Sak API, men eksponeres som ZonedDateTime
}
