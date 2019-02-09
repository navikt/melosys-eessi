package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SedType;

public class SedUtils {

  public static SedType getSedTypeFromBestemmelse(Bestemmelse bestemmelse) {

    switch (bestemmelse) {

      case ART_11_1:
      case ART_11_3_a:
      case ART_11_3_b:
      case ART_11_3_c:
      case ART_11_3_d:
      case ART_11_3_e:
      case ART_11_4_2:
        break;
      case ART_12_1:
      case ART_12_2:
        return SedType.A009;
      case ART_13_1_a:
      case ART_13_1_b_1:
      case ART_13_1_b_2:
      case ART_13_1_b_3:
      case ART_13_1_b_4:
      case ART_13_2_a:
      case ART_13_2_b:
      case ART_13_3:
      case ART_13_4:
        break;
      case ART_16_1:
      case ART_16_2:
        break;
    }

    throw new RuntimeException("Lovvalgsbestemmelse " + bestemmelse.name() + " er ikke støttet enda!");
  }

  public static BucType getBucTypeFromBestemmelse(Bestemmelse bestemmelse) {

    switch (bestemmelse) {

      case ART_11_1:
      case ART_11_3_a:
      case ART_11_3_b:
      case ART_11_3_c:
      case ART_11_3_d:
      case ART_11_3_e:
      case ART_11_4_2:
        break;
      case ART_12_1:
      case ART_12_2:
        return BucType.LA_BUC_04;
      case ART_13_1_a:
      case ART_13_1_b_1:
      case ART_13_1_b_2:
      case ART_13_1_b_3:
      case ART_13_1_b_4:
      case ART_13_2_a:
      case ART_13_2_b:
      case ART_13_3:
      case ART_13_4:
        break;
      case ART_16_1:
      case ART_16_2:
        break;
    }
    throw new RuntimeException("Bestemmelse " + bestemmelse.name() + " er ikke støttet enda!");
  }
}

