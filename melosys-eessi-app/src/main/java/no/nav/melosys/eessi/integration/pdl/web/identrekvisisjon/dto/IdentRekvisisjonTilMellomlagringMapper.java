package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import no.nav.melosys.eessi.integration.pdl.dto.PDLKjoennType;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Kjønn;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

import static no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.finnLandkodeIso3;

public class IdentRekvisisjonTilMellomlagringMapper {

    public static IdentRekvisisjonTilMellomlagring byggIdentRekvisisjonTilMellomlagring(SedMottattHendelse sedMottattHendelse, SED sed) {
        var personFraSed = sed.finnPerson().orElseThrow();

        var identRekvisjonTilMellomlagringBuilder = IdentRekvisisjonTilMellomlagring.builder()
            .kilde(
                IdentRekvisisjonKilde.builder()
                    .institusjon(sedMottattHendelse.getSedHendelse().getAvsenderNavn())
                    .landkode(Objects.requireNonNull(
                        finnLandkodeIso3(sedMottattHendelse.getSedHendelse().getLandkode(), true)
                        , "Landkode kan ikke være null fra SED"))
                    .build()
            )
            .personopplysninger(
                IdentRekvisisjonPersonopplysninger.builder()
                    .fornavn(personFraSed.getFornavn())
                    .etternavn(personFraSed.getEtternavn())
                    .foedselsdato(LocalDate.parse(personFraSed.getFoedselsdato()))
                    .kjoenn(hentPDLKjønn(personFraSed))
                    .foedeland(personFraSed.getFoedested() != null ? finnLandkodeIso3(personFraSed.getFoedested().getLand(), true) : null)
                    .foedested(personFraSed.getFoedested() != null ? personFraSed.getFoedested().getBy() : null)
                    .statsborgerskap(
                        personFraSed.getStatsborgerskap()
                            .stream()
                            .map(Statsborgerskap::getLand)
                            .map(land -> finnLandkodeIso3(land, true))
                            .collect(Collectors.toSet()))
                    .build()
            )
            .kontaktadresse(
                IdentRekvisisjonKontaktadresse.builder()
                    .utenlandskVegadresse(hentUtenlandskVegadresse(sed))
                    .build()
            );

        var pinSEDErFraLandSedKommerFra = personFraSed.getPin().stream().filter(a -> a.getLand().equals(sedMottattHendelse.getSedHendelse().getLandkode())).findFirst();
        pinSEDErFraLandSedKommerFra
            .ifPresent(pin -> identRekvisjonTilMellomlagringBuilder.utenlandskIdentifikasjon(
                IdentRekvisisjonUtenlandskIdentifikasjon
                    .builder()
                    .utstederland(finnLandkodeIso3(pin.getLand(), true))
                    .utenlandskId(pin.getIdentifikator())
                    .build()));

        return identRekvisjonTilMellomlagringBuilder.build();
    }

    private static IdentRekvisisjonUtenlandskVegadresse hentUtenlandskVegadresse(SED sed) {
        var adresse = sed.getNav().getBruker().getAdresse() != null ? sed.getNav().getBruker().getAdresse().get(0) : null;
        if (adresse != null) {
            return IdentRekvisisjonUtenlandskVegadresse.builder()
                .adressenavnNummer(adresse.getGate())
                .bygningEtasjeLeilighet(adresse.getBygning())
                .postkode(adresse.getPostnummer())
                .bySted(adresse.getBy())
                .regionDistriktOmraade(adresse.getRegion())
                .landkode(finnLandkodeIso3(adresse.getLand(), true))
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
