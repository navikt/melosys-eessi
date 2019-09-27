package no.nav.melosys.eessi.service.sed;

import java.util.Collection;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.repository.SedMottattRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SedMottattService {

    private final SedMottattRepository sedMottattRepository;

    public SedMottattService(SedMottattRepository sedMottattRepository) {
        this.sedMottattRepository = sedMottattRepository;
    }

    @Transactional
    public void lagre(SedMottatt sedMottatt) {
        sedMottattRepository.save(sedMottatt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Collection<SedMottatt> hentAlleUbehandlet() {
        return sedMottattRepository.findAllByFerdigFalseAndFeiledeForsokLessThan(5);
    }
}
