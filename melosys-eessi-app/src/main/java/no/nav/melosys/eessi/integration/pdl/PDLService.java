package no.nav.melosys.eessi.integration.pdl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagring;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.service.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.integration.pdl.dto.HarMetadata.hentSisteOpplysning;
import static no.nav.melosys.eessi.integration.pdl.dto.PDLSokCriterion.*;

@Component
public class PDLService implements PersonFasade {

    private final PDLConsumer pdlConsumer;

    private final PdlWebConsumer pdlWebConsumer;

    public PDLService(PDLConsumer pdlConsumer, PdlWebConsumer pdlWebConsumer) {
        this.pdlConsumer = pdlConsumer;
        this.pdlWebConsumer = pdlWebConsumer;
    }

    @Override
    public String opprettLenkeForRekvirering(IdentRekvisisjonTilMellomlagring identRekvisisjonTilMellomlagring) {
        return pdlWebConsumer.opprettLenkeForRekvirering(identRekvisisjonTilMellomlagring);
    }

    @Override
    public PersonModell hentPerson(String ident) {
        var pdlPerson = pdlConsumer.hentPerson(ident);

        var personModellBuilder = PersonModell.builder().ident(ident);

        hentSisteOpplysning(pdlPerson.getNavn()).ifPresent(navn -> {
            personModellBuilder.fornavn(navn.getFornavn());
            personModellBuilder.etternavn(navn.getEtternavn());
        });

        hentSisteOpplysning(pdlPerson.getFoedselsdato())
            .ifPresent(fødsel -> personModellBuilder.fødselsdato(fødsel.getFoedselsdato()));
        hentSisteOpplysning(pdlPerson.getFolkeregisterpersonstatus())
            .ifPresent(status -> personModellBuilder.erOpphørt(status.statusErOpphørt()));
        hentSisteOpplysning(pdlPerson.getKjoenn())
            .ifPresent(kjønn -> personModellBuilder.kjønn(kjønn.getKjoenn().tilDomene()));

        return personModellBuilder
            .statsborgerskapLandkodeISO2(
                pdlPerson.getStatsborgerskap().stream()
                    .map(PDLStatsborgerskap::getLand)
                    .map(LandkodeMapper::mapTilLandkodeIso2)
                    .collect(Collectors.toSet()))
            .utenlandskId(pdlPerson.getUtenlandskIdentifikasjonsnummer()
                .stream()
                .map(p -> new UtenlandskId(p.getIdentifikasjonsnummer(), LandkodeMapper.mapTilLandkodeIso2(p.getUtstederland())))
                .collect(Collectors.toSet()))
            .build();
    }

    @Override
    public String hentAktoerId(String ident) {
        return pdlConsumer.hentIdenter(ident).getIdenter()
            .stream()
            .filter(PDLIdent::erAktørID)
            .findFirst()
            .map(PDLIdent::getIdent)
            .orElseThrow(() -> new NotFoundException("Finner ikke aktørID!"));
    }

    @Override
    public String hentNorskIdent(String aktoerID) {
        return pdlConsumer.hentIdenter(aktoerID).getIdenter()
            .stream()
            .filter(PDLIdent::erFolkeregisterIdent)
            .findFirst()
            .map(PDLIdent::getIdent)
            .orElseThrow(() -> new NotFoundException("Finner ikke folkeregisterident!"));
    }

    @Override
    public List<PersonSokResponse> soekEtterPerson(PersonsokKriterier personsokKriterier) {
        return pdlConsumer.søkPerson(new PDLSokRequestVars(
                new PDLPaging(1, 20),
                Set.of(
                    fornavn().inneholder(personsokKriterier.getFornavn()),
                    etternavn().inneholder(personsokKriterier.getEtternavn()),
                    fødselsdato().erLik(personsokKriterier.getFoedselsdato())
                )))
            .getHits()
            .stream()
            .map(PDLSokHit::getIdenter)
            .map(this::hentFolkeregisterIdent)
            .filter(Objects::nonNull)
            .map(PersonSokResponse::new)
            .collect(Collectors.toList());
    }

    private String hentFolkeregisterIdent(Collection<PDLIdent> pdlIdenter) {
        return pdlIdenter.stream()
            .filter(PDLIdent::erFolkeregisterIdent)
            .findFirst()
            .map(PDLIdent::getIdent)
            .orElse(null);
    }
}
