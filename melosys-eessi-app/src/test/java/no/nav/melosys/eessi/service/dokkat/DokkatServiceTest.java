package no.nav.melosys.eessi.service.dokkat;

import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokkatServiceTest {

    @Mock
    private DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    @Mock
    private DokumenttypeIdConsumer dokumenttypeIdConsumer;

    @InjectMocks
    private DokkatService dokkatService;

    private final String sedType = "A009";
    private final String dokumentTypeId = "sed-123";

    @Before
    public void setup() throws Exception {
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
        when(dokumenttypeInfoConsumer.hentDokumenttypeInfo(eq(dokumentTypeId))).thenReturn(dokumentTypeInfoToV4);
    }

    @Test
    public void hentMetaDataFraDokkat_expectDokkatSedInfo() throws Exception {
        DokkatSedInfo dokkatSedInfo = dokkatService.hentMetadataFraDokkat(sedType);

        assertThat(dokkatSedInfo).isNotNull();
        assertThat(dokkatSedInfo.getBehandlingstema()).isEqualTo("behandlingstema");
        assertThat(dokkatSedInfo.getDokumenttypeId()).isEqualTo(dokumentTypeId);
    }

}