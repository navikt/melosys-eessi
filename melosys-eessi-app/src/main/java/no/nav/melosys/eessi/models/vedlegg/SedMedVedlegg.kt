package no.nav.melosys.eessi.models.vedlegg;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SedMedVedlegg {

  private final BinaerFil sed;
  private List<BinaerFil> vedlegg;

  public SedMedVedlegg(BinaerFil sed, List<BinaerFil> vedlegg) {
    this.sed = sed;
    this.vedlegg = vedlegg != null ? vedlegg : Collections.emptyList();
  }

  @Value
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class BinaerFil {

    private String filnavn;
    private String mimeType;
    private byte[] innhold;

    public BinaerFil(String filnavn, String mimeType, byte[] innhold) {
      this.filnavn = filnavn;
      this.mimeType = mimeType;
      this.innhold = innhold;
    }
  }
}
