package no.nav.melosys.eessi.service.dokkat;

import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DokkatServiceTest {

    @Mock
    private DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    @Mock
    private DokumenttypeIdConsumer dokumenttypeIdConsumer;

    private DokkatService dokkatService;

    private final String sedType = "A009";
    private final String dokumentTypeId = "sed-123";

    @BeforeEach
    public void setup() throws Exception {
        dokkatService = new DokkatService(dokumenttypeIdConsumer, dokumenttypeInfoConsumer);

        DokumenttypeIdDto dokumenttypeIdTo = new DokumenttypeIdDto();
        dokumenttypeIdTo.setDokumenttypeId(dokumentTypeId);
        when(dokumenttypeIdConsumer.hentDokumenttypeId(eq(sedType), anyString())).thenReturn(dokumenttypeIdTo);

        DokumentTypeInfoDto dokumentTypeInfoToV4 = DokumentTypeInfoDto.builder()
                .dokumentType("type")
                .dokumentKategori("kategori")
                .dokumentTittel("tittel")
                .arkivSystem("arkivSystem")
                .artifaktId("artifaktId")
                .behandlingstema("behandlingstema")
                .dokumenttypeId(dokumentTypeId)
                .tema("tema")
                .build();
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(dokumentTypeId)).thenReturn(dokumentTypeInfoToV4);
    }

    @Test
    void hentMetaDataFraDokkat_expectDokkatSedInfo() {
        DokkatSedInfo dokkatSedInfo = dokkatService.hentMetadataFraDokkat(sedType);

        assertThat(dokkatSedInfo).isNotNull();
        assertThat(dokkatSedInfo.getBehandlingstema()).isEqualTo("behandlingstema");
        assertThat(dokkatSedInfo.getDokumenttypeId()).isEqualTo(dokumentTypeId);
    }
}
