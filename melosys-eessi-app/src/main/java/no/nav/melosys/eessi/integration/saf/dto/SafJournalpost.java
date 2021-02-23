package no.nav.melosys.eessi.integration.saf.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class SafJournalpost {

    private List<Tilleggsopplysning> tilleggsopplysninger = new ArrayList<>();

    public Optional<String> hentRinaSakId() {
        return tilleggsopplysninger.stream()
                .filter(t -> "rinaSakId".equals(t.getNokkel()))
                .map(Tilleggsopplysning::getVerdi)
                .findFirst();
    }
}
