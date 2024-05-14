package no.nav.melosys.eessi.integration.eux.rina_api.dto;

public record EuxMelosysSedOppdateringDto(String rinasakId,
                                          String sedId,
                                          Integer sedVersjon,
                                          SedJournalstatus sedJournalstatus) {
}
