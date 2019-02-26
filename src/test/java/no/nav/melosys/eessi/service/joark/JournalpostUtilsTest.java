package no.nav.melosys.eessi.service.joark;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.Aktoer;
import org.junit.Before;
import org.junit.Test;
import static no.nav.melosys.eessi.service.joark.JournalpostUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class JournalpostUtilsTest {

    private JsonNode participants;

    @Before
    public void setup() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL jsonUrl = getClass().getClassLoader().getResource("buc_participants.json");

        participants = objectMapper.readTree(jsonUrl);
    }

    @Test
    public void extractReceiverInformation_expectParticipantInfo() {
        ParticipantInfo receiver = extractReceiverInformation(participants);

        assertThat(receiver, not(nullValue()));
        assertThat(receiver.getId(), is("NO:NAVT003"));
        assertThat(receiver.getName(), is("NAVT003"));
    }

    @Test
    public void extractSenderInformation_expectParticipantInfo() {
        ParticipantInfo sender = extractSenderInformation(participants);

        assertThat(sender, not(nullValue()));
        assertThat(sender.getId(), is("NO:NAVT002"));
        assertThat(sender.getName(), is("NAVT002"));
    }

    @Test
    public void person_expectAktoerWithAdditionalProperties() {
        String ident = "1122334455";

        Aktoer aktoer = person(ident);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("person"));
        assertThat(aktoerEntry.getValue(), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry personEntry = (AbstractMap.SimpleImmutableEntry) aktoerEntry.getValue();
        assertThat(personEntry.getKey(), is("ident"));
        assertThat(personEntry.getValue(), is("1122334455"));
    }

    @Test
    public void organisasjon_expectAktoerWithAdditionalProperties() {
        String navn = "org";
        String orgnr = "5544332211";

        Aktoer aktoer = organisasjon(orgnr, navn);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("org"));
        assertThat(organisasjonEntry.get("orgnr"), is("5544332211"));
    }

    @Test
    public void organisasjon_withNoOrgnr_expectAktoerWithDefaultValues() {
        String navn = "org";

        Aktoer aktoer = organisasjon(null, navn);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("avsender ikke tilgjengelig"));
        assertThat(organisasjonEntry.get("orgnr"), is("avsender ikke tilgjengelig"));
    }

    @Test
    public void organisasjon_withNoOrgnrOrNavn_expectAktoerWithDefaultValues() {
        Aktoer aktoer = organisasjon(null, null);

        assertThat(aktoer, not(nullValue()));
        assertThat(aktoer.getAdditionalProperties(), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), not(nullValue()));
        assertThat(aktoer.getAdditionalProperties().get("aktoer"), instanceOf(AbstractMap.SimpleImmutableEntry.class));

        AbstractMap.SimpleImmutableEntry aktoerEntry = (AbstractMap.SimpleImmutableEntry) aktoer.getAdditionalProperties().get("aktoer");
        assertThat(aktoerEntry.getKey(), is("organisasjon"));
        assertThat(aktoerEntry.getValue(), instanceOf(HashMap.class));

        HashMap organisasjonEntry = (HashMap) aktoerEntry.getValue();
        assertThat(organisasjonEntry.get("navn"), is("avsender ikke tilgjengelig"));
        assertThat(organisasjonEntry.get("orgnr"), is("avsender ikke tilgjengelig"));
    }
}