package no.nav.melosys.eessi.integration.pdl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import no.nav.melosys.eessi.service.tps.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsokKriterier;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.integration.pdl.dto.HarMetadata.hentSisteOpplysning;
import static no.nav.melosys.eessi.integration.pdl.dto.PDLSokCriterion.*;

@Component
public class PDLService implements PersonFasade {

    private final PDLConsumer pdlConsumer;
    private final PersonSokMetrikker personSokMetrikker;

    public PDLService(PDLConsumer pdlConsumer, PersonSokMetrikker personSokMetrikker) {
        this.pdlConsumer = pdlConsumer;
        this.personSokMetrikker = personSokMetrikker;
    }

    @Override
    public PersonModell hentPerson(String ident) {
        var pdlPerson = pdlConsumer.hentPerson(ident);

        var personModellBuilder = PersonModell.builder().ident(ident);

        hentSisteOpplysning(pdlPerson.getNavn()).ifPresent(navn -> {
            personModellBuilder.fornavn(navn.getFornavn());
            personModellBuilder.etternavn(navn.getEtternavn());
        });

        hentSisteOpplysning(pdlPerson.getFoedsel())
                .ifPresent(fødsel -> personModellBuilder.fødselsdato(fødsel.getFoedselsdato()));
        hentSisteOpplysning(pdlPerson.getFolkeregisterpersonstatus())
                .ifPresent(status -> personModellBuilder.erOpphørt(status.statusErOpphørt()));
        personModellBuilder.statsborgerskapLandkodeISO2(
                pdlPerson.getStatsborgerskap().stream()
                .map(PDLStatsborgerskap::getLand)
                .map(LandkodeMapper::getLandkodeIso2)
                .collect(Collectors.toSet())
        );

        return personModellBuilder.build();
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
                .peek(res -> personSokMetrikker.registrerAntallTreffPDL(res.getIdenter().size()))
                .map(PDLSokHit::getIdenter)
                .map(this::hentFolkeregisterIdent)
                .map(PersonSokResponse::new)
                .collect(Collectors.toList());
    }

    private String hentFolkeregisterIdent(Collection<PDLIdent> pdlIdenter) {
        return pdlIdenter.stream()
                .filter(PDLIdent::erFolkeregisterIdent)
                .findFirst()
                .map(PDLIdent::getIdent)
                .orElseThrow();
    }
}
