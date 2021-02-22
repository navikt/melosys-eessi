package no.nav.melosys.eessi.integration.common.graphql.response;

import lombok.Data;
import no.nav.melosys.eessi.integration.saf.dto.SafJournalpost;

@Data
public class SafResponse {
    private SafJournalpost query;
}
