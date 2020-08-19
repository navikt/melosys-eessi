package no.nav.melosys.eessi.service.dokkat;

import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("metadataDokkat")
    public DokkatSedInfo hentMetadataFraDokkat(String sedType) {
        DokumenttypeIdDto dokumenttypeIdTo = dokumenttypeIdConsumer
            .hentDokumenttypeId(sedType, EKSTERN_ID_TYPE_SED);
        DokumentTypeInfoDto dokumentTypeInfoDto = dokumenttypeInfoConsumer
            .hentDokumenttypeInfo(dokumenttypeIdTo.getDokumenttypeId());
        return map(dokumentTypeInfoDto);
    }

    private DokkatSedInfo map(DokumentTypeInfoDto dokumentTypeInfoDto) {
        DokkatSedInfo dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentKategori(dokumentTypeInfoDto.getDokumentKategori());
        dokkatSedInfo.setDokumentTittel(dokumentTypeInfoDto.getDokumentTittel());
        dokkatSedInfo.setDokumenttypeId(dokumentTypeInfoDto.getDokumenttypeId());
        dokkatSedInfo.setBehandlingstema(dokumentTypeInfoDto.getBehandlingstema());
        dokkatSedInfo.setTema(dokumentTypeInfoDto.getTema());

        return dokkatSedInfo;
    }
}

