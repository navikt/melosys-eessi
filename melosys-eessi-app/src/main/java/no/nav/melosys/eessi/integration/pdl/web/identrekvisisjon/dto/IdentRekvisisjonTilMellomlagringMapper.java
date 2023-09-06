package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import no.nav.melosys.eessi.integration.pdl.dto.PDLKjoennType;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Kjønn;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdentRekvisisjonTilMellomlagringMapper {

    public static IdentRekvisisjonTilMellomlagring byggIdentRekvisisjonTilMellomlagring(SedMottattHendelse sedMottattHendelse, SED sed) {
        var personFraSed = sed.finnPerson().orElseThrow();

        var identRekvisjonTilMellomlagringBuilder = IdentRekvisisjonTilMellomlagring.builder()
            .kilde(
                IdentRekvisisjonKilde.builder()
                    .institusjon(sedMottattHendelse.getSedHendelse().getAvsenderId())
                    .landkode(Objects.requireNonNull(sedMottattHendelse.getSedHendelse().getLandkode(), "Landkode kan ikke være null fra SED"))
                    .build()
            )
            .personopplysninger(
                IdentRekvisisjonPersonopplysninger.builder()
                    .fornavn(personFraSed.getFornavn())
                    .etternavn(personFraSed.getEtternavn())
                    .foedselsdato(LocalDate.parse(personFraSed.getFoedselsdato()))
                    .kjoenn(hentPDLKjønn(personFraSed))
                    .foedeland(personFraSed.getFoedested() != null ? personFraSed.getFoedested().getLand() : null)
                    .foedested(personFraSed.getFoedested() != null ? personFraSed.getFoedested().getBy() : null)
                    .statsborgerskap(personFraSed.getStatsborgerskap().stream().map(Statsborgerskap::getLand).collect(Collectors.toSet()))
                    .build()
            )
            .kontaktadresse(
                IdentRekvisisjonKontaktadresse.builder()
                    .utenlandskPostboksadresse(hentUtenlandskPostAdresse(sed))
                    .build()
            );

        var pinSEDErFraLandSedKommerFra = personFraSed.getPin().stream().filter(a -> a.getLand().equals(sedMottattHendelse.getSedHendelse().getLandkode())).findFirst();
        pinSEDErFraLandSedKommerFra
            .ifPresent(pin -> identRekvisjonTilMellomlagringBuilder.utenlandskIdentifikasjon(
                IdentRekvisisjonUtenlandskIdentifikasjon
                    .builder()
                    .utstederland(pin.getLand())
                    .utenlandskId(pin.getIdentifikator())
                    .build()));

        return identRekvisjonTilMellomlagringBuilder.build();
    }

    private static IdentRekvisisjonUtenlandskPostboksadresse hentUtenlandskPostAdresse(SED sed) {
        var adresse = sed.getNav().getBruker().getAdresse() != null ? sed.getNav().getBruker().getAdresse().get(0) : null;
        if (adresse != null) {
            return IdentRekvisisjonUtenlandskPostboksadresse.builder()
                .postkode(adresse.getPostnummer())
                .bySted(adresse.getBy())
                .landkode(adresse.getLand())
                .regionDistriktOmraade(adresse.getRegion())
                .build();
        }

        return null;
    }

    private static PDLKjoennType hentPDLKjønn(Person personFraSed) {
        if (personFraSed.getKjoenn() == Kjønn.K) {
            return PDLKjoennType.KVINNE;
        } else if (personFraSed.getKjoenn() == Kjønn.M) {
            return PDLKjoennType.MANN;
        }
        return PDLKjoennType.UKJENT;

    }
}
