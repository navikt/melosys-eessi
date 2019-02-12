package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import no.nav.dokarkivsed.api.v1.ArkivSak;
import no.nav.dokarkivsed.api.v1.ForsendelsesInformasjon;
import no.nav.dokarkivsed.api.v1.Organisasjon;
import no.nav.dokarkivsed.api.v1.Person;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.integration.gsak.Sak;

public class OpprettUtgaaendeJournalpostMapper {

    private static final String JOURNALF_ENHET = "4530";
    private static final String REFERANSE_TYPE = "SED_FLYT";
    private static final String UTSENDINGSKANAL = "EESSI";//?????
    private static final String HOVEDDOKUMENT = "HOVEDDOKUMENT";
    private static final String SERVICE_ID = "melosys-app";

    //direkte fra eux-fagmodul-journalfoering
    private static final String GOSYS_ARKIVSAKSYSTEM = "FS22";



    public ForsendelsesInformasjon createForsendelse(String aktoerId, SedSendt sedSendt, Sak sak,
            ReceiverInfo mottaker) {

        return ForsendelsesInformasjon.builder()
                .arkivSak(sak != null ?
                        ArkivSak.builder()
                                .arkivSakId(sak.getId())
                                .arkivSakSystem(GOSYS_ARKIVSAKSYSTEM)
                                .build() : null)
                .mottaker(Organisasjon.builder()
                        .navn(mottaker != null ?
                                mottaker.getName()
                                : "Ikke tilgjengelig") //TODO: er dette riktig?
                        .orgnummer(mottaker != null ?
                                mottaker.getId()
                                : "Ikke tilgjengelig") //TODO: er dette riktig?
                        .build())
                .bruker(aktoerId != null ?
                        Person.builder().aktoerId(aktoerId).build() : null)
                .bucId(sedSendt.getRinaSakId())
                .kanalreferanseId(sedSendt.getSedId())
                .tema(Optional.ofNullable(sak).map(Sak::getTema)
                        .orElse("MED"))
                .build();
    }
}