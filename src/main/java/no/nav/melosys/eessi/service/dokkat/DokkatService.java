package no.nav.melosys.eessi.service.dokkat;

import no.nav.dokkat.api.tkat020.v4.DokumentTypeInfoToV4;
import no.nav.dokkat.api.tkat022.DokumenttypeIdTo;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DokkatService {

    private static final String EKSTERN_ID_TYPE_SED = "SED_TYPE";
    private final DokumenttypeIdConsumer dokumenttypeIdConsumer;
    private final DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    @Autowired
    public DokkatService(
        DokumenttypeIdConsumer dokumenttypeIdConsumer,
        DokumenttypeInfoConsumer dokumenttypeInfoConsumer) {
        this.dokumenttypeIdConsumer = dokumenttypeIdConsumer;
        this.dokumenttypeInfoConsumer = dokumenttypeInfoConsumer;
    }

    public DokkatSedInfo hentMetadataFraDokkat(String sedType) throws IntegrationException {
        DokumenttypeIdTo dokumenttypeIdTo = dokumenttypeIdConsumer
            .hentDokumenttypeId(sedType, EKSTERN_ID_TYPE_SED);
        DokumentTypeInfoToV4 dokumentTypeInfoToV4 = dokumenttypeInfoConsumer
            .hentDokumenttypeInfo(dokumenttypeIdTo.getDokumenttypeId());
        return map(dokumentTypeInfoToV4);
    }

    private DokkatSedInfo map(DokumentTypeInfoToV4 dokumentTypeInfoToV4) {
        DokkatSedInfo dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentKategori(dokumentTypeInfoToV4.getDokumentKategori());
        dokkatSedInfo.setDokumentTittel(dokumentTypeInfoToV4.getDokumentTittel());
        dokkatSedInfo.setDokumenttypeId(dokumentTypeInfoToV4.getDokumenttypeId());
        dokkatSedInfo.setBehandlingstema(dokumentTypeInfoToV4.getBehandlingstema());
        dokkatSedInfo.setTema(dokumentTypeInfoToV4.getTema());

        return dokkatSedInfo;
    }
}

